package cn.zhaoxi.zxyx.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.util.ContentUtil;
import cn.zhaoxi.zxyx.common.util.ScreenUtils;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.databinding.FeedWaterfullRecycleItemBinding;

public class FeedWaterfullAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "FeedWaterfullAdapter";
    private List<FeedDto> mList;
    private FeedWaterfullAdapter.OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, FeedDto feed, int position);
    }

    public void setOnItemListener(FeedWaterfullAdapter.OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public FeedWaterfullAdapter(List<FeedDto> list) {
        this.mList = list == null ? new ArrayList<FeedDto>() : list;
    }

    // 获取列表项的个数
    public int getItemCount() {
        return mList.size();
    }

    // 创建列表项的视图持有者
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        FeedWaterfullRecycleItemBinding feedWaterfullRecycleItemBinding = DataBindingUtil.inflate(LayoutInflater.from(vg.getContext()), R.layout.feed_waterfull_recycle_item, vg, false);

        return new FeedWaterfullAdapter.ItemHolder(feedWaterfullRecycleItemBinding);
    }

    // 绑定列表项的视图持有者
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {
        ItemHolder holder = (ItemHolder) vh;
        holder.bindItem(mList.get(position), position);
    }

    // 获取列表项的类型
    public int getItemViewType(int position) {
        return position;
    }

    // 获取列表项的编号
    public long getItemId(int position) {
        return position;
    }

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

    // 定义列表项的视图持有者
    class ItemHolder extends RecyclerView.ViewHolder {
        private FeedWaterfullRecycleItemBinding feedDetailRecycleItemBinding;

        public ItemHolder(FeedWaterfullRecycleItemBinding feedBinding) {
            super(feedBinding.getRoot());
            feedDetailRecycleItemBinding = feedBinding;
        }

        public void bindItem(FeedDto feed, int position) {
            // 图片
            String cover = feed.getFeedCover();
            if(TextUtils.isEmpty(cover)) {
                feedDetailRecycleItemBinding.ivFeedWaterfullItem.setVisibility(View.GONE);
            } else {
                feedDetailRecycleItemBinding.ivFeedWaterfullItem.setVisibility(View.VISIBLE);

                //获取item宽度，计算图片等比例缩放后的高度，为imageView设置参数
                ViewGroup.LayoutParams layoutParams = feedDetailRecycleItemBinding.ivFeedWaterfullItem.getLayoutParams();
                float itemWidth = (ScreenUtils.getScreenWidth(feedDetailRecycleItemBinding.getRoot().getContext()) - 16*3) / 2;
                layoutParams.width = (int) itemWidth;
                float scale = itemWidth/feed.getCoverWidth();
                layoutParams.height= (int) (feed.getCoverHeight()*scale);
                feedDetailRecycleItemBinding.ivFeedWaterfullItem.setLayoutParams(layoutParams);

                ContentUtil.loadWaterfullImage(feedDetailRecycleItemBinding.ivFeedWaterfullItem, cover, layoutParams.height, layoutParams.width);
            }

            feedDetailRecycleItemBinding.ivFeedWaterfullItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemListener.onItemClick(v, feed, position);
                }
            });
        }
    }
}
