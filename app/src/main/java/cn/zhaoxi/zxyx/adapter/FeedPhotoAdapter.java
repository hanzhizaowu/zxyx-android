package cn.zhaoxi.zxyx.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.util.ContentUtil;
import cn.zhaoxi.zxyx.data.dto.PhotoDto;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2017/11/03
 * desc   : 动态图片
 * version: 1.0
 */
public class FeedPhotoAdapter extends RecyclerView.Adapter<FeedPhotoAdapter.PhotoViewHolder> {

    private int mType = 0;
    private List<PhotoDto> mPhotos;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onPhotoClick(int position);
    }

    public FeedPhotoAdapter(List<PhotoDto> photos) {
        this.mPhotos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.publish_photo_recycle_item, null);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoViewHolder holder, final int position) {
        holder.bindItem(mPhotos.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public void setPhotos(List<PhotoDto> photos) {
        this.mPhotos = photos;
        notifyDataSetChanged();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_photo)
        ImageView mIvPhoto;
        @BindView(R.id.iv_delete)
        ImageView mIvDelete;
        private int mPosition;

        PhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(PhotoDto photo, final int position) {
            mPosition = position;
            mIvDelete.setVisibility(View.GONE);
            // 加载图片
            ContentUtil.loadFeedImage(mIvPhoto , photo.getUrl());
        }

        @OnClick({R.id.iv_photo})
        public void onClick(View view){
            switch (view.getId()) {
                case R.id.iv_photo:
                    if (mOnItemClickListener != null) mOnItemClickListener.onPhotoClick(mPosition);
                    break;
            }
        }
    }
}
