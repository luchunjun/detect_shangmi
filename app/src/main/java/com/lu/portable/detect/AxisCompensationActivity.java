package com.lu.portable.detect;

import android.content.Intent;
import android.os.Bundle;

import android.widget.ListView;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.adaptor.CarTypeAdaptor;
import com.lu.portable.detect.model.Car;

import java.util.ArrayList;
import java.util.HashMap;

public class AxisCompensationActivity extends BaseActivity {
    private int axisType;
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_axis_compensation_list);
        axisType = getIntent().getIntExtra("axisType",3);
        setTitle(axisType+"轴车"+getString(R.string.compensation_by_truck_type));
        initView();
    }

    private void initView() {
        ListView listView = findViewById(R.id.list_view);
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        String codes[];
        String labels[];
        int icons[];
        int limit[];
        if (axisType == 3) {
            codes = Car.threeAxisesTypes;
            icons = Car.threeAxisesTypeIcons;
            labels = Car.threeLabel;
            limit =Car.threeLimit;
        } else if (axisType == 4) {
            codes = Car.fourAxisesTypes;
            icons = Car.fourAxisesTypeIcons;
            labels = Car.fourLabel;
            limit =Car.fourLimit;
        } else if (axisType == 5) {
            codes = Car.fiveAxisesTypes;
            icons = Car.fiveAxisesTypeIcons;
            labels = Car.fiveLabel;
            limit =Car.fiveLimit;
        } else {
            codes = Car.sixAxisesTypes;
            icons = Car.sixAxisesTypeIcons;
            labels = Car.sixLabel;
            limit =Car.sixLimit;
        }
        for(int i=0;i<codes.length;i++){
            HashMap<String, Object> map = new HashMap<>();
            map.put(CarTypeAdaptor.CODE,codes[i]);
            map.put(CarTypeAdaptor.IMAGE,getResources().getDrawable(icons[i]));
            map.put(CarTypeAdaptor.TITLE,icons[i]);
            map.put(CarTypeAdaptor.SUB_TITLE,labels[i]+" 限重"+limit[i] + "t");
            data.add(map);
        }

        CarTypeAdaptor carTypeAdaptor =new CarTypeAdaptor(data,this);
        listView.setAdapter(carTypeAdaptor);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String key= (String)data.get(position).get(CarTypeAdaptor.CODE);
            int icon= (int)data.get(position).get(CarTypeAdaptor.TITLE);
            if(key.equals("t3_1_1_1") || key.equals("t3_1_1_1_s")||key.equals("t4_1_1_1_1") ){
                startActivity(new Intent(AxisCompensationActivity.this,CompensationCommonCarWeightActivity.class
                ).putExtra("axis",axisType).putExtra("truckDetailType",key).putExtra("truckDetailTypeIcon",icon));
               // return;
            }else{
                startActivity(new Intent(AxisCompensationActivity.this,TwoConnectCompensationActivity.class
                ).putExtra("axis",axisType).putExtra("truckDetailType",key).putExtra("truckDetailTypeIcon",icon));
            }
        });
    }
}