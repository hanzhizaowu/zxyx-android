package cn.zhaoxi.zxyx.module.message.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.zhaoxi.library.base.BaseActivity;
import cn.zhaoxi.library.view.SpacesItemDecoration;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.adapter.ChatRecyclerAdapter;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.common.util.Presenter;
import cn.zhaoxi.zxyx.common.util.SPUtil;
import cn.zhaoxi.zxyx.data.dto.LetterDto;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import cn.zhaoxi.zxyx.databinding.ActivityChatBinding;
import cn.zhaoxi.zxyx.module.main.viewmodel.MessageViewModel;
import cn.zhaoxi.zxyx.module.message.util.MessageEvent;
import cn.zhaoxi.zxyx.module.message.util.MqttClient;
import cn.zhaoxi.zxyx.module.message.util.SubcribeCallBackHandler;

public class ChatActivity extends BaseActivity implements Presenter {

    public static final String TAG="ChatActivity";
    private ChatRecyclerAdapter chatAdapter;
    private ActivityChatBinding activityChatBinding;
    private Long postUserId;
    private Long receiverUserId;
    private Long letterId = 0L;
    private String receiverUserAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        activityChatBinding.setPresenter(this);
        getData();
        initView();
        getChatList(true);
        //initSubscribe();
    }

    private void initView() {
        activityChatBinding.btnStartPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**获取发布的消息*/
                String pubMessage = activityChatBinding.edPubMessage.getText().toString().trim();

                if(TextUtils.isEmpty(pubMessage)){
                    showToast("发布内容不能为空");
                } else{
                    publishChat(pubMessage);
                }
            }
        });

        activityChatBinding.srChat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getChatList(false);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        activityChatBinding.rvChat.setLayoutManager(linearLayoutManager);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(2,2,0,0);
        activityChatBinding.rvChat.addItemDecoration(spacesItemDecoration);
        List<LetterDto> list=new ArrayList<LetterDto>();
        if(chatAdapter==null) {
            Log.i(TAG, "receiver id is:" + receiverUserId);
            chatAdapter = new ChatRecyclerAdapter(list, this, receiverUserId);
        }
        activityChatBinding.rvChat.setAdapter(chatAdapter);
    }

    private void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        postUserId = bundle.getLong("POST_USERID", 0L);
        receiverUserId = bundle.getLong("RECEIVER_USERID", 0L);
        receiverUserAvatar = bundle.getString("RECEIVER_USER_AVATAR");
    }

    private void initSubscribe() {
        MqttAndroidClient client = MqttClient.getMqttAndroidClientInstace();
        Long userId = SPUtil.build().getLong(Constants.SP_USER_ID);
        try {
            client.subscribe(Long.toString(userId), 0, null, new SubcribeCallBackHandler(ChatActivity.this));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取chat列表
     */
    private void getChatList(boolean first) {
        MessageViewModel messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        messageViewModel.getLetterPage(postUserId, letterId, receiverUserId, 10).observe(this, new Observer<RetrofitResponseData<List<LetterDto>>>() {
            @Override
            public void onChanged(RetrofitResponseData<List<LetterDto>> response) {
                if(!ExceptionMsg.SUCCESS.getCode().equals(response.getCode())) {
                    showToast("获取聊天消息失败");
                    return;
                } else {
                    activityChatBinding.srChat.setRefreshing(false);
                    List<LetterDto> letterDtos = response.getData();
                    if(first) {
                        chatAdapter.setList(letterDtos);
                    } else {
                        chatAdapter.addList(letterDtos);
                        activityChatBinding.rvChat.smoothScrollBy(0, -200);
                    }
                    if((letterDtos != null) && (letterDtos.size() > 0)) {
                        letterId = letterDtos.get(0).getLetterId();
                        for(LetterDto letterDto : letterDtos) {
                            if(letterDto.getLetterId() < letterId) {
                                Log.i(TAG, "letter id is:" + letterDto.getLetterId() + ", old letter id is:" + letterId);
                                letterId = letterDto.getLetterId();
                            }
                        }
                    }
                }
            }
        });
    }

    private void publishChat(String content) {
        MessageViewModel messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        messageViewModel.publishChat(receiverUserId, postUserId, content).observe(this, new Observer<RetrofitResponseData<LetterDto>>() {
            @Override
            public void onChanged(RetrofitResponseData<LetterDto> response) {
                if(ExceptionMsg.SUCCESS.getCode().equals(response.getCode())) {
                    showToast("发布成功");
                    activityChatBinding.edPubMessage.setText("");

                    LetterDto letterDto = new LetterDto();
                    letterDto.setContent(content);
                    UserDto postUser = new UserDto();
                    postUser.setUserId(receiverUserId);
                    postUser.setUserAvatar(receiverUserAvatar);
                    letterDto.setPostUser(postUser);
                    UserDto receiverUser = new UserDto();
                    receiverUser.setUserId(postUserId);
                    letterDto.setReceiverUser(receiverUser);
                    letterDto.setLetterId(response.getData().getLetterId());
                    if((letterId == null) || (letterId == 0)) {
                        letterId = response.getData().getLetterId();
                    }

                    chatAdapter.add(letterDto);
                } else {
                    showToast("发布失败");
                }
            }
        });
    }

    /**
     * 运行在主线程
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String string = event.getString();
        /**接收到服务器推送的信息，显示在右边*/
        if("".equals(string)){
            String topic = event.getTopic();
            MqttMessage mqttMessage = event.getMqttMessage();
            String s = new String(mqttMessage.getPayload());
            topic=topic+" : "+s;
            //chatAdapter.addListDate(new Message(topic,false));
            /**接收到订阅成功的通知,订阅成功，显示在左边*/
        }else{
            //chatAdapter.addListDate(new Message("Me : "+string,true));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {

    }
}
