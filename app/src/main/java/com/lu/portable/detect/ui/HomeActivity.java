package com.lu.portable.detect.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ShellUtils;
import com.kernal.demo.plateid.R;
import com.lu.portable.detect.AccountActivity;
import com.lu.portable.detect.ResultActivity;
import com.lu.portable.detect.SocketActivity;
import com.lu.portable.detect.configtools.ConfigTools;
import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.eparking.KHTSample;
import com.lu.portable.detect.fragment.DetectFragment;
import com.lu.portable.detect.fragment.MineFragment;
import com.lu.portable.detect.fragment.RecordFragment;
import com.lu.portable.detect.model.Car;
import com.lu.portable.detect.model.Config;
import com.lu.portable.detect.model.ScaleDevice;
import com.lu.portable.detect.navigation.ViewPagerAdapter;
import com.lu.portable.detect.util.AxisTypeJsonHelper;
import com.lu.portable.detect.util.FileUtils;
import com.lu.portable.detect.util.LogUtil;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import static com.lu.portable.detect.configtools.InstrumentConfig.INSTRUMENT_IP;

public class HomeActivity extends SocketActivity {
    private final static String TAG = "HomeActivity";
    DetectFragment deviceFragment = DetectFragment.newInstance();
    KHTSample.FuncActivity camer1 = new KHTSample.FuncActivity();
    //  启动连接线程1
    public static int continuenext1 = -1;
    int connectnum1 = 0;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;
    private int[] titleIds = {R.string.detect_fragment_title, R.string.record_fragment_title, R.string.mine_fragment_title};
    boolean camerPowerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ConfigTools.logFileName = new Date().toString() + "checkLog.log";
        InstrumentConfig.initScaleDevices(SharedPreferencesUtil.getScaleMode());
        loadConfig();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.detectFileUriExposure();
        }
        if (mPrintQueue == null) {
            initPortablePrinter();
        }
        InstrumentConfig.cameraHasElect = false;
        viewPager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.item_detect:
                            switchToFragment(0);
                            break;
                        case R.id.item_record:
                            switchToFragment(1);
                            break;
                        case R.id.item_mine:
                            switchToFragment(2);
                            break;
                        default:
                            switchToFragment(0);
                            break;
                    }
                    return false;
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
        beginConnect();
        checkCameraStatus();
    }
    @Override
    public void playHightLight() {
        super.playHightLight();
        deviceFragment.playHightLight();
    }

    private void switchToFragment(int index) {
        viewPager.setCurrentItem(index);
        setTitle(titleIds[index]);
    }

    @Override
    public void updateListView() {
        super.updateListView();
        runOnUiThread(() -> deviceFragment.updateList());
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(deviceFragment);
        adapter.addFragment(RecordFragment.newInstance());
        adapter.addFragment(MineFragment.newInstance());
        viewPager.setAdapter(adapter);
    }

    public void startCheck() {
        if (continuenext1 == 1) {
            try {
                if (camerPowerOpen) {
                    camer1.Trigger_Button();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (InstrumentConfig.noScaleOnline()) {
            showToast(R.string.IDS_no_scale);
            return;
        }
        InstrumentConfig.carPlateNumFullPhoto = "";
        InstrumentConfig.carPlateNumPhoto = "";
        InstrumentConfig.carPlate = "未识别车牌";
        if (1 == SharedPreferencesUtil.getPlateMethod()) {
            startTakePhotoPlate();
        } else {
            beginCheck();
        }
    }

    private void beginCheck() {
        ConfigTools.writeLog("测量开始时间:" + new Date().toLocaleString());
        judgeCammera();
        cancelStatus = false;
        isDetectOver = false;
        InstrumentConfig.isCheckbtnClick = true;
        for (ScaleDevice scaleDevice : InstrumentConfig.scaleArr) {
            scaleDevice.speedArrayList.clear();
            scaleDevice.peakArrayList.clear();
            scaleDevice.timeArrayList.clear();
            scaleDevice.axisDistanceArrayList.clear();
            scaleDevice.setLastSixCommand("");
        }
        InstrumentConfig.resultAddressList.clear();
        if (camerPowerOpen) {
            sendCameraMsg(InstrumentConfig.generateSendMsg("03", "F5", InstrumentConfig.OPEN_CAMERA_LIGHT));
        }
        sendCheckList();
        pronounce("开始检测，请稍后");
        if (connectnum1 < 1) {
            //  showToast(R.string.IDS_no_camera);
            ConfigTools.writeLog("no camera");
        }
    }

    public void startTakePhotoPlate() {
        //手动拍照按钮
        Intent cameraIntent = new Intent(this, PlateidMainActivity.class);
        startActivityForResult(cameraIntent, 3004);
    }

    private void showProgressDialog() {
        super.initProgressDialog("正在计算重量，请稍等...");
    }

    public void startTest(View view) {
        startActivity(new Intent(this, TestActivity.class));
    }

    public void stopCheck() {
        isDetectOver = true;
        cancelStatus = false;
        isStopCheckingBtnClick = true;
        new Thread(() -> {
            if (camerPowerOpen) {
                sendCameraMsg(InstrumentConfig.generateSendMsg("03", "F5", InstrumentConfig.CLOSE_CAMERA_LIGHT));//light
            }
            stopCaculate();
        }).start();
        showProgressDialog();
        mMainHandler.postDelayed(runnable, 15000);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            closeProgressDialog(R.string.IDS_overtime);
            cancelStatus = true;
            Log.e(TAG, "fuckk!!!!!one");
            isStopCheckingBtnClick = false;
            InstrumentConfig.isCheckbtnClick = false;
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        closeProgressDialog();
    }

    public void cancelCheck() {
        InstrumentConfig.isCheckbtnClick = false;
        isDetectOver = true;
        cancelStatus = true;
        Log.e(TAG, "fuckk!!!!!two");
        new Thread(() -> {
            if (camerPowerOpen) {
                sendCameraMsg(InstrumentConfig.generateSendMsg("03", "F5", InstrumentConfig.CLOSE_CAMERA_LIGHT));
            }
            for (ScaleDevice scaleDevice : InstrumentConfig.scaleArr) {
                if (scaleDevice.getHeart() > 0 && scaleDevice.getAddress().length() > 0) {
                    String cancelCommand = InstrumentConfig.generateSendMsg(scaleDevice.getAddress(), "F2", InstrumentConfig.SLEEP);
                    LogUtil.d(TAG, cancelCommand);
                    sendScaleMsg(cancelCommand);
                }
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destoryPrinter();
        if (continuenext1 == 1) {
            camer1.Disconnect();
        }
        closeSocket();
    }


    public void startAccount(View view) {
        View confirm = LayoutInflater.from(this).inflate(R.layout.compensation_comfrim, null);
        final EditText passwordText = confirm.findViewById(R.id.password_confirm);
        DialogInterface.OnClickListener listener = (paramDialogInterface, paramInt) -> {
            String password = passwordText.getText().toString();
            String adminPassword = SharedPreferencesUtil.getUserPassword();
            LogUtil.d(TAG, "" + adminPassword);
            if (password.equals(adminPassword)) {
                startActivity(new Intent(HomeActivity.this, AccountActivity.class));
            } else {
                showToast(R.string.err_password);
            }
        };
        showCommonDialog(R.string.input_user_password, confirm, listener);
    }

    private void loadConfig() {
        try {
            InputStream localInputStream = getAssets().open("config/axisTypeMap.json");
            byte[] arrayOfByte = new byte[localInputStream.available()];
            localInputStream.read(arrayOfByte);
            localInputStream.close();
            String axisTypeConfig = new String(arrayOfByte);
            Log.d(TAG, "config/axisTypeMap.json:" + axisTypeConfig);
            Car.setAllTypeWeight(AxisTypeJsonHelper.newInstance(axisTypeConfig));
            Config.setSharedPreferences(this.getSharedPreferences("settings", 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     public void checkCameraOnline(){
        isAvailableByPing("192.168.88.55");
     }
    public void judgeCammera() {
        if (camerPowerOpen) {
            int isconnect1 = camer1.FConnect(INSTRUMENT_IP, this);
            Log.e(TAG, "isconnect1:" + isconnect1);
            if (isconnect1 != 1) {
                Toast.makeText(this, "相机连接失败", Toast.LENGTH_SHORT).show();
                LogUtil.v(TAG, "相机连接失败");
                continuenext1 = -1;
            } else {
                LogUtil.v(TAG, "相机连接成功");
                InstrumentConfig.startCamera =0;
                continuenext1 = 1;
                camer1.JosnOrBin();
                camer1.SetResu();
                camer1.MyThread();
                connectnum1++;
            }
        }
    }
    public static void isAvailableByPing(String ip) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ShellUtils.CommandResult result = ShellUtils.execCmd(String.format("ping -c 1 %s", ip), false);
                boolean ret = result.result == 0;
                if (result.errorMsg != null) {
                    Log.d("NetworkUtils", "isAvailableByPing() called" + result.errorMsg);
                }
                if (result.successMsg != null) {
                    Log.d("NetworkUtils", "isAvailableByPing() called" + result.successMsg);
                }
                InstrumentConfig.cameraConnected =ret;
            }
        }).start();
    }

    public void controlCameraPower(View view) {
        Button btn = (Button) view;
        LinearLayout disableLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.sensor_disable, null);
        TextView sensorNameTv = disableLayout.findViewById(R.id.sensor_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Log.e("InstrumentConfig.cameraHasElect", "::::" + InstrumentConfig.cameraHasElect);
        if (InstrumentConfig.cameraHasElect) {
            sensorNameTv.setText(getString(R.string.IDS_close_camera_power_hint));
            builder.setTitle(getString(R.string.IDS_close_camera_power_title)).setView(disableLayout).setPositiveButton("确定", (paramDialogInterface, paramInt) -> {
                closeCameraPower();
                btn.setTextColor(getResources().getColor(R.color.colorAccent));
                InstrumentConfig.cameraHasElect = false;
            });

        } else {
            sensorNameTv.setText(getString(R.string.IDS_open_camera_power_hint));
            builder.setTitle(getString(R.string.IDS_open_camera_power_title)).setView(disableLayout).setPositiveButton("确定", (paramDialogInterface, paramInt) -> {
                openCameraPower();
                InstrumentConfig.cameraHasElect = true;
                btn.setTextColor(getResources().getColor(R.color.white));
            });
        }
        builder.setNegativeButton(R.string.IDS_common_cancel, null).show();
    }

    public void openCameraPower() {
        new Thread(() -> sendMsg(InstrumentConfig.generateSendMsg("04", "F5", InstrumentConfig.OPEN_CAMERA_POWER))).start();
        camerPowerOpen = true;
        InstrumentConfig.cameraConnected =false;
    }
    long lastTime =0;
    long currentTime =0;
    public void checkCameraStatus(){
        Log.e(TAG,"checkCameraStatus");
        currentTime = System.nanoTime();
        if (lastTime == 0) {
            lastTime = currentTime;
        }
        //185747000
        if (currentTime - lastTime > 3 * 1000 * 1000 * 1000) {
            Log.e(TAG, "checkCameraStatus" + "lastTime" + lastTime + "currentTime" + currentTime + ":" + (currentTime - lastTime) / 1000 / 1000 / 1000);
            lastTime = currentTime;
            if (camerPowerOpen) {
                checkCameraOnline();
                Log.e(TAG,"checkCameraOnline");
            }
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkCameraStatus();
                }
            }, 3000);
        }
    }

    public void closeCameraPower() {
        Log.e(TAG,"closeCameraPower");
        new Thread(() -> sendMsg(InstrumentConfig.generateSendMsg("04", "F5", InstrumentConfig.CLOSE_CAMERA_POWER))).start();
        camerPowerOpen = false;
        InstrumentConfig.cameraConnected =false;
        if(connectnum1>1) {
            camer1.Disconnect();
        }
        continuenext1=-1;

    }

    public void showCarPlateNumber(byte[] bb) {

        if(InstrumentConfig.isCheckbtnClick) {
            try {
                String s = new String(bb, "gbk");
                JSONObject Json = new JSONObject(s);
                Log.e("Json", s);
                if (Json.getInt("count") == 0) {
                    //plateNumberEditText.setText("无牌车");
                    LogUtil.d(TAG, "未识别车牌");
                    InstrumentConfig.carPlate = "未识别车牌";
                } else {
                    final String license = new String(Json.getJSONArray("item").getJSONObject(0).getString("license").getBytes("GBK"), "GBK");
                    LogUtil.d(TAG, "牌车" + license);
                    InstrumentConfig.carPlate = license;
                }
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void showCarPlatePhoto(byte[] bb) {
        Log.d(TAG, "showCarPlatePhoto");
        if(InstrumentConfig.isCheckbtnClick) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bb, 0, bb.length);
            String picPATH = Environment.getExternalStorageDirectory().getPath() + "/portableBalance/carPlate/";
            File picDir = new File(picPATH);
            if (!picDir.exists()) {
                FileUtils.createDir(picPATH);
            }
            InstrumentConfig.carPlateNumFullPhoto = picPATH + System.nanoTime() + ".jpg";
            Log.e(TAG, InstrumentConfig.carPlateNumFullPhoto);
            File file = new File(InstrumentConfig.carPlateNumFullPhoto);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                //LogUtil.e(TAG, file.getAbsolutePath());
                // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 发送广播，通知刷新图库的显示
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + InstrumentConfig.carPlateNumPhoto)));
        }
    }

    public void logOUt() {
        InstrumentConfig.isLogin = false;
        closeSocket();
        startActivity(new Intent(this,       LoginActivity.class));
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( 3004== requestCode){
            beginCheck();
        }
        if (30003 == resultCode) {
            new Thread(() -> {
                try {
                    connectScaleSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } else if (30004 == resultCode) {
            beginCheck();
        }
    }

    public void showMiniCarPlatePhoto(byte[] bb) {
        if(InstrumentConfig.isCheckbtnClick) {
            Log.d(TAG, "showMiniCarPlatePhoto");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bb, 0, bb.length);
            String picPATH = Environment.getExternalStorageDirectory().getPath() + "/portableBalance/carPlate/";
            File picDir = new File(picPATH);
            if (!picDir.exists()) {
                FileUtils.createDir(picPATH);
            }
            InstrumentConfig.carPlateNumPhoto = picPATH + "mini" + System.nanoTime() + ".jpg";
            File file = new File(InstrumentConfig.carPlateNumPhoto);
            if (file.exists()) {
               boolean result= file.delete();
               if(result) {
                   Log.e(TAG, "delete carPlateNumPhoto Successs");
               }
            }
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                LogUtil.e(TAG, file.getAbsolutePath());
                // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 发送广播，通知刷新图库的显示
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + InstrumentConfig.carPlateNumPhoto)));
        }
    }

    @Override
    public void startResultActivity() {
        super.startResultActivity();
        mMainHandler.removeCallbacks(runnable);
        Log.e(TAG, "why?????");
        keepScaleOpen();
        closeProgressDialog();
        if (staticWeightStatus) {
            return;
        }
        if (cancelStatus) {
            return;
        }
        ConfigTools.writeLog("测量结束时间:" + new Date().toLocaleString());
        if (isDetectOver && isStopCheckingBtnClick) {
            startActivity(new Intent(this, ResultActivity.class));
            Log.e(TAG, "cuck?????");
            isStopCheckingBtnClick = false;
            InstrumentConfig.isCheckbtnClick = false;
            keepScaleOpen();
        }
    }
}