package cn.zhaoxi.zxyx.module.message.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * Description :
 * Author : liujun
 * Email  : liujin2son@163.com
 * Date   : 2016/10/25 0025
 */

public class ConnectCallBackHandler implements IMqttActionListener {

    private Context context;

    public ConnectCallBackHandler(Context context) {
        this.context=context;
    }

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        Log.d("ConnectCallBackHandler","ConnectCallBackHandler/onSuccess");
        Toast.makeText(context,"连接成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
        Log.d("ConnectCallBackHandler","ConnectCallBackHandler/onFailure");
        Toast.makeText(context,"连接失败",Toast.LENGTH_SHORT).show();
    }
}
