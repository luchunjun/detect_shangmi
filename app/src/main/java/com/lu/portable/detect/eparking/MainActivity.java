package com.lu.portable.detect.eparking;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.util.LogUtil;
import com.kernal.demo.plateid.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity {
    Button ConnectButton;//定义连接按钮
    Button UP_Button;//定义抬杆按钮
    Button Trigger_Button;//定义模拟触发按钮
    Button Disconnect1;//定义断开按钮
    TextView Result1;//定义结果输出框1
    ImageView image1; //定义显示图片框1
    Bitmap bitmap;
    KHTSample.FuncActivity camer1 = new KHTSample.FuncActivity();
    //  启动连接线程1
    int continuenext1 = -1;
    int connectnum1 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kht);
        ConnectButton =  findViewById(R.id.Connect_Bt);//获得连接按钮对象
        UP_Button = (Button) findViewById(R.id.UP_Button);//获得抬杆按钮对象
        Trigger_Button = (Button) findViewById(R.id.Trigger_Button);//获得模拟触发按钮对象
        Disconnect1 = (Button) findViewById(R.id.Disconnect1);//获得断开按钮1
        Result1 = (TextView) findViewById(R.id.Result1);
        image1 = (ImageView) findViewById(R.id.image1);
    }

    public void Connect_onClick() {
        if (connectnum1 >= 1) {
            Toast.makeText(MainActivity.this, "已连接", Toast.LENGTH_SHORT).show();
            return;
        }
//        int isconnect1 = camer1.FConnect("192.168.88.55", this);
//        if (isconnect1 != 1) {
//            Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
//        }
//        if (isconnect1 == 1) {
//            Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
//            continuenext1 = 1;
//            camer1.JosnOrBin();
//            camer1.SetResu();
//            camer1.MyThread();
//            connectnum1++;
//        }
    }




    //模拟触发1
    public void Trigger_Button(View v) {
        if (continuenext1 != 1) {
            Toast.makeText(MainActivity.this, "请先连接", Toast.LENGTH_SHORT).show();
        }
        if (continuenext1 == 1) {
            try {
                camer1.Trigger_Button();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //断开1
    public void Disconnect1(View v) {
        camer1.Disconnect();
        continuenext1 = -1;
        connectnum1 = 0;
        Toast.makeText(MainActivity.this, "已断开", Toast.LENGTH_SHORT).show();
    }

    //显示识别结果图片
    public void show_ictrl(byte[] bb) {
        bitmap = BitmapFactory.decodeByteArray(bb, 0, bb.length);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                image1.setImageBitmap(bitmap);
            }
        });
    }


    //可自行添加控件 显示识别结果文本
    public void show_text(byte[] bb, String m_ip) throws UnsupportedEncodingException {
        String s = new String(bb, "gbk");
        try {
            JSONObject Json = new JSONObject(s);
            if (Json.getInt("count") == 0) {
                System.out.println("无牌车");
                InstrumentConfig.carPlate="未识别车牌";
            } else {
                final String license = new String(Json.getJSONArray("item").getJSONObject(0).getString("license").getBytes("GBK"), "GBK");
                LogUtil.e("show_text",license);
                InstrumentConfig.carPlate=license;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}