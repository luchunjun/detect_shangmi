package com.lu.portable.detect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.portable.detect.adaptor.IconMenuAdaptor;
import com.lu.portable.detect.ui.ActionSheet;
import com.lu.portable.detect.ui.ModifyMineInfoActivity;
import com.lu.portable.detect.util.FileUtils;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import com.lu.portable.detect.view.MyImageView;
import com.kernal.demo.plateid.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class AccountActivity extends BaseActivity implements ActionSheet.MenuItemClickListener {
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;
    private final int PERMISSION_READ_AND_CAMERA = 0;//读和相机权限
    private final int PERMISSION_READ = 1;//读取权限
    //改变头像的标记位
    private int new_icon = 0xa3;
    private MyImageView headImage = null;
    private Uri mUriPath;
    private TextView companyNameTv;
    private TextView userNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_test);
        setTitle(R.string.account_info);
        ListView listView = findViewById(R.id.list_view);
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        companyNameTv = findViewById(R.id.companyNameTv);
        userNameTv = findViewById(R.id.usernameTv);
        userNameTv.setText(SharedPreferencesUtil.getUserName());
        final int titleIds[] = {R.string.modify_password, R.string.modify_company, R.string.add_user, R.string.IDS_delete_user};
        final int imgIds[] = {R.drawable.ic_lock_blue_24dp, R.drawable.ic_company_blue_24dp, R.drawable.ic_user_add_blue_24dp, R.drawable.ic_del_user};
        int supportCount = 1;
        if (SharedPreferencesUtil.getUserId().equals("1")) {
            supportCount = 4;
        }
        for (int i = 0; i < supportCount; i++) {//overlook member
            HashMap<String, Object> map = new HashMap<>();
            map.put(IconMenuAdaptor.TITLE, getString(titleIds[i]));
            map.put(IconMenuAdaptor.IMAGE, getResources().getDrawable(imgIds[i]));
            list.add(map);
        }
        IconMenuAdaptor mIconMenuAdaptor = new IconMenuAdaptor(list, this);
        listView.setAdapter(mIconMenuAdaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (position < 3) {
                    startActivity(new Intent(AccountActivity.this, ModifyMineInfoActivity.class).putExtra("type", position).putExtra("title", titleIds[position]));
                } else if (position == 3) {
                    startActivity(new Intent(AccountActivity.this, DelUserActivity.class));
                }
            }
        });
        headImage = findViewById(R.id.imageView);
        setImageToHeadView();
    }

    @Override
    public void onResume() {
        super.onResume();
        companyNameTv.setText(SharedPreferencesUtil.getCompanyName());
        userNameTv.setText(PortableBalanceApplication.getUserName());
    }

    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
        intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    private void choseHeadImageFromCameraCapture() {
        Intent intent;
        if (FileUtils.isSdCardAvailable()) {
            //设定拍照存放到自己指定的目录,可以先建好
            Uri pictureUri;
            File pictureFile = new File(FileUtils.getDetectDir(), FileUtils.getUserIconName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                pictureUri = FileProvider.getUriForFile(this, "com.wintone.demo.plateid.fileProvider", pictureFile);
            } else {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pictureUri = Uri.fromFile(pictureFile);
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    pictureUri);
            startActivityForResult(intent, CODE_CAMERA_REQUEST);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_CANCELED) {
            showToast(R.string.IDS_common_cancel);
            return;
        }
        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                cropRawPhoto(intent.getData());
                break;
            case CODE_CAMERA_REQUEST:
                if (FileUtils.isSdCardAvailable()) {
                    File tempFile = new File(
                            FileUtils.getDetectDir(),
                            FileUtils.getUserIconName());
                    cropRawPhoto(FileUtils.getImageContentUri(this, tempFile));
                } else {
                    Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            case CODE_RESULT_REQUEST:
                Log.d("CODE_RESULT_REQUEST", mUriPath.getPath());
                setImageToHeadView();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void checkStoragePermission() {
        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {/*Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ,*/Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_READ_AND_CAMERA);
        } else {
            choseHeadImageFromCameraCapture();
        }
    }

    private void checkReadPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_READ);
        } else {
            choseHeadImageFromGallery();
        }
    }

    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_AND_CAMERA:
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "why ??????", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                choseHeadImageFromCameraCapture();
                break;
            case PERMISSION_READ:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choseHeadImageFromGallery();
                }
                break;
        }

    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);
        File mFile = new File(FileUtils.getDetectDir(), FileUtils.getUserIconName());
        mUriPath = Uri.parse("file://" + mFile.getAbsolutePath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPath);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("return-data", false);
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    private void setImageToHeadView() {
        Bitmap bitmap = FileUtils.getImageDrawable(this, SharedPreferencesUtil.getUserId());
        if (bitmap != null) {
            headImage.setImageBitmap(bitmap);
        }
    }

    public void changeHeadImage(View view) {
        setTheme(R.style.ActionSheetStyleIOS7);
        ActionSheet menuView = new ActionSheet(this);
        menuView.setCancelButtonTitle(getString(R.string.IDS_common_cancel));
        menuView.addItems(getString(R.string.IDS_common_album), getString(R.string.IDS_common_take_photo));
        menuView.setItemClickListener(this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();
    }

    @Override
    public void onItemClick(int itemPosition) {
        if (0 == itemPosition) {
            checkReadPermission();
        } else if (1 == itemPosition) {
            checkStoragePermission();
        }
    }
}