package cn.zhaoxi.zxyx.module.main.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import cn.zhaoxi.zxyx.adapter.FeedVideoAdapter;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.common.util.Presenter;
import cn.zhaoxi.zxyx.common.util.SPUtil;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import cn.zhaoxi.zxyx.data.dto.VideoDto;
import cn.zhaoxi.zxyx.databinding.FragmentVideoBinding;
import cn.zhaoxi.zxyx.module.main.viewmodel.FeedViewModel;
import cn.zhaoxi.zxyx.module.publish.ui.PublishActivity;
import cn.zhaoxi.zxyx.module.video.ui.VideoSelectActivity;
import cn.zhaoxi.zxyx.module.video.ui.view.VideoViewHolder;
import cn.zhaoxi.zxyx.module.video.videoplayer.NiceVideoPlayer;
import cn.zhaoxi.zxyx.module.video.videoplayer.NiceVideoPlayerManager;

public class VideoFragment extends BaseFragment {

    private FragmentVideoBinding fragmentVideoBinding;
    private FeedVideoAdapter feedAdapter;

    private final int PAGE_COUNT = 10;
    private final int MOD_REFRESH = 1;
    private final int MOD_LOADING = 2;
    private int RefreshMODE = 0;

    public VideoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentVideoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_video, container,false);
        //fragmentVideoBinding.setPresenter(this);
        View view = fragmentVideoBinding.getRoot();
        init();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentVideoBinding = null;
        feedAdapter = null;
    }

    private void init() {
        ToolbarUtil.init(fragmentVideoBinding.videoToolbar, getActivity())
                .setTitle(R.string.nav_video)
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

        getRecycleLinear();
        initEvent();
        getMoodList(PAGE_COUNT, 0L);
    }

    //初始化事件
    private void initEvent() {
        //刷新
        fragmentVideoBinding.videoSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshMODE = MOD_REFRESH;
                getMoodList(PAGE_COUNT, 0L);
            }
        });

        //滑动监听
        fragmentVideoBinding.videoRecyclerView.addOnScrollListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (feedAdapter.getItemCount() < PAGE_COUNT) return;

                RefreshMODE = MOD_LOADING;
                feedAdapter.updateLoadStatus(LoadMord.LOAD_MORE);

                fragmentVideoBinding.videoRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Long feedId = SPUtil.build().getLong(Constants.SP_FEED_VIDEO_ID);
                        if (feedId < 0) {
                            feedId = 0L;
                        }

                        getMoodList(PAGE_COUNT, feedId);
                    }
                },1000);
            }
        });
    }

    // 前往动态发布
    private void gotoPublish() {
        Intent intent = new Intent(getActivity(), PublishActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.PUBLISH_TYPE, Constants.PHOTO_TYPE_VIDEO);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.ACTIVITY_VIDEO_PUBLISH);
    }

    // 获取动态列表
    private void getMoodList(int pageSize, Long feedId) {
        if (!fragmentVideoBinding.videoSwipeRefreshLayout.isRefreshing()
                && RefreshMODE == MOD_REFRESH) fragmentVideoBinding.videoSwipeRefreshLayout.setRefreshing(true);
        Long uid = SPUtil.build().getLong(Constants.SP_USER_ID);

        FeedViewModel feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
        feedViewModel.getFeedPage(uid, feedId, Constants.PHOTO_TYPE_VIDEO).observe(this, new Observer<RetrofitResponseData<List<FeedDto>>>() {
            @Override
            public void onChanged(RetrofitResponseData<List<FeedDto>> response) {
                fragmentVideoBinding.videoSwipeRefreshLayout.setRefreshing(false);
                Integer code = response.getCode();
                if (!ExceptionMsg.SUCCESS.getCode().equals(code)) {
                    feedAdapter.updateLoadStatus(LoadMord.LOAD_NONE);

                    showToast(R.string.toast_get_feed_error);
                    return;
                }

                List<FeedDto> pageFeed = response.getData();
                if ((pageFeed == null) || (pageFeed.size() == 0)) {
                    feedAdapter.updateLoadStatus(LoadMord.LOAD_NONE);
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
        feedAdapter.setData(data);
    }

    // 更新数据
    public void updateData(List<FeedDto> data) {
        feedAdapter.addData(data);
    }

    // 刷新数据
    private void onRefresh(){
        RefreshMODE = MOD_REFRESH;
        getMoodList(PAGE_COUNT, 0L);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 发布动态回退则掉调起刷新
        if (resultCode == Constants.ACTIVITY_VIDEO_PUBLISH) {
            onRefresh();
        }
    }

    private void getRecycleLinear() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        fragmentVideoBinding.videoRecyclerView.setItemAnimator(new ItemAnimator());

        fragmentVideoBinding.videoRecyclerView.setPadding(0,0,0,0);

        ItemDecoration itemLinearDecoration = new ItemDecoration(LinearLayoutCompat.VERTICAL, 10, Color.parseColor("#f2f2f2"));
        // 隐藏最后一个item的分割线
        itemLinearDecoration.setGoneLast(true);
        fragmentVideoBinding.videoRecyclerView.addItemDecoration(itemLinearDecoration);
        fragmentVideoBinding.videoRecyclerView.setLayoutManager(linearLayoutManager);

        if(feedAdapter == null) {
            feedAdapter = new FeedVideoAdapter();
            //item点击
            feedAdapter.setOnItemListener(new FeedVideoAdapter.OnItemListener() {
                @Override
                public void onItemClick(View view, FeedDto feed, int position) {
                    switch (view.getId()) {
                        case R.id.user_img:
                            break;
                    }
                }
            });
        }
        fragmentVideoBinding.videoRecyclerView.setAdapter(feedAdapter);

        fragmentVideoBinding.videoRecyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                if(holder instanceof VideoViewHolder) {
                    NiceVideoPlayer niceVideoPlayer = ((VideoViewHolder)holder).getVideoPlayer();
                    if (niceVideoPlayer == NiceVideoPlayerManager.instance().getCurrentNiceVideoPlayer()) {
                        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
                    }
                }
            }
        });
    }
}
