package com.lu.portable.detect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.posapi.PosApi;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.kernal.demo.plateid.R;
import com.lu.portable.detect.portableprint.PowerUtil;
import com.lu.portable.detect.portableprint.PrintQueue;
import com.lu.portable.detect.sunmi.AidlUtil;
import com.lu.portable.detect.util.LogUtil;

import java.io.UnsupportedEncodingException;

//050101010101020DAA696D654F0D0A---053801000000010DAA08DE59000D0A
//alt +commad+L§§§ format
//ctrl+alt+o orginzie import
//5505F30674696D654F4CAD5A  WEIGHT

public class BaseActivity extends AppCompatActivity {
    public static PrintQueue mPrintQueue;
    // 默认本地发音人
    public static String voicerLocal = "xiaoyan";
    private static String TAG = BaseActivity.class.getSimpleName();
    PosApi.OnCommEventListener mCommEventListener = (cmdFlag, state, resp, respLen) -> {
        switch (cmdFlag) {
            case PosApi.POS_INIT:
                if (state == PosApi.COMM_STATUS_SUCCESS) {
                    Toast.makeText(getApplicationContext(), "内置打印机启动", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "内置打印机启动失败" + state, Toast.LENGTH_SHORT)
                            .show();
                }
                break;
        }
    };
    // 语音合成对象
    private SpeechSynthesizer mTts;
    //缓冲进度
    private int mPercentForBuffering = 0;
    //播放进度
    private int mPercentForPlaying = 0;
    private Toast mToast;
    private PosApi mApi;
    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                LogUtil.d(TAG, "初始化失败,错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };
    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {

            Log.d(TAG, "开始播放");
        }

        @Override
        public void onSpeakPaused() {
            Log.d(TAG, "暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            Log.d(TAG, "继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            mPercentForBuffering = percent;
//            Log.d(TAG, String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
//            LogUtil.d(TAG, String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                LogUtil.d(TAG, "播放完成");
            } else {
                LogUtil.d(TAG, error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}

            //实时音频流输出参考
			/*if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
				byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
				Log.e("MscSpeechLog", "buf is =" + buf);
			}*/
        }
    };

    public void initPortablePrinter() {
        //A980
        //  if (!android.os.Build.PRODUCT.equals("full_U9000")  && !Build.MANUFACTURER.equals("alps")) {
        // if (!android.os.Build.PRODUCT.equals("A980")  && !Build.MANUFACTURER.equals("A980")) {
        if (!android.os.Build.PRODUCT.equals("full_U9000") || !Build.MANUFACTURER.equals("alps")) {
            Log.d("initPortablePrinter", Build.PRODUCT);
            Log.d("initPortablePrinter", Build.MANUFACTURER);
           // showToast(R.string.IDS_device_not_support_print);
            return;
        }
        //Power on
        PowerUtil.power("1");
        //init
        mApi = PosApi.getInstance(getBaseContext());
        mApi.setOnComEventListener(mCommEventListener);
        //get interface
        mApi.initDeviceEx("/dev/ttyMT2");
        mPrintQueue = new PrintQueue(this, mApi);
        mPrintQueue.init();
        mPrintQueue.setOnPrintListener(new PrintQueue.OnPrintListener() {
            @Override
            public void onGetState(int state) {
                switch (state) {
                    case 0:
                        showToast(R.string.IDS_has_paper);
                        break;
                    case 1:
                        showToast(R.string.IDS_no_paper);
                        break;
                }
            }

            @Override
            public void onPrinterSetting(int state) {
                switch (state) {
                    case 0:
                        showToast(R.string.IDS_has_paper);
                        break;
                    case 1:
                        showToast(R.string.IDS_no_paper);
                        break;
                    case 2:
                        showToast(R.string.IDS_black_mark);
                        break;
                }
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                showToast(R.string.IDS_print_finished);
            }

            @Override
            public void onFailed(int state) {
                // TODO Auto-generated method stub
                switch (state) {
                    case PosApi.ERR_POS_PRINT_NO_PAPER:
                        showToast(R.string.IDS_no_paper);
                        break;
                    case PosApi.ERR_POS_PRINT_FAILED:
                        showToast(R.string.IDS_print_fail);
                        break;
                    case PosApi.ERR_POS_PRINT_VOLTAGE_LOW:
                        showToast(R.string.IDS_voltage_low);
                        break;
                    case PosApi.ERR_POS_PRINT_VOLTAGE_HIGH:
                        showToast(R.string.IDS_voltage_high);
                        break;
                }
            }
        });
    }

    private void addPrintTextWithSize(int size, int concentration, byte[] data) {
        if (data == null) {
            return;
        }
        // 2 size Font
        byte[] _2x = new byte[]{0x1b, 0x57, 0x02};
        // 1 size Font
        byte[] _1x = new byte[]{0x1b, 0x57, 0x01};
        byte[] mData;
        if (size == 1) {
            mData = new byte[3 + data.length];
            System.arraycopy(_1x, 0, mData, 0, _1x.length);
            System.arraycopy(data, 0, mData, _1x.length, data.length);
            mPrintQueue.addText(concentration, mData);
        } else if (size == 2) {
            mData = new byte[3 + data.length];
            System.arraycopy(_2x, 0, mData, 0, _2x.length);
            System.arraycopy(data, 0, mData, _2x.length, data.length);
            mPrintQueue.addText(concentration, mData);
        }
    }

    public void printText(StringBuilder sb) {
        Log.e("printText", "android.os.Build.PRODUCT" + android.os.Build.PRODUCT);
        //if (!android.os.Build.PRODUCT.equals("full_U9000") && !Build.MANUFACTURER.equals("alps")) {
        // if (!android.os.Build.PRODUCT.equals("A980") && !Build.MANUFACTURER.equals("A980")) {
        if (android.os.Build.PRODUCT.equals("full_U9000") && Build.MANUFACTURER.equals("alps")) {
            try {
                sb.append("\n\n");
                byte[] text = sb.toString().getBytes("GBK");
                addPrintTextWithSize(2, 60, text);
                mPrintQueue.printStart();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if(android.os.Build.PRODUCT.equals("V2")) {
            AidlUtil.getInstance().printText(sb.toString(), 45, false, false);
            Log.v("printText", Build.PRODUCT);
        }else{
            Log.v("printText", Build.PRODUCT);
            showToast(R.string.IDS_device_not_support_print);
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
    }

    public void showCommonDialog(int titleId, View viewLayout, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleId);
        builder.setView(viewLayout);
        builder.setPositiveButton(R.string.confirm_btn_string, okListener);
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private String getResourcePath() {
        //合成通用资源
        //发音人资源
        String tempBuffer = ResourceUtil.generateResourcePath(getBaseContext(), ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet") +
                ";" +
                ResourceUtil.generateResourcePath(getBaseContext(), ResourceUtil.RESOURCE_TYPE.assets, "tts/" + voicerLocal + ".jet");
        return tempBuffer;
    }

    public void setStatusBar() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    public void setTitle(String titleText) {
        TextView title = findViewById(R.id.title_textView);
        if (title != null) {
            title.setText(titleText);
        }
    }

    public void back(View view) {
        Log.d("BaseActivity", "back");
        finish();
    }

    public void setTitle(int titleId) {
        ((TextView) findViewById(R.id.title_textView)).setText(titleId);
    }

    public void showToast(int id) {
        showToast(getString(id));
    }

    public void showToast(final String msg) {
        if (isFinishing()) {
            return;
        }
        if (!msg.equals("")) {
            runOnUiThread(() -> {
                if (mToast == null) {
                    //将这一句换成下面两句
                    mToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                }
                mToast.setText(msg);
                mToast.show();
            });

        }
    }

    public void pronounce(String voice) {
        // 设置参数
        setParam();
        int code = mTts.startSpeaking(voice, mTtsListener);

        if (code != ErrorCode.SUCCESS) {
            LogUtil.d(TAG, "语音合成失败,错误码: " + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
        }
    }


    //    public void queryBattery(String battery){
//        sendScaleMsg();
//        sendScaleMsg(InstrumentConfig.generateSendMsg(addr,"F2",InstrumentConfig.TSBV));
//        mMainHandler.sendEmptyMessage(QUERY_BATTERY);
//
//    }

    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置合成
        //设置使用本地引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        //设置发音人资源路径
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicerLocal);
        //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY,"1");//支持实时音频流抛出，仅在synthesizeToUri条件下支持
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "90");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//            mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//            mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    public void destoryPrinter() {
        if (android.os.Build.PRODUCT.equals("full_U9000") && Build.MANUFACTURER.equals("alps")) {
            if (mApi != null) {
                mApi.closeDev();
            }
            PowerUtil.power("0");
            try {
                if (mPrintQueue != null) {
                    mPrintQueue.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


