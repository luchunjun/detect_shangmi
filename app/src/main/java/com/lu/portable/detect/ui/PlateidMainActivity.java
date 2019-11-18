package com.lu.portable.detect.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kernal.demo.plateid.R;
import com.kernal.plateid.CoreSetup;
import com.kernal.plateid.activity.PlateidCameraActivity;
import com.kernal.plateid.controller.CommonTools;
import com.kernal.plateid.controller.ImportPicRecog;
import com.lu.portable.detect.BaseActivity;
import com.lu.portable.detect.configtools.InstrumentConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author user
 */
public class PlateidMainActivity extends BaseActivity implements View.OnClickListener {
    private TextView plateColor, plateId;
    private ImageView plateImage;
    private ImportPicRecog importPicRecog;
    private CoreSetup coreSetup = new CoreSetup();
    /**
     * 动态授权需要的权限
     */
    static final String[] PERMISSION = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE, Manifest.permission.INTERNET,
    };
    private static final int PERMISSION_REQUESTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_plate);
        setTitle(R.string.plateid_result);
        findViews();
        takePhoto();
    }

    private void takePhoto() {
        Intent cameraIntent = new Intent(PlateidMainActivity.this, PlateidCameraActivity.class);
        coreSetup.takePicMode = true;
        cameraIntent.putExtra("coreSetup", coreSetup);
        startActivityForResult(cameraIntent, 1);
    }

    private void findViews() {
        plateColor = findViewById(R.id.plateColor);
        plateId = findViewById(R.id.plateId);
        plateImage = findViewById(R.id.plateImage);
    }

    @Override
    public void onClick(View v) {
        if (R.id.confirm == v.getId()) {
            setResult(3004);
            finish();
        } else if (R.id.take_photo_again == v.getId()) {
            takePhoto();
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                //获取到的识别结果
                String[] recogResult = data.getStringArrayExtra("RecogResult");
                //保存图片路径
                String savePicturePath = data.getStringExtra("savePicturePath");
                String picPATH = Environment.getExternalStorageDirectory().getPath() + "/portableBalance/carPlate/";
                InstrumentConfig.carPlateNumPhoto = picPATH + "mini" + System.nanoTime() + ".jpg";
                InstrumentConfig.carPlateNumFullPhoto = savePicturePath;


                //是竖屏还是横屏
                int screenDirection = data.getIntExtra("screenDirection", 0);
                Bitmap bitmap;
                int left, top, w, h;//设置现在图片的区域

                if (recogResult[0] != null && !"".equals(recogResult[0])) {
                    left = Integer.valueOf(recogResult[7]);
                    top = Integer.valueOf(recogResult[8]);
                    w = Integer.valueOf(recogResult[9])
                            - Integer.valueOf(recogResult[7]);
                    h = Integer.valueOf(recogResult[10])
                            - Integer.valueOf(recogResult[8]);
                    plateId.setText(recogResult[0]);
                    InstrumentConfig.carPlate =recogResult[0];
                    plateColor.setText(recogResult[1]);
                } else {
                    if (screenDirection == 1 || screenDirection == 3) {
                        left = coreSetup.preHeight / 24;
                        top = coreSetup.preWidth / 4;
                        w = coreSetup.preHeight / 24 + coreSetup.preHeight * 11 / 12;
                        h = coreSetup.preWidth / 3;
                    } else {
                        left = coreSetup.preWidth / 4;
                        top = coreSetup.preHeight / 4;
                        w = coreSetup.preWidth / 2;
                        h = coreSetup.preHeight - coreSetup.preHeight / 2;
                    }
                    plateId.setText("null");
                    plateColor.setText("null");
                }
                bitmap = BitmapFactory.decodeFile(savePicturePath);
                if (bitmap != null) {
                    bitmap = Bitmap.createBitmap(bitmap, left, top, w, h);
                    saveMyBitmap(InstrumentConfig.carPlateNumPhoto,bitmap);
                    plateImage.setImageBitmap(bitmap);
                }
            }
        } else {
            if (data != null) {
                Uri uri = data.getData();
                String picPathString = CommonTools.getPath(PlateidMainActivity.this, uri);
                //初始化和识别接口要有一个时间段，所以将初始化放在了上面，这里要注意下
                //传入图片识别获取结果
                String[] recogResult = importPicRecog.recogPicResults(picPathString);
                plateId.setText(recogResult[0]);
                plateColor.setText(recogResult[1]);
                Bitmap bitmap = BitmapFactory.decodeFile(picPathString);
                plateImage.setImageBitmap(bitmap);
            }
        }
    }

    public void saveMyBitmap(final String bitName, final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(bitName);
                try {
                    file.createNewFile();
                    FileOutputStream fOut = new  FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUESTCODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户点击了同意授权
                    importPicRecog = new ImportPicRecog(PlateidMainActivity.this);
                    Intent selectIntent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    Intent wrapperIntent = Intent.createChooser(selectIntent, "请选择一张图片");
                    startActivityForResult(wrapperIntent, 2);
                } else {
                    //用户拒绝了授权
                    Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
