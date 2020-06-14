package cn.zhaoxi.zxyx.adapter;

import android.provider.MediaStore;
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
import cn.zhaoxi.zxyx.common.glide.GlideApp;
import cn.zhaoxi.zxyx.common.util.ContentUtil;
import cn.zhaoxi.zxyx.common.util.DateUtil;
import cn.zhaoxi.zxyx.common.util.FeedContentUtil;
import cn.zhaoxi.zxyx.common.util.Presenter;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.data.dto.PhotoDto;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import cn.zhaoxi.zxyx.data.dto.VideoDto;
import cn.zhaoxi.zxyx.databinding.FeedDetailRecycleItemBinding;
import cn.zhaoxi.zxyx.databinding.VideoDetailRecycleItemBinding;
import cn.zhaoxi.zxyx.module.video.ui.view.VideoViewHolder;
import cn.zhaoxi.zxyx.module.video.videoplayer.NiceVideoPlayer;
import cn.zhaoxi.zxyx.module.video.videoplayer.TxVideoPlayerController;

/**
 * FeedVideo Adapter
 */
public class FeedVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FeedDto> mList;

    private LoadMoreViewHolder mLoadMoreViewHolder;

    private final int TYPE_FOOTER = -1;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, FeedDto feed, int position);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public FeedVideoAdapter() {
        this.mList =  new ArrayList<FeedDto>();
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
            if (BuildConfig.DEBUG) Log.i("FeedVideoAdapter", "create load more");
            return mLoadMoreViewHolder = new LoadMoreViewHolder(loadView);
        } else {
            VideoDetailRecycleItemBinding videoDetailRecycleItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.video_detail_recycle_item, parent, false);

            TxVideoPlayerController controller = new TxVideoPlayerController(parent.getContext());
            VideoViewHolder holder = new VideoViewHolder(videoDetailRecycleItemBinding);
            holder.setController(controller);
            return holder;
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
            VideoViewHolder moodViewHolder = (VideoViewHolder) holder;
            moodViewHolder.bindItem(mList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        if (BuildConfig.DEBUG) Log.i("FeedVideoAdapter", "count is:" + (mList.size() + 1));
        return mList.size() + 1;
    }

    public void updateLoadStatus(int status) {
        mLoadMoreViewHolder.bindItem(status);
    }

    // 设置数据
    public void setData(List<FeedDto> data) {
        if (data == null) {
            return;
        }
        mList = data;
        notifyDataSetChanged();
    }

    // 添加数据
    public void addData(List<FeedDto> data) {
        if (data == null) {
            return;
        }
        mList.addAll(data);
        notifyDataSetChanged();
    }

    // 更新item
    public void updateItem(FeedDto feed, int position) {
        mList.set(position, feed);
        notifyItemChanged(position);
    }
}
