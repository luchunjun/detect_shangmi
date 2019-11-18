package com.lu.portable.detect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lu.portable.detect.view.TouchImageView;
import com.kernal.demo.plateid.R;


public class SingleTouchImageViewActivity extends BaseActivity
{
    protected void onCreate(android.os.Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.activity_single_touchimageview);
        setTitle(R.string.big_car_pic);
        String path = getIntent().getStringExtra("path");
        TouchImageView image = findViewById(R.id.img);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        image.setImageBitmap(bitmap);
    }
}