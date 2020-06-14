package cn.zhaoxi.zxyx.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import cn.zhaoxi.library.util.DeviceUtil;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.util.Utils;
import cn.zhaoxi.zxyx.module.video.util.VideoTrimmerUtil;

/**
 * time   : 2020/05/30/
 * version: 1.0
 * description:
 */
public class VideoTrimmerAdapter extends RecyclerView.Adapter {
    private List<Bitmap> mBitmaps = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;

    public VideoTrimmerAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrimmerViewHolder(mInflater.inflate(R.layout.video_thumb_item_layout, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TrimmerViewHolder) holder).thumbImageView.setImageBitmap(mBitmaps.get(position));
    }

    @Override public int getItemCount() {
        return mBitmaps.size();
    }

    public void addBitmaps(Bitmap bitmap) {
        mBitmaps.add(bitmap);
        notifyDataSetChanged();
    }

    private final class TrimmerViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbImageView;

        TrimmerViewHolder(View itemView) {
            super(itemView);
            thumbImageView = itemView.findViewById(R.id.thumb);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) thumbImageView.getLayoutParams();
            int SCREEN_WIDTH_FULL = DeviceUtil.getDeviceWidth(context);
            int RECYCLER_VIEW_PADDING = Utils.dp2px(35);
            int VIDEO_FRAMES_WIDTH = SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2;
            layoutParams.width = VIDEO_FRAMES_WIDTH / VideoTrimmerUtil.MAX_COUNT_RANGE;
            thumbImageView.setLayoutParams(layoutParams);
        }
    }
}
