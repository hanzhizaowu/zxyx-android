package cn.zhaoxi.zxyx.module.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.zhaoxi.library.base.BaseActivity;
import cn.zhaoxi.library.util.ToolbarUtil;
import cn.zhaoxi.library.view.LoadingDialog;
import cn.zhaoxi.library.view.MoeToast;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.adapter.RelevantAdapter;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.common.okhttp.OkUtil;
import cn.zhaoxi.zxyx.common.util.SPUtil;
import cn.zhaoxi.zxyx.entity.Feed;
import cn.zhaoxi.zxyx.entity.Relevant;
import okhttp3.Call;

/**
 * 与我相关 && 我的回复
 */
public class RelevantActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private RelevantAdapter mAdapter;
    private LoadingDialog loadingProgress;
    private String saveId;
    private List<Relevant> mRelevantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relevant_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        int x = (int) (Math.random() * 4) + 1;
        if (x == 1) {
            MoeToast.makeText(this, R.string.egg_can_you_find);
        }

        Intent intent = getIntent();
        String replyType = intent.getStringExtra(Constants.REPLY_TYPE);
        if (TextUtils.isEmpty(replyType)) {
            onBackPressed();
            return;
        }

        boolean isMine = false;
        String title = "";
        switch (replyType) {
            case Constants.REPLY_MY:
                title = getString(R.string.title_bar_my_reply);
                isMine = true;
                break;
            case Constants.REPLY_RELEVANT:
                title = getString(R.string.title_bar_relevant);
                break;
            default:
                onBackPressed();
                break;
        }

        ToolbarUtil.init(mToolbar, this)
                .setTitle(title)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        saveId = SPUtil.build().getString(Constants.SP_USER_ID);
        loadingProgress = new LoadingDialog(this, R.string.dialog_loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RelevantAdapter(this, mRelevantList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new RelevantAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, Relevant relevant) {
                switch (view.getId()) {
                    case R.id.user_img:
                        break;
                    case R.id.feed_body:
                        gotoFeed(relevant.getFeed());
                        break;
                }
            }
        });

        /*if (isMine) {
            getRelevantList(Api.mineReply);
        } else {
            getRelevantList(Api.relevant);
            updateUnread();
        }*/
    }

    /**
     * 更新未读条数
     */
    public void updateUnread() {
        /*String userId = SPUtil.build().getString(Constants.SP_USER_ID);
        OkUtil.post()
                .url(Api.updateUnread)
                .addParam("userId", userId)
                .execute(new ResultCallback<Result<Integer>>() {
                    @Override
                    public void onSuccess(Result<Integer> response) {
                        Constants.isRead = true;
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });*/
    }

    // 请求与我相关
    public void getRelevantList(String url) {
        /*Integer pageNum = 1;
        Integer pageSize = 20;
        OkUtil.post()
                .url(url)
                .addParam("userId", saveId)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .setLoadDelay()
                .setProgressDialog(loadingProgress)
                .execute(new ResultCallback<Result<PageInfo<Relevant>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Relevant>> response) {
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            showToast("加载失败，下拉重新加载");
                            return;
                        }
                        updateData(response.getData().getList());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast("加载失败，下拉重新加载");
                    }
                });*/
    }

    private void updateData(List<Relevant> relevantList) {
        mAdapter.updateData(relevantList);
    }

    //前往详情页
    private void gotoFeed(Feed feed) {
        /*Intent intent = new Intent(RelevantActivity.this, FeedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("publish", publish);
        intent.putExtras(bundle);
        startActivity(intent);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        OkUtil.newInstance().cancelAll();
        super.onDestroy();
    }
}
