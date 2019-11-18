package com.lu.portable.detect;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.lu.portable.detect.adaptor.CompensationMenuAdaptor;
import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.model.CompensationFactorItem;
import com.lu.portable.detect.util.CompensationJsonHelper;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompensationSpeedFactorActivity extends BaseActivity {
    CompensationJsonHelper compensationJsonHelper;
    ListView listView;
    ArrayList <HashMap <String, Object>> data;
    ArrayList <String> keys = new ArrayList <>();
    CompensationMenuAdaptor adaptor;
   // private Button resetCompensationFactorButton;
    private Button saveCompensationFactorButton;
   // private ArrayList <CompensationFactorItem> settingItems = new ArrayList();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("SpeedFactor", "OnCreate");
        setContentView(R.layout.activity_compensation_speed_factor);
        setTitle(R.string.compensation_by_speed);
        setUpView();
    }

    private void setUpView() {
        data = new ArrayList <>();
        listView = findViewById(R.id.list_view);
        compensationJsonHelper = CompensationJsonHelper.create(this);
        initSpeedView();
        saveCompensationFactorButton = findViewById(R.id.saveCompensationFactorButton);
        saveCompensationFactorButton.setOnClickListener(view -> {
            for (String key : keys) {
                if(SharedPreferencesUtil.getScaleMode()==4) {
                    SharedPreferencesUtil.setInt(key+"_big", Integer.parseInt((String) adaptor.values.get(key)));
                }else{
                    SharedPreferencesUtil.setInt(key, Integer.parseInt((String) adaptor.values.get(key)));
                }
            }
            showToast(R.string.save_success);
            finish();
        });
    }

    private void initSpeedView() {
        CompensationJsonHelper.SpeedType speedType = compensationJsonHelper.getSpeed();
        List <CompensationJsonHelper.SpeedType.SpeedCompensation> speedCompensations = speedType.getItems();
        for (CompensationJsonHelper.SpeedType.SpeedCompensation speedCompensation : speedCompensations) {
            String key = speedCompensation.getKey();
            if(SharedPreferencesUtil.getScaleMode()==4) {
                //speedCompensation.getValue()
                speedCompensation.setValue(SharedPreferencesUtil.getFactorInt(key+"_big", 100));
            }else{
                speedCompensation.setValue(SharedPreferencesUtil.getFactorInt(key, 100));
            }
            HashMap <String, Object> hashMap = new HashMap <>();
            hashMap.put(CompensationMenuAdaptor.TITLE, speedCompensation.getName());
            hashMap.put(CompensationMenuAdaptor.SUB_TITLE, String.valueOf(speedCompensation.getValue()));
            hashMap.put(CompensationMenuAdaptor.CODE, speedCompensation.getKey());
            hashMap.put(CompensationMenuAdaptor.IMAGE, getResources().getDrawable(R.mipmap.t2_1_1));
            data.add(hashMap);
            keys.add(speedCompensation.getKey());
        }
        adaptor = new CompensationMenuAdaptor(data, this);
        listView.setAdapter(adaptor);
    }
}