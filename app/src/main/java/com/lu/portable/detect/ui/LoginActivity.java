package com.lu.portable.detect.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.BaseActivity;
import com.lu.portable.detect.PortableBalanceApplication;
import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.database.DatabaseAdapter;
import com.lu.portable.detect.util.FileUtils;
import com.lu.portable.detect.util.LogUtil;
import com.lu.portable.detect.util.Md5;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import com.lu.portable.detect.util.WifiUtil;
import com.lu.portable.detect.view.RoundedImageView;


import java.util.ArrayList;


public class LoginActivity extends BaseActivity {
    private final static String TAG = "LoginActivity";
    RoundedImageView userImage;
    TextView userNameTextView;
    private ArrayList<Integer> idList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> passwordList = new ArrayList<>();
    private int userIndex = 0;
    private EditText passwordEdit;
    /**动态授权需要的权限*/
    static final String[] PERMISSION = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE, Manifest.permission.INTERNET,
    };
    private static final int PERMISSION_REQUESTCODE = 1;
    public void initAllDefaultParameter() {
        initSharePreferences("groupName", "01班组");
        initSharePreferences("banshouName", "18:7A:93:04:A3:4A");
        initSharePreferences("banshouWatchTime", "10");
        initSharePreferences("dataWatchTime", "7");
    }
    private void initSharePreferences(String key, String value) {
        final String defaultValue = "";
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getString(key, defaultValue).equals(defaultValue)) {
            editor.putString(key, value);
        }
        editor.apply();
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);
        grandPermissons();
      //  Log.i("哈哈", "Config.FILE_CONFIGfile");
        getUserData();
        initViews();
        initAllDefaultParameter();
    }




    private void grandPermissons() {
        Log.d(TAG, "grandPermissons");
        String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.BLUETOOTH", "android.permission.CAMERA"};
        for (String permStr : permissions) {
            if (ContextCompat.checkSelfPermission(this, permStr) != 0) {
                ActivityCompat.requestPermissions(this, new String[]{permStr}, 1);
            }
        }
    }

    private void getUserData() {
        Cursor cursor = DatabaseAdapter.getDatabaseAdapter(getApplicationContext()).getAllUsersInfo();
        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    idList.add(cursor.getInt(0));
                    nameList.add(cursor.getString(1));
                    passwordList.add(cursor.getString(2));
                } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public void login(View view) {
        InstrumentConfig.isCameraPowerLoginClose=false;
        RadioButton twoScaleRadio = findViewById(R.id.twoScaleRadioBtn);
        RadioButton fourScaleRadio = findViewById(R.id.fourScaleRadioBtn);
       // pronounce("无情不误人，有情多伤心");
        if (twoScaleRadio.isChecked()) {
            SharedPreferencesUtil.setScaleMode(2);
        } else if (fourScaleRadio.isChecked()) {
            SharedPreferencesUtil.setScaleMode(4);
        } else {
//            pronounce(getString(R.string.IDS_select_scale_mode_hint));
//            showToast(R.string.IDS_select_scale_mode_hint);
            return;
        }

        if ( WifiUtil.isSameNetWithInstrument(this)) {
            LogUtil.e(TAG, "instrument is connected!");
        } else {
           // pronounce(getString(R.string.IDS_not_connected_instrument));
            showToast(R.string.IDS_not_connected_instrument);
        }
        String password = passwordEdit.getText().toString();
        if (passwordList.size() > userIndex) {
            if (new Md5(password).get32().equals(passwordList.get(userIndex))) {
                PortableBalanceApplication.setUserId(idList.get(userIndex));
                PortableBalanceApplication.setUserName(nameList.get(userIndex));
                SharedPreferencesUtil.setUserId("" + idList.get(userIndex));
                SharedPreferencesUtil.setUserName(nameList.get(userIndex));
                SharedPreferencesUtil.setUserPassword(password);
                InstrumentConfig.isLogin = true;
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();

            } else {
                pronounce(getString(R.string.err_password));
                showToast(R.string.err_password);
            }
        } else {
            if (new Md5(password).get32().equals(new Md5("123456").get32())) {
                PortableBalanceApplication.setUserId(206466);
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                showToast(R.string.err_password);
            }
        }
    }

    private void initViews() {
        TextView copyrightTextiew = findViewById(R.id.copyrighttextview);
        userImage = findViewById(R.id.userlogo);
        userNameTextView = findViewById(R.id.username);
        try {
            PackageManager pm =getPackageManager();
            PackageInfo pi =pm.getPackageInfo(getPackageName(),0);
            String versionName = pi.versionName;
            int versioncode=pi.versionCode;
            copyrightTextiew.setText(String.format(getString(R.string.copy_string), SharedPreferencesUtil.getCompanyName()+versionName));
            copyrightTextiew.setVisibility(View.VISIBLE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (nameList.size() > userIndex) {
            updateUserView(userIndex);
        } else {
            userNameTextView.setText(R.string.administrator);
            userImage.setImageResource(R.mipmap.user_icon);
        }
        passwordEdit = findViewById(R.id.password);
        LogUtil.d(TAG,"password"+SharedPreferencesUtil.getUserPassword());
        passwordEdit.setText(SharedPreferencesUtil.getUserPassword());
        passwordEdit.setSelection(passwordEdit.getText().length());
    }

    public void preUser(View view) {
        if (userIndex > 0) {
            updateUserView(--userIndex);
        }
    }

    public void nextUser(View view) {
        if (userIndex < idList.size() - 1) {
            updateUserView(++userIndex);
        }
    }

    private void updateUserView(int index) {
        // onConn();
        userNameTextView.setText(nameList.get(index));
        Bitmap bitmap = FileUtils.getImageDrawable(this, "" + idList.get(index));
        if (bitmap != null) {
            userImage.setImageBitmap(bitmap);
        } else {
            userImage.setImageResource(R.mipmap.user_icon);
        }
    }


}