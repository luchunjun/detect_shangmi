package com.lu.portable.detect;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.adaptor.CommonCompenstationAdaptor;
import com.lu.portable.detect.model.Car;
import com.lu.portable.detect.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class CompensationCommonCarWeightActivity extends BaseActivity {
    private int axis;
    ListView listView;
    ArrayList<HashMap<String, Object>> data = new ArrayList<>();
    CommonCompenstationAdaptor adaptor;
    String titles_2[] = {"5t以下", "5t～10t", "10t～16t", "16t～22t", "22t以上"};
    String titles_3[] = {"8t以下", "8t～16t", "16t～24t", "24t～32t", "32t以上"};
    String titles_4[] = {"11t以下", "11t～22t", "22t～32t", "24t～32t", "43t以上"};
    String titles_5[] = {"13t以下", "13t～26t", "26t～39t", "39t～52t", "52t以上"};
    String titles_6[] = {"15t以下", "15t～30t", "30t～44t", "44t～59t", "59t以上"};
    String titles_more[] = {"60t以下", "60t～100t", "100t～200t", "200t～300t", "300t以上"};
    String truckDetailType;
    int truckDetailTypeIcon;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_compensation_comon_weight);
        axis = getIntent().getIntExtra("axis", 2);
        truckDetailType = getIntent().getStringExtra("truckDetailType");
        truckDetailTypeIcon = getIntent().getIntExtra("truckDetailTypeIcon",R.mipmap.t2_1_1);
        ImageView imageView = findViewById(R.id.typeIcon);
        imageView.setBackgroundResource(truckDetailTypeIcon);
        setTitle(Car.getCarLabel(axis,truckDetailType)+getString(R.string.common_car_weight_compensation));
        initView();
    }

    private void produceData(int axis) {
        String titleData[];
        switch (axis) {
            case 2:
                titleData = titles_2;
                break;
            case 3:
                titleData = titles_3;
                break;
            case 4:
                titleData = titles_4;
                break;
            case 5:
                titleData = titles_5;
                break;
            case 6:
                titleData = titles_6;
                break;
            default:
                titleData = titles_2;
                break;
        }
        //if (truckDetailType.equals("t2")) {
            for (int i = 0; i < titleData.length; i++) {
                HashMap<String, Object> map = new HashMap<>();
                String key = truckDetailType + "_w_" + i;
                map.put(CommonCompenstationAdaptor.TITLE, titleData[i]);
                map.put(CommonCompenstationAdaptor.SUB_TITLE, "" + SharedPreferencesUtil.getFactorInt(key, 100));
                map.put(CommonCompenstationAdaptor.CODE, key);
                data.add(map);
            }
        //}
    }

    private void initView() {
        listView = findViewById(R.id.list_view);
        produceData(axis);
        adaptor = new CommonCompenstationAdaptor(data, this);
        listView.setAdapter(adaptor);
    }


    public void saveWeightCompensation(View view) {
        for (HashMap map : data) {
            String key = (String) map.get(CommonCompenstationAdaptor.CODE);
            int result = Integer.parseInt((String) CommonCompenstationAdaptor.values.get(key));
            SharedPreferencesUtil.setInt(key, result);
        }
        showToast(R.string.save_success);
        finish();
    }
}