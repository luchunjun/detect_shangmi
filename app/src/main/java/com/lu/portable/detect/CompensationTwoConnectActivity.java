package com.lu.portable.detect;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.model.Car;
import com.lu.portable.detect.util.SharedPreferencesUtil;

public class CompensationTwoConnectActivity extends BaseActivity {
    String truckDetailType;
    String connectType;
    int truckDetailTypeIcon;
    EditText twoConnect;
    String key;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_compensation_two_connect_car);
        truckDetailType=getIntent().getStringExtra("truckDetailType");
        connectType=getIntent().getStringExtra("connectType");
        int axis=getIntent().getIntExtra("axis",3);
        key =truckDetailType+connectType;
        int axisIconResource ;
        int titleId =-1;
        if(connectType.equals("_2c")){
            titleId =R.string.two_connect_compensation;
            axisIconResource =R.mipmap.t2c;
        }else if(connectType.equals("_3c")){
            titleId =R.string.three_connect_compensation;
            axisIconResource =R.mipmap.t3c;
        }else if(connectType.equals("_2l")){
            titleId =R.string.two_long_compensation;
            axisIconResource =R.mipmap.t2l;
        }else{
            titleId =R.string.three_long_compensation;
            axisIconResource =R.mipmap.t3l;
        }

        setTitle(Car.getCarLabel(axis,truckDetailType)+"型车" +getString(titleId));
        truckDetailTypeIcon =getIntent().getIntExtra("truckDetailTypeIcon",R.mipmap.t3_2_1);
        ImageView icon =findViewById(R.id.icon);
        ImageView axisIcon =findViewById(R.id.axisIcon);
        icon.setBackgroundResource(truckDetailTypeIcon);
        axisIcon.setBackgroundResource(axisIconResource);
        twoConnect =findViewById(R.id.twoConnect);
        twoConnect.setText(""+SharedPreferencesUtil.getFactorInt(key,100));
    }

    public  void saveCompensation(View view){
        SharedPreferencesUtil.setInt(key,Integer.parseInt(twoConnect.getText().toString()));
        showToast(R.string.save_success);
        finish();
    }

}