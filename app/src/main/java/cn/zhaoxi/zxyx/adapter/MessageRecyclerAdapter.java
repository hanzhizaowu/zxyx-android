package cn.zhaoxi.zxyx.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.zhaoxi.library.loadmore.LoadMord;
import cn.zhaoxi.library.loadmore.LoadMoreViewHolder;
import cn.zhaoxi.library.view.CircleImageView;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.util.ContentUtil;
import cn.zhaoxi.zxyx.common.util.DateUtil;
import cn.zhaoxi.zxyx.data.dto.ConversationDto;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ConversationDto> data = new ArrayList<ConversationDto>();
    private LoadMoreViewHolder mLoadMoreViewHolder;

    private final int TYPE_FOOTER = -1;

    public void addList(List<ConversationDto> newList) {
        if (null != newList && newList.size() > 0) {
            data.addAll(newList);
            if(mLoadMoreViewHolder != null) {
                mLoadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
            }
            notifyDataSetChanged();
        }
    }

    public void setList(List<ConversationDto> list) {
        if (null != list && list.size() > 0) {
            data = list;
            if(mLoadMoreViewHolder != null) {
                mLoadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
            }
            notifyDataSetChanged();
        }
    }

    public void updateLoadStatus(int status) {
        if(mLoadMoreViewHolder != null)
            mLoadMoreViewHolder.bindItem(status);
    }

    public void clear() {
        data.clear();
        if(mLoadMoreViewHolder != null) {
            mLoadMoreViewHolder.bindItem(LoadMord.LOAD_END);
        }
        notifyDataSetChanged();
    }

    public MessageRecyclerAdapter(List<ConversationDto> data, Context context) {
        this.data = data;
        this.context = context;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        if (null != data && data.size() > 0) {
            return data.size();
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View loadView = View.inflate(parent.getContext(), R.layout.lib_load_more, null);
            return mLoadMoreViewHolder = new LoadMoreViewHolder(loadView);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof LoadMoreViewHolder) {
            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
            if (getItemCount() >= 10) {
                loadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
            } else {
                loadMoreViewHolder.bindItem(LoadMord.LOAD_END);
            }
        } else {
            //监听回调
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, index);

                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        return false;
                    }
                });
            }
            if (!data.equals("")) {
                ItemViewHolder msgViewHold = (ItemViewHolder) holder;
                msgViewHold.bindItem(data.get(position));
            }
        }
    }

    //展示的item
    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView title, time, content, unReadMsg;
        CircleImageView img;

        public ItemViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.item_main_username);
            time = (TextView) view.findViewById(R.id.item_main_time);
            content = (TextView) view.findViewById(R.id.item_main_content);
            img = (CircleImageView) view.findViewById(R.id.item_main_img);
            unReadMsg = (TextView) view.findViewById(R.id.item_main_message);
        }

        public void bindItem(ConversationDto conversationDto) {
            content.setText(conversationDto.getLatestMessage().getContent());
            title.setText(conversationDto.getLatestMessage().getPostUser().getUserName());
            time.setText(DateUtil.showTime(conversationDto.getLatestMessage().getCreateTime()));
            ContentUtil.loadUserAvatar(img, conversationDto.getLatestMessage().getPostUser().getUserAvatar());

            Integer unReadCount = conversationDto.getUnReadMsgCnt();
            if(unReadCount >  0) {
                unReadMsg.setVisibility(View.VISIBLE);
                unReadMsg.setText(unReadCount+"条未读消息");
            }
        }
    }

}
