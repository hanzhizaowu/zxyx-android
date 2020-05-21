package cn.zhaoxi.zxyx.module.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import cn.zhaoxi.library.base.BaseFragment;
import cn.zhaoxi.library.loadmore.LoadMord;
import cn.zhaoxi.library.loadmore.OnLoadMoreListener;
import cn.zhaoxi.library.photo.PhotoBrowser;
import cn.zhaoxi.library.recycle.ItemAnimator;
import cn.zhaoxi.library.recycle.ItemDecoration;
import cn.zhaoxi.library.util.ToolbarUtil;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.adapter.FeedAdapter;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.common.util.SPUtil;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import cn.zhaoxi.zxyx.databinding.FragmentFeedBinding;
import cn.zhaoxi.zxyx.entity.Feed;
import cn.zhaoxi.zxyx.module.feed.ui.PublishActivity;
import cn.zhaoxi.zxyx.module.feed.viewmodel.FeedViewModel;

/**
 * 圈子动态
 */
public class FeedFragment extends BaseFragment {

    private static final String FEED_TYPE = "feed_type";

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FragmentFeedBinding fragmentFeedBinding;

    private List<FeedDto> mList = new ArrayList<>();
    private FeedAdapter mAdapter;

    private int mCount = 10;
    private final int MOD_REFRESH = 1;
    private final int MOD_LOADING = 2;
    private int RefreshMODE = 0;

    public FeedFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentFeedBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container,false);
        View view = fragmentFeedBinding.getRoot();
        mRecyclerView = fragmentFeedBinding.recyclerView;
        mSwipeRefreshLayout = fragmentFeedBinding.swipeRefreshLayout;
        init();
        return view;
    }

    private void init() {
        ToolbarUtil.init(fragmentFeedBinding.feedToolbar, getActivity())
                .setTitle(R.string.nav_home)
                .setTitleCenter()
                .setMenu(R.menu.publish_menu, new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_share:
                                gotoPublish();
                                break;
                        }
                        return false;
                    }
                })
                .build();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setItemAnimator(new ItemAnimator());
        ItemDecoration itemDecoration = new ItemDecoration(LinearLayoutCompat.VERTICAL, 10, Color.parseColor("#f2f2f2"));
        // 隐藏最后一个item的分割线
        itemDecoration.setGoneLast(true);
        mRecyclerView.addItemDecoration(itemDecoration);

        mAdapter = new FeedAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);

        initEvent();
        getMoodList(mCount);
    }

    //初始化事件
    private void initEvent() {
        //刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshMODE = MOD_REFRESH;
                getMoodList(mCount);
            }
        });

        //item点击
        mAdapter.setOnItemListener(new FeedAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, FeedDto feed, int position) {
                switch (view.getId()) {
                    case R.id.user_img:
                        goToUser(feed.getPostUser());
                        break;
                    case R.id.feed_card:
                    //case R.id.feed_comment_layout:
                        gotoMood(feed);
                        break;
                    /*case R.id.feed_like_layout:
                        if (feed.isLike()) return;
                        // 未点赞点赞
                        postAddLike(feed, position);
                        break;*/
                }
            }

            @Override
            public void onPhotoClick(ArrayList<String> photos, int position) {
                PhotoBrowser.builder()
                        .setPhotos(photos)
                        .setCurrentItem(position)
                        .start(Objects.requireNonNull(getActivity()));
            }
        });

        //滑动监听
        mRecyclerView.addOnScrollListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (mAdapter.getItemCount() < mCount) return;

                RefreshMODE = MOD_LOADING;
                mAdapter.updateLoadStatus(LoadMord.LOAD_MORE);

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMoodList(mCount);
                    }
                },1000);
            }
        });
    }

    // 前往动态发布
    private void gotoPublish() {
        Intent intent = new Intent(getActivity(), PublishActivity.class);
        startActivityForResult(intent, Constants.ACTIVITY_PUBLISH);
    }

    // 前往动态详情
    private void gotoMood(FeedDto feed) {
       /* Intent intent = new Intent(getActivity(), FeedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("feed", feed);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.ACTIVITY_MOOD);*/
    }

    // 点赞
    private void postAddLike(final Feed feed, final int position) {
        /*OkUtil.post()
                .url(Api.saveAction)
                .addParam("feedId", feed.getId())
                .addParam("userId", saveUid)
                .addParam("type", "0")
                .execute(new ResultCallback<Result>() {
                    @Override
                    public void onSuccess(Result response) {
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            showToast("点赞失败");
                            return;
                        }
                        List<Like> likeList = new ArrayList<>(feed.getLikeList());
                        Like like = new Like();
                        like.setUserId(saveUid);
                        like.setUsername(saveUName);
                        likeList.add(like);
                        feed.setLikeList(likeList);
                        feed.setLike(true);
                        mAdapter.updateItem(feed, position);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast("点赞失败");
                    }
                });*/
    }

    // 获取动态列表
    private void getMoodList(int pageSize) {
        if (!mSwipeRefreshLayout.isRefreshing() && RefreshMODE == MOD_REFRESH) mSwipeRefreshLayout.setRefreshing(true);
        Long uid = SPUtil.build().getLong(Constants.SP_USER_ID);
        Long feedId = SPUtil.build().getLong(Constants.SP_FEED_ID);
        if (feedId < 0) {
            feedId = 0L;
        }

        FeedViewModel feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
        feedViewModel.getFeedPage(uid, feedId).observe(this, new Observer<RetrofitResponseData<List<FeedDto>>>() {
            @Override
            public void onChanged(RetrofitResponseData<List<FeedDto>> response) {
                mSwipeRefreshLayout.setRefreshing(false);
                Integer code = response.getCode();
                if (!ExceptionMsg.SUCCESS.getCode().equals(code)) {
                    mAdapter.updateLoadStatus(LoadMord.LOAD_NONE);
                    showToast(R.string.toast_get_feed_error);
                    return;
                }

                List<FeedDto> pageFeed = response.getData();
                if ((pageFeed == null) || (pageFeed.size() == 0)) {
                    mAdapter.updateLoadStatus(LoadMord.LOAD_NONE);
                    return;
                }
                switch (RefreshMODE) {
                    case MOD_LOADING:
                        updateData(pageFeed);
                        break;
                    default:
                        setData(pageFeed);
                        break;
                }
            }
        });
    }

    // 设置数据
    private void setData(List<FeedDto> data){
        mAdapter.setData(data);
    }

    // 更新数据
    public void updateData(List<FeedDto> data) {
        mAdapter.addData(data);
    }

    // 刷新数据
    private void onRefresh(){
        RefreshMODE = MOD_REFRESH;
        getMoodList(mCount);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 发布动态回退则掉调起刷新
        if (resultCode == Constants.ACTIVITY_PUBLISH) {
            onRefresh();
        }
    }

    /**
     * 前往用户页面
     */
    private void goToUser(UserDto user){
        /*Intent intent = new Intent(getActivity(), UserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PASSED_USER_INFO, user);
        intent.putExtras(bundle);
        startActivity(intent);*/
    }
}
