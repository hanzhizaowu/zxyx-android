package cn.zhaoxi.zxyx.module.video.ui.view;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.glide.GlideApp;
import cn.zhaoxi.zxyx.common.util.ContentUtil;
import cn.zhaoxi.zxyx.common.util.DateUtil;
import cn.zhaoxi.zxyx.common.util.FeedContentUtil;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import cn.zhaoxi.zxyx.data.dto.VideoDto;
import cn.zhaoxi.zxyx.databinding.VideoDetailRecycleItemBinding;
import cn.zhaoxi.zxyx.module.video.videoplayer.NiceVideoPlayer;
import cn.zhaoxi.zxyx.module.video.videoplayer.TxVideoPlayerController;

public class VideoViewHolder extends RecyclerView.ViewHolder {
    private TxVideoPlayerController mController;
    private NiceVideoPlayer mVideoPlayer;
    private VideoDetailRecycleItemBinding videoDetailRecycleItemBinding;

    public VideoViewHolder(VideoDetailRecycleItemBinding feedBinding) {
        super(feedBinding.getRoot());
        videoDetailRecycleItemBinding = feedBinding;
        mVideoPlayer = (NiceVideoPlayer) videoDetailRecycleItemBinding.niceVideoPlayer;
        // 将列表中的每个视频设置为默认16:9的比例
        ViewGroup.LayoutParams params = mVideoPlayer.getLayoutParams();
        params.width = itemView.getResources().getDisplayMetrics().widthPixels; // 宽度为屏幕宽度
        params.height = (int) (params.width * 9f / 16f);    // 高度为宽度的9/16
        mVideoPlayer.setLayoutParams(params);
    }

    public NiceVideoPlayer getVideoPlayer() {
        return mVideoPlayer;
    }

    public void setController(TxVideoPlayerController controller) {
        mController = controller;
        mVideoPlayer.setController(mController);
    }

    private void bindData(VideoDto video, String cover) {
        mController.setLenght(video.getVideoTime());
        GlideApp.with(itemView.getContext())
                .load(cover)
                .placeholder(R.drawable.img_default)
                .centerInside()
                .into(mController.imageView());
        mVideoPlayer.setUp(video.getVideoUrl(), null);
    }

    public void bindItem(FeedDto feed, int position) {
        UserDto user = feed.getPostUser();
        user = user == null ? new UserDto() : user;
        // 动态详情
        ContentUtil.loadUserAvatar(videoDetailRecycleItemBinding.videoInfoLayout.userImg, user.getUserAvatar());
        videoDetailRecycleItemBinding.videoInfoLayout.userName.setText(user.getUserName());

        videoDetailRecycleItemBinding.videoInfoLayout.feedTime.setText(DateUtil.showTime(feed.getCreateTime()));
        videoDetailRecycleItemBinding.videoInfoLayout.feedVideoInfo.setText(FeedContentUtil.getFeedText(feed.getFeedTitle(),
                videoDetailRecycleItemBinding.videoInfoLayout.feedVideoInfo));
        // 图片
        bindData(feed.getVideo(), feed.getFeedCover());
        //mFeedCommentNum.setText(String.valueOf(publish.getCommentNum()));;
    }
}
