package cn.zhaoxi.zxyx.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import butterknife.OnClick;
import cn.zhaoxi.library.BuildConfig;
import cn.zhaoxi.library.loadmore.LoadMord;
import cn.zhaoxi.library.loadmore.LoadMoreViewHolder;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.util.ContentUtil;
import cn.zhaoxi.zxyx.common.util.DateUtil;
import cn.zhaoxi.zxyx.common.util.FeedContentUtil;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.data.dto.PhotoDto;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import cn.zhaoxi.zxyx.databinding.FeedDetailRecycleItemBinding;

/**
 * Feed Adapter
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FeedDto> mList;

    private LoadMoreViewHolder mLoadMoreViewHolder;

    private final int TYPE_FOOTER = -1;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, FeedDto feed, int position);
        void onPhotoClick(ArrayList<String> photos, int position);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public FeedAdapter(List<FeedDto> list) {
        this.mList = list == null ? new ArrayList<FeedDto>() : list;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return position;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View loadView = View.inflate(parent.getContext(), R.layout.lib_load_more, null);
            return mLoadMoreViewHolder = new LoadMoreViewHolder(loadView);
        } else {
            FeedDetailRecycleItemBinding feedDetailRecycleItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.feed_detail_recycle_item, parent, false);

            return new MoodViewHolder(feedDetailRecycleItemBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadMoreViewHolder) {
            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
            if (getItemCount() >= 10) {
                loadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
            } else {
                loadMoreViewHolder.bindItem(LoadMord.LOAD_END);
            }
        } else {
            MoodViewHolder moodViewHolder = (MoodViewHolder) holder;
            moodViewHolder.bindItem(mList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public void updateLoadStatus(int status) {
        if(mLoadMoreViewHolder != null)
            mLoadMoreViewHolder.bindItem(status);
    }

    // 设置数据
    public void setData(List<FeedDto> data) {
        if (data == null) {
            return;
        }
        mList = data;
        if(mLoadMoreViewHolder != null) {
            mLoadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
        }

        notifyDataSetChanged();
    }

    // 添加数据
    public void addData(List<FeedDto> data) {
        if (data == null) {
            return;
        }
        mList.addAll(data);
        if(mLoadMoreViewHolder != null) {
            mLoadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
        }

        notifyDataSetChanged();
    }

    // 更新item
    public void updateItem(FeedDto feed, int position) {
        mList.set(position, feed);
        notifyItemChanged(position);
    }

    class MoodViewHolder extends RecyclerView.ViewHolder {

        private FeedDto mFeed;
        private int mPosition;
        private FeedDetailRecycleItemBinding feedDetailRecycleItemBinding;

        public MoodViewHolder(FeedDetailRecycleItemBinding feedBinding) {
            super(feedBinding.getRoot());
            feedDetailRecycleItemBinding = feedBinding;
        }

        public void bindItem(FeedDto feed, int position) {
            mFeed = feed;
            mPosition = position;
            UserDto user = feed.getPostUser();
            user = user == null ? new UserDto() : user;
            // 动态详情
            ContentUtil.loadUserAvatar(feedDetailRecycleItemBinding.feedInfoLayout.userImg, user.getUserAvatar());

            feedDetailRecycleItemBinding.feedInfoLayout.feedTime.setText(DateUtil.showTime(feed.getCreateTime()));
            feedDetailRecycleItemBinding.feedInfoLayout.feedInfo.setText(FeedContentUtil.getFeedText(feed.getFeedTitle(),
                    feedDetailRecycleItemBinding.feedInfoLayout.feedInfo));
            // 图片
            final String cover = feed.getFeedCover();
            if(cover == null) {
                feedDetailRecycleItemBinding.feedImg.setVisibility(View.GONE);

            } else {
                feedDetailRecycleItemBinding.feedImg.setVisibility(View.VISIBLE);
                ContentUtil.loadImage(feedDetailRecycleItemBinding.feedImg, cover);
            }
            //mFeedCommentNum.setText(String.valueOf(publish.getCommentNum()));;
        }


        @OnClick({R.id.user_img, R.id.feed_comment_layout, R.id.feed_like_layout, R.id.feed_card})
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mFeed, mPosition);
        }
    }
}
