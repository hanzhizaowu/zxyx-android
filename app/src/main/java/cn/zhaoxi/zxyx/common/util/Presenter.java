package cn.zhaoxi.zxyx.common.util;

import android.view.View;

public interface Presenter extends View.OnClickListener {
    @Override
    void onClick(View v);
}
