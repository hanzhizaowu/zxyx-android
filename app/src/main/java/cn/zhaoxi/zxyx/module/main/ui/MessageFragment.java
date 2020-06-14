package cn.zhaoxi.zxyx.module.main.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import cn.zhaoxi.library.base.BaseFragment;
import cn.zhaoxi.library.loadmore.LoadMord;
import cn.zhaoxi.library.loadmore.OnLoadMoreListener;
import cn.zhaoxi.library.view.MyAlertDialog;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.adapter.MessageRecyclerAdapter;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.common.util.Presenter;
import cn.zhaoxi.zxyx.common.util.SPUtil;
import cn.zhaoxi.zxyx.data.dto.ConversationDto;
import cn.zhaoxi.zxyx.databinding.FragmentMessageBinding;
import cn.zhaoxi.zxyx.module.main.viewmodel.MessageViewModel;
import cn.zhaoxi.zxyx.module.message.ui.ChatActivity;

public class MessageFragment extends BaseFragment implements Presenter {
    private FragmentMessageBinding fragmentMessageBinding;
    private Handler handler = new Handler();
    private MessageRecyclerAdapter adapter;
    private List<ConversationDto> data = new ArrayList<>();
    private final int MOD_REFRESH = 1;
    private final int MOD_LOADING = 2;
    private int RefreshMODE = 0;

    public MessageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentMessageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container,false);
        fragmentMessageBinding.setPresenter(this);
        View view = fragmentMessageBinding.getRoot();
        init();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {

    }

    private void init() {
        initRefresh();
        initData();
        onClickItem();
        getUserMsg(0L);
    }

    /*下拉刷新*/
    private void initRefresh() {
        //监听刷新状态操作
        fragmentMessageBinding.fragmentMessageRf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新数据
                RefreshMODE = MOD_REFRESH;
                getUserMsg(0L);
            }
        });

        //滑动监听
        fragmentMessageBinding.fragmentMainRv.addOnScrollListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (adapter.getItemCount() < 10) return;

                RefreshMODE = MOD_LOADING;
                adapter.updateLoadStatus(LoadMord.LOAD_MORE);

                Long msgTime = SPUtil.build().getLong(Constants.SP_MESSAGE_USER_TIME);
                if (msgTime < 0) {
                    msgTime = 0L;
                }

                getUserMsg(msgTime);
            }
        });
    }

    /*监听item*/
    private void onClickItem() {
        adapter.setOnItemClickListener(new MessageRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view != null) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("POST_USERID", data.get(position).getLatestMessage().getPostUser().getUserId());
                    intent.putExtra("RECEIVER_USERID", data.get(position).getLatestMessage().getReceiverUser().getUserId());
                    intent.putExtra("RECEIVER_USER_AVATAR", data.get(position).getLatestMessage().getReceiverUser().getUserAvatar());
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                String[] strings = {"删除会话"};
                MyAlertDialog dialog = new MyAlertDialog(getActivity(), strings,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    MessageViewModel messageViewModel = ViewModelProviders.of(getActivity()).get(MessageViewModel.class);
                                    messageViewModel.deleteSingleConversation(data.get(position).getLatestMessage().getPostUser().getUserId(),
                                            data.get(position).getLatestMessage().getReceiverUser().getUserId()).observe(getActivity(), new Observer<Boolean>() {
                                                @Override
                                                public void onChanged(Boolean response) {
                                                    if(response) {
                                                        data.remove(position);
                                                        adapter.notifyDataSetChanged();
                                                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });


                                }
                            }
                        });
                dialog.initDialog();

            }
        });
    }

    private void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fragmentMessageBinding.fragmentMainRv.setLayoutManager(layoutManager);
        adapter = new MessageRecyclerAdapter(data, getActivity());
        //分割线
        fragmentMessageBinding.fragmentMainRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        fragmentMessageBinding.fragmentMainRv.setAdapter(adapter);
    }

    private void getUserMsg(Long msgTime) {
        if (!fragmentMessageBinding.fragmentMessageRf.isRefreshing()
                && RefreshMODE == MOD_REFRESH) {
            fragmentMessageBinding.fragmentMessageRf.setRefreshing(true);
        }

        Long uid = SPUtil.build().getLong(Constants.SP_USER_ID);

        MessageViewModel messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        messageViewModel.getMessagePage(uid, msgTime).observe(this, new Observer<RetrofitResponseData<List<ConversationDto>>>() {
            @Override
            public void onChanged(RetrofitResponseData<List<ConversationDto>> response) {
                fragmentMessageBinding.fragmentMessageRf.setRefreshing(false);
                Integer code = response.getCode();
                if (!ExceptionMsg.SUCCESS.getCode().equals(code)) {
                    showToast(R.string.toast_get_msg_error);
                    return;
                }

                List<ConversationDto> list = response.getData();
                if ((list == null) || (list.size() <= 0)) {
                    fragmentMessageBinding.fragmentMessageNone.setVisibility(View.VISIBLE);
                    fragmentMessageBinding.fragmentMainRv.setVisibility(View.GONE);
                } else {
                    fragmentMessageBinding.fragmentMessageNone.setVisibility(View.GONE);
                    fragmentMessageBinding.fragmentMainRv.setVisibility(View.VISIBLE);
                }

                switch (RefreshMODE) {
                    case MOD_LOADING:
                        data.addAll(list);
                        adapter.addList(list);
                        break;
                        default:
                            data = list;
                            adapter.setList(list);
                            break;
                }
            }
        });
    }
}
