package cn.zhaoxi.zxyx.module.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import cn.zhaoxi.zxyx.adapter.FeedWaterfullAdapter;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.common.util.Presenter;
import cn.zhaoxi.zxyx.common.util.SPUtil;
import cn.zhaoxi.zxyx.common.util.SpacesItemDecoration;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import cn.zhaoxi.zxyx.databinding.FragmentFeedBinding;
import cn.zhaoxi.zxyx.module.feed.ui.PublishActivity;
import cn.zhaoxi.zxyx.module.feed.viewmodel.FeedViewModel;

/**
 * 圈子动态
 */
public class FeedFragment extends BaseFragment implements Presenter {

    private FragmentFeedBinding fragmentFeedBinding;

    private List<FeedDto> mList = new ArrayList<>();
    private FeedAdapter feedAdapter;
    private FeedWaterfullAdapter feedWaterfullAdapter;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;

    private final int PAGE_COUNT = 10;
    private final int MOD_REFRESH = 1;
    private final int MOD_LOADING = 2;
    private int RefreshMODE = 0;

    private LinearLayoutManager linearLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutmanager;
    private ItemDecoration itemLinearDecoration;
    private SpacesItemDecoration itemWaterfullDecoration;

    public FeedFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentFeedBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container,false);
        fragmentFeedBinding.setPresenter(this);
        View view = fragmentFeedBinding.getRoot();
        init();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentFeedBinding = null;
        mList = null;
        mAdapter = null;
        feedAdapter = null;
        feedWaterfullAdapter = null;
        linearLayoutManager = null;
        staggeredGridLayoutmanager = null;
        itemWaterfullDecoration = null;
        itemLinearDecoration = null;
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

        fragmentFeedBinding.pageFollow.setTextColor(getResources().getColor(R.color.red));

        initValue();
        getRecycleLinear();
        initEvent();
        getMoodList(PAGE_COUNT, 0L, true);
    }

    private void initValue() {
        itemWaterfullDecoration = new SpacesItemDecoration(8);

        itemLinearDecoration = new ItemDecoration(LinearLayoutCompat.VERTICAL, 10, Color.parseColor("#f2f2f2"));
        // 隐藏最后一个item的分割线
        itemLinearDecoration.setGoneLast(true);
    }

    //初始化事件
    private void initEvent() {
        //刷新
        fragmentFeedBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshMODE = MOD_REFRESH;
                boolean getLinear = (mAdapter instanceof FeedAdapter) ? true : false;
                getMoodList(PAGE_COUNT, 0L, getLinear);
            }
        });

        //滑动监听
        fragmentFeedBinding.recyclerView.addOnScrollListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (mAdapter.getItemCount() < PAGE_COUNT) return;

                RefreshMODE = MOD_LOADING;
                if(mAdapter instanceof FeedAdapter) {
                    feedAdapter.updateLoadStatus(LoadMord.LOAD_MORE);
                }

                fragmentFeedBinding.recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Long feedId = SPUtil.build().getLong(Constants.SP_FEED_ID);
                        if (feedId < 0) {
                            feedId = 0L;
                        }

                        boolean getLinear = (mAdapter instanceof FeedAdapter) ? true : false;
                        getMoodList(PAGE_COUNT, feedId, getLinear);
                    }
                },1000);
            }
        });

        fragmentFeedBinding.recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                staggeredGridLayoutmanager.invalidateSpanAssignments();//防止第一行到顶部有空白
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

    // 获取动态列表
    private void getMoodList(int pageSize, Long feedId, boolean getLinear) {
        if (!fragmentFeedBinding.swipeRefreshLayout.isRefreshing()
                && RefreshMODE == MOD_REFRESH) fragmentFeedBinding.swipeRefreshLayout.setRefreshing(true);
        Long uid = SPUtil.build().getLong(Constants.SP_USER_ID);

        FeedViewModel feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
        feedViewModel.getFeedPage(uid, feedId).observe(this, new Observer<RetrofitResponseData<List<FeedDto>>>() {
            @Override
            public void onChanged(RetrofitResponseData<List<FeedDto>> response) {
                fragmentFeedBinding.swipeRefreshLayout.setRefreshing(false);
                Integer code = response.getCode();
                if (!ExceptionMsg.SUCCESS.getCode().equals(code)) {
                    if(getLinear) {
                        feedAdapter.updateLoadStatus(LoadMord.LOAD_NONE);
                    }

                    showToast(R.string.toast_get_feed_error);
                    return;
                }

                List<FeedDto> pageFeed = response.getData();
                if (((pageFeed == null) || (pageFeed.size() == 0)) && getLinear) {
                    feedAdapter.updateLoadStatus(LoadMord.LOAD_NONE);
                    return;
                }
                switch (RefreshMODE) {
                    case MOD_LOADING:
                        updateData(pageFeed, getLinear);
                        break;
                    default:
                        setData(pageFeed, getLinear);
                        break;
                }
            }
        });
    }

    // 设置数据
    private void setData(List<FeedDto> data, boolean getLinear){
        if(getLinear) {
            feedAdapter.setData(data);
        } else {
            feedWaterfullAdapter.setData(data);
        }
    }

    // 更新数据
    public void updateData(List<FeedDto> data, boolean getLinear) {
        if(getLinear) {
            feedAdapter.addData(data);
        } else {
            feedWaterfullAdapter.addData(data);
        }
    }

    // 刷新数据
    private void onRefresh(){
        RefreshMODE = MOD_REFRESH;
        boolean getLinear = (mAdapter instanceof FeedAdapter) ? true : false;
        getMoodList(PAGE_COUNT, 0L, getLinear);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.page_follow:
                fragmentFeedBinding.pageFollow.setTextColor(getResources().getColor(R.color.red));
                fragmentFeedBinding.pageRecommend.setTextColor(getResources().getColor(R.color.black));
                getRecycleLinear();
                getMoodList(PAGE_COUNT, 0L, true);
                break;
            case R.id.page_recommend:
                fragmentFeedBinding.pageFollow.setTextColor(getResources().getColor(R.color.black));
                fragmentFeedBinding.pageRecommend.setTextColor(getResources().getColor(R.color.red));
                getRecyclerStaggered();
                getMoodList(PAGE_COUNT, 0L, false);
                break;
        }
    }

    // 初始化瀑布流布局的循环视图
    private void getRecyclerStaggered() {
        // 创建一个垂直方向的瀑布流布局管理器
        if(staggeredGridLayoutmanager == null) {
            staggeredGridLayoutmanager = new StaggeredGridLayoutManager(
                    2, RecyclerView.VERTICAL);
            //防止item交换位置
            staggeredGridLayoutmanager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            fragmentFeedBinding.recyclerView.setItemAnimator(new ItemAnimator());
        }

        fragmentFeedBinding.recyclerView.setPadding(8,8,8,8);
        //以下三行去掉 RecyclerView 动画代码，防止闪烁
        //((DefaultItemAnimator) fragmentFeedBinding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //((SimpleItemAnimator) fragmentFeedBinding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //fragmentFeedBinding.recyclerView.getItemAnimator().setChangeDuration(0);

        fragmentFeedBinding.recyclerView.addItemDecoration(itemWaterfullDecoration);
        // 设置循环视图的布局管理器
        fragmentFeedBinding.recyclerView.setLayoutManager(staggeredGridLayoutmanager);
        if(feedWaterfullAdapter == null) {
            // 构建一个服装列表的瀑布流适配器
            feedWaterfullAdapter = new FeedWaterfullAdapter(mList);
            // 设置瀑布流列表的点击监听器
            feedWaterfullAdapter.setOnItemListener(new FeedWaterfullAdapter.OnItemListener() {

                @Override
                public void onItemClick(View view, FeedDto feed, int position) {
                    switch (view.getId()) {
                        case R.id.iv_feed_waterfull_item:
                            gotoMood(feed);
                            break;
                    }
                }
            });
        }

        // 给rv_staggered设置服装瀑布流适配器er
        fragmentFeedBinding.recyclerView.setAdapter(feedWaterfullAdapter);
        mAdapter = feedWaterfullAdapter;
    }

    private void getRecycleLinear() {
        if(linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            fragmentFeedBinding.recyclerView.setItemAnimator(new ItemAnimator());
        }

        fragmentFeedBinding.recyclerView.setPadding(0,0,0,0);
        fragmentFeedBinding.recyclerView.addItemDecoration(itemLinearDecoration);
        fragmentFeedBinding.recyclerView.setLayoutManager(linearLayoutManager);

        if(feedAdapter == null) {
            feedAdapter = new FeedAdapter(mList);
            //item点击
            feedAdapter.setOnItemListener(new FeedAdapter.OnItemListener() {
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
        }
        fragmentFeedBinding.recyclerView.setAdapter(feedAdapter);
        mAdapter = feedAdapter;
    }
}
