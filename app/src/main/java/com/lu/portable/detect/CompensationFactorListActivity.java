package com.lu.portable.detect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lu.portable.detect.adaptor.CommonCompenstationAdaptor;
import com.lu.portable.detect.adaptor.IconMenuAdaptor;
import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CompensationFactorListActivity extends BaseActivity {


    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_compensation_factor_list);
        setTitle(R.string.title_activity_compensationFactorList);
        ListView listView = findViewById(R.id.list_view);
        ArrayList <HashMap <String, Object>> list = new ArrayList <>();
        String axis_types[] = getResources().getStringArray(R.array.axis_types);
        for (int i = 1; i < axis_types.length - 1; i++) {//overlook member
            HashMap <String, Object> map = new HashMap <>();
            map.put(IconMenuAdaptor.TITLE, axis_types[i]);
            list.add(map);
        }
        IconMenuAdaptor mIconMenuAdaptor = new IconMenuAdaptor(list, this);
        listView.setAdapter(mIconMenuAdaptor);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if(position ==0){
                startActivity(new Intent(this, CompensationCommonCarWeightActivity.class)
                        .putExtra("axis", 2).putExtra("truckDetailType","t2_1_1"));
            }else{
//                startActivity(
//                        new Intent(this, CompensationFactorActivity.class)
//                                .putExtra("flag", position + 2));
                startActivity(new Intent(this, AxisCompensationActivity.class)
                        .putExtra("axisType", position+2));

            }
        });
    }


    public void startSpeedCompensation(View view) {
        startActivity(new Intent(this, CompensationSpeedFactorActivity.class));
    }
}