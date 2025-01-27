package cn.zhaoxi.zxyx.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.entity.Message;

/**
 * Description :
 * Author : czx
 * Date   : 2020/6/12
 */

public class SubcriberAdapter extends BaseAdapter {

    private List<Message> listDate = null;

    private Context context = null;

    public SubcriberAdapter(List<Message> listDate, Context context) {
        this.listDate = listDate;
        this.context = context;
    }

    public List<Message> getListDate() {
        return listDate;
    }

    public void setListDate(List<Message> listDate) {
        this.listDate = listDate;
        notifyDataSetChanged();
    }

    public void addListDate(Message message) {
        this.listDate.add(message);
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return listDate.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = View.inflate(context, R.layout.item_subscriber, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        /**
         * 操作数据
         */
        Message message = listDate.get(i);
        if(message!=null){
            if(message.isLeft){
                viewHolder.rlLeft.setVisibility(View.VISIBLE);
                viewHolder.rlRigth.setVisibility(View.GONE);
                viewHolder.tvLeft.setText(message.string);
            }else{
                viewHolder.rlLeft.setVisibility(View.GONE);
                viewHolder.rlRigth.setVisibility(View.VISIBLE);
                viewHolder.tvRigth.setText(message.string);
            }
        }
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.tv_left)
        TextView tvLeft;
        @BindView(R.id.rl_left)
        RelativeLayout rlLeft;
        @BindView(R.id.tv_rigth)
        TextView tvRigth;
        @BindView(R.id.rl_rigth)
        RelativeLayout rlRigth;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
