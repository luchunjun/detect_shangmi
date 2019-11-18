package com.lu.portable.detect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.model.Car;

public class TwoConnectCompensationActivity extends BaseActivity {
    String truckDetailType;
    int truckDetailTypeIcon;
    private int axis;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_compensation_two_connect);
        truckDetailTypeIcon = getIntent().getIntExtra("truckDetailTypeIcon", R.mipmap.t3_2_1);
        axis = getIntent().getIntExtra("axis", 3);
        ImageView icon = findViewById(R.id.icon);
        icon.setBackgroundResource(truckDetailTypeIcon);
        truckDetailType = getIntent().getStringExtra("truckDetailType");
        TextView two_connect_axis = findViewById(R.id.two_connect_axis);
        TextView two_long_axis = findViewById(R.id.two_long_axis);
        TextView three_connect_axis = findViewById(R.id.three_connect_axis);
        TextView three_long_axis = findViewById(R.id.three_long_axis);
        setTitle(Car.getCarLabel(axis, truckDetailType) + getString(R.string.car_type_compensation));
        if (truckDetailType.equals("t3_2_1") || truckDetailType.equals("t4_1_1_2_0") || truckDetailType.equals("t5_1_1_1_1_1")) {
            two_long_axis.setVisibility(View.VISIBLE);
        }
        if (truckDetailType.equals("t3_1_2") || truckDetailType.equals("t4_1_1_2_0") || truckDetailType.equals("t4_1_1_2_s") || truckDetailType.equals("t4_1_1_2")
                || truckDetailType.equals("t4_1_2_1") || truckDetailType.equals("t5_1_1_1_2") || truckDetailType.equals("t5_1_2_2") || truckDetailType.equals("t5_1_1_1_2_s")
                || truckDetailType.equals("t5_1_2_2_s") || truckDetailType.equals("t5_1_2_1_1") || truckDetailType.equals("t6_1_1_2_2") || truckDetailType.equals("t6_1_1_2_2_49")
                || truckDetailType.equals("t6_1_1_2_1_1") || truckDetailType.equals("t6_1_1_2_1_1_49") || truckDetailType.equals("t6_1_2_3") || truckDetailType.equals("t6_1_2_3_49") || truckDetailType.equals("t6_1_2_3_s") || truckDetailType.equals("t6_1_2_3_s_49")) {
            two_connect_axis.setVisibility(View.VISIBLE);
        }
        if (truckDetailType.equals("t5_1_1_3") || truckDetailType.equals("t6_1_1_1_3") || truckDetailType.equals("t6_1_2_3") || truckDetailType.equals("t6_1_2_3_49") || truckDetailType.equals("t6_1_2_3_s") || truckDetailType.equals("t6_1_2_3_s_49")) {
            three_connect_axis.setVisibility(View.VISIBLE);
        }
        if (truckDetailType.equals("t5_1_1_1_2") || truckDetailType.equals("t5_1_1_1_2_s") || truckDetailType.equals("t6_1_1_1_3")) {
            three_long_axis.setVisibility(View.VISIBLE);
        }
    }

    public void startTwoConnectCompensation(View view) {
        startActivity(new Intent(TwoConnectCompensationActivity.this, CompensationTwoConnectActivity.class
        ).putExtra("truckDetailType", truckDetailType).putExtra("truckDetailTypeIcon", truckDetailTypeIcon)
                .putExtra("connectType", "_2c").putExtra("axis", axis));
        finish();
    }

    public void startThreeConnectCompensation(View view) {
        startActivity(new Intent(TwoConnectCompensationActivity.this, CompensationTwoConnectActivity.class
        ).putExtra("truckDetailType", truckDetailType).putExtra("truckDetailTypeIcon", truckDetailTypeIcon)
                .putExtra("connectType", "_3c").putExtra("axis", axis));
        finish();
    }

    public void startTwoLongCompensation(View view) {
        startActivity(new Intent(TwoConnectCompensationActivity.this, CompensationTwoConnectActivity.class
        ).putExtra("truckDetailType", truckDetailType).putExtra("truckDetailTypeIcon", truckDetailTypeIcon)
                .putExtra("connectType", "_2l").putExtra("axis", axis));
        finish();
    }

    public void startThreeLongCompensation(View view) {
        startActivity(new Intent(TwoConnectCompensationActivity.this, CompensationTwoConnectActivity.class
        ).putExtra("truckDetailType", truckDetailType).putExtra("truckDetailTypeIcon", truckDetailTypeIcon)
                .putExtra("connectType", "_3l").putExtra("axis", axis));
        finish();
    }

    public void startWeightFragmentCompensation(View view) {
        startActivity(new Intent(TwoConnectCompensationActivity.this, CompensationCommonCarWeightActivity.class
        ).putExtra("axis", axis).putExtra("truckDetailType", truckDetailType).putExtra("truckDetailTypeIcon", truckDetailTypeIcon));
        finish();
    }
}