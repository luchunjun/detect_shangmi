package com.lu.portable.detect.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kernal.demo.plateid.R;

public class CompensationFactorItem {
    private EditText compensationValue;
    private Context context;
    private int imgId = 0;
    private String tag;
    private String title;
    private int value = 100;

    public CompensationFactorItem(String title, int value, String tag, Context context) {
        this.title = title;
        this.value = value;
        this.tag = tag;
        this.context = context;
    }

    public CompensationFactorItem(String title, int value, String tag, Context context, int imgId) {
        this.title = title;
        this.value = value;
        this.tag = tag;
        this.context = context;
        this.imgId = imgId;
    }

    public static CompensationFactorItem create(String title, int value, String tag, Context context) {
        return new CompensationFactorItem(title, value, tag, context);
    }

    public static CompensationFactorItem create(String title, int value, String tag, Context context, int imgId) {
        return new CompensationFactorItem(title, value, tag, context, imgId);
    }

    public int getModifiedValue() {
        return Integer.parseInt(this.compensationValue.getText().toString());
    }

    public String getTag() {
        return this.tag;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
        compensationValue.setText(value + "");
    }

    public View getView() {
        View compensationView = LayoutInflater.from(context).inflate(R.layout.compensation_view_layout, null);
        ImageView truckImage = compensationView.findViewById(R.id.truckImage);
        if (imgId != 0) {
            truckImage.setImageResource(imgId);
        }
        compensationValue = compensationView.findViewById(R.id.compensationValue);
        ((TextView) compensationView.findViewById(R.id.compensationTitle)).setText(title + "");
        compensationValue.setText(value + "");
        compensationValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setValue(Integer.parseInt(compensationValue.getText().toString()));
                }
            }
        });
        return compensationView;
    }
}