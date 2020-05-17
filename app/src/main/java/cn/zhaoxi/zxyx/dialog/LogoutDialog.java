package cn.zhaoxi.zxyx.dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.zhaoxi.zxyx.R;

/**
 * time   : 2018/08/02
 * desc   : 登出Dialog
 * version: 1.0
 */
public class LogoutDialog extends DialogFragment {

    @BindView(R.id.prompt_info)
    TextView mPromptInfo;
    @BindView(R.id.prompt_ok)
    Button mPromptOk;
    @BindView(R.id.prompt_cancel)
    Button mPromptCancel;

    public interface LogoutListener {
        void onLogout();
    }

    private LogoutListener mLogoutListener;

    public void setLogoutListener(LogoutListener listener) {
        mLogoutListener = listener;
    }

    public static LogoutDialog newInstance() {
        Bundle args = new Bundle();
        LogoutDialog fragment = new LogoutDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.mine_logout_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.prompt_ok, R.id.prompt_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.prompt_ok:
                if (mLogoutListener != null) {
                    mLogoutListener.onLogout();
                }
                dismiss();
                break;
            case R.id.prompt_cancel:
                dismiss();
                break;
        }
    }
}