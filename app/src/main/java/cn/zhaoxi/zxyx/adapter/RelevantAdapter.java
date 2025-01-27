package cn.zhaoxi.zxyx.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import cn.zhaoxi.zxyx.common.util.ContentUtil;
import cn.zhaoxi.zxyx.common.util.Utils;
import cn.zhaoxi.zxyx.entity.Comment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.data.entity.User;
import cn.zhaoxi.zxyx.entity.Feed;
import cn.zhaoxi.zxyx.entity.Relevant;
import cn.zhaoxi.zxyx.entity.Reply;

/**
 * Reply Adapter
 */
public class RelevantAdapter extends RecyclerView.Adapter<RelevantAdapter.RelevantViewHolder> {

    private Context mContext;
    private List<Relevant> mList;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Relevant relevant);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public RelevantAdapter(Context context, List<Relevant> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RelevantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.relevant_recycle_item, null);
        return new RelevantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelevantViewHolder holder, int position) {
        holder.bindItem(mContext, mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<Relevant> data) {
        mList.addAll(data);
        notifyDataSetChanged();
    }

    class RelevantViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img)
        ImageView mUserImg;
        @BindView(R.id.user_name)
        TextView mUserName;
        @BindView(R.id.relevant_time)
        TextView mRelevantTime;
        @BindView(R.id.relevant_info)
        AppCompatTextView mRelevantInfo;
        @BindView(R.id.feed_info)
        AppCompatTextView mMoodInfo;
        @BindView(R.id.feed_body)
        LinearLayout mMoodBody;

        private Relevant mRelevant;

        public RelevantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(Context context, Relevant relevant) {
            mRelevant = relevant;
            Comment comment = relevant.getComment();
            User user = comment.getUser();
            Integer replyNum = relevant.getReplyNum();
            StringBuilder relevantInfo = new StringBuilder();
            String timeStr = "";
            if (replyNum > 0) {
                List<Reply> replyList = relevant.getReplyList();
                for (int i = 0, size = replyList.size(); i < size; i++) {
                    Reply reply = replyList.get(i);
                    if (i == 0) {
                        user = reply.getUser();
                        timeStr = reply.getCreateTime();
                        relevantInfo.append(reply.getCommentInfo());
                    } else {
                        relevantInfo.append("//{@").append(reply.getUser().getUserName()).append("}:")
                                .append(reply.getCommentInfo());
                    }

                }
                relevantInfo.append("//{@").append(comment.getUser().getUserName()).append("}:")
                        .append(comment.getCommentInfo());
            } else {
                timeStr = comment.getCreateTime();
                relevantInfo.append(comment.getCommentInfo());
            }

            ContentUtil.loadUserAvatar(mUserImg, user.getUserAvatar());

            mUserName.setText(user.getUserName());
            mRelevantTime.setText(timeStr);

            mRelevantInfo.setText(Utils.colorFormat(relevantInfo.toString()));

            Feed feed = relevant.getFeed();
            String feedInfo = "{" + feed.getUser().getUserName() + "}：" + feed.getFeedInfo();
            mMoodInfo.setText(Utils.colorFormat(feedInfo));
        }

        @OnClick({R.id.user_img, R.id.feed_body})
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mRelevant);
        }
    }
}
