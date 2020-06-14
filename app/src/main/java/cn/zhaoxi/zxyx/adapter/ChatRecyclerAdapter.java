package cn.zhaoxi.zxyx.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.util.ContentUtil;
import cn.zhaoxi.zxyx.data.dto.LetterDto;
import cn.zhaoxi.zxyx.databinding.ItemSubscriberBinding;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<LetterDto> data = new ArrayList<LetterDto>();
    private Long userId;

    public void addList(List<LetterDto> newList) {
        if (null != newList && newList.size() > 0) {
            data.addAll(newList);
            sortData(data);
            notifyDataSetChanged();
        }
    }

    public void add(LetterDto letterDto) {
        if (null != letterDto) {
            data.add(letterDto);
            sortData(data);
            notifyDataSetChanged();
        }
    }

    public void setList(List<LetterDto> list) {
        if (null != list && list.size() > 0) {
            data = list;
            sortData(data);
            notifyDataSetChanged();
        }
    }

    private void sortData(List<LetterDto> list) {
        Collections.sort(list, new Comparator<LetterDto>() {
            @Override
            public int compare(LetterDto o1, LetterDto o2) {
                if(o1.getLetterId() > o2.getLetterId()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public ChatRecyclerAdapter(List<LetterDto> data, Context context, Long userId) {
        this.data = data;
        this.userId = userId;
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
        ItemSubscriberBinding itemSubscriberBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_subscriber,
                parent, false);
        return new ChatRecyclerAdapter.ItemViewHolder(itemSubscriberBinding);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (data != null) {
            ChatRecyclerAdapter.ItemViewHolder chatViewHold = (ChatRecyclerAdapter.ItemViewHolder) holder;
            chatViewHold.bindItem(data.get(position));
        }
    }

    //展示的item
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ItemSubscriberBinding itemSubscriberBinding;

        public ItemViewHolder(ItemSubscriberBinding itemSubscriberBinding) {
            super(itemSubscriberBinding.getRoot());
            this.itemSubscriberBinding = itemSubscriberBinding;
        }

        public void bindItem(LetterDto message) {
            if(message!=null){
                Log.i("ChatAdapter", "userId is:"+userId+" ,receiver Id is:"+message.getReceiverUser().getUserId());
                if(userId.equals(message.getReceiverUser().getUserId())) {
                    itemSubscriberBinding.rlLeft.setVisibility(View.VISIBLE);
                    itemSubscriberBinding.rlRigth.setVisibility(View.GONE);
                    itemSubscriberBinding.tvLeft.setText(message.getContent());
                    ContentUtil.loadAvatar(itemSubscriberBinding.cvLeft, message.getPostUser().getUserAvatar());
                } else {
                    itemSubscriberBinding.rlLeft.setVisibility(View.GONE);
                    itemSubscriberBinding.rlRigth.setVisibility(View.VISIBLE);
                    itemSubscriberBinding.tvRigth.setText(message.getContent());
                    ContentUtil.loadAvatar(itemSubscriberBinding.cvRight, message.getPostUser().getUserAvatar());
                }
            }
        }
    }
}