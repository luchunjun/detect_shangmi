package com.lu.portable.detect;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lu.portable.detect.model.CompensationFactorItem;
import com.lu.portable.detect.util.CompensationJsonHelper;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompensationFactorActivity extends BaseActivity {
    public int compensationType = 0;
    HashMap <String, Integer> truckKeyToThumb;
    LinearLayout compensationFactorSettingItemsLayout;
    CompensationJsonHelper compensationJsonHelper;
  //  private Button resetCompensationFactorButton;
    private Button saveCompensationFactorButton;
    private ArrayList <CompensationFactorItem> settingItems = new ArrayList();

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_compensation_factor);
        compensationType = getIntent().getIntExtra("flag", 1);
        saveCompensationFactorButton = findViewById(R.id.saveCompensationFactorButton);
       // resetCompensationFactorButton = findViewById(R.id.resetCompnesationFactorButton);
        setUpView();
        saveCompensationFactorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                for (CompensationFactorItem item : settingItems) {
                    item.setValue(item.getModifiedValue());
                    SharedPreferencesUtil.setInt(item.getTag(), item.getModifiedValue());
                }
            }
        });
//        resetCompensationFactorButton.setOnClickListener(new View.OnClickListener() {
////            public void onClick(View view) {
////                resetPreferences();
////            }
////        });
    }

    private void resetPreferences() {
        for (CompensationFactorItem item : settingItems) {
            item.setValue(item.getValue());
        }
    }

    @SuppressLint({"StringFormatMatches"})
    private void setUpView() {
        compensationFactorSettingItemsLayout = findViewById(R.id.compensationFactorSettingItems);
        compensationJsonHelper = CompensationJsonHelper.create(this);
        initAxisView();
    }

    private void initAxixImg() {
        truckKeyToThumb = new HashMap <>();
        switch (compensationType) {
            case 0:
                break;
            case 2://2轴车
                truckKeyToThumb.put("cf_2|1_1|total", R.mipmap.t2_1_1);
                break;
            case 3:
                truckKeyToThumb.put("cf_3|1_2|total", R.mipmap.t_3_zh1);//wheel1_2
                truckKeyToThumb.put("cf_3|1_2|2", R.mipmap.t_3_zh2);
                truckKeyToThumb.put("cf_3|2_1|total", R.mipmap.wheel2_1);
                truckKeyToThumb.put("cf_3|2_1|2", R.mipmap.w3t2_1b2);
                break;
            case 4:
                truckKeyToThumb.put("cf_4|1_1_2|total", R.mipmap.four_1_1__2_36);
                truckKeyToThumb.put("cf_4|1_1_2|2", R.mipmap.four_1_1__2_36);
                break;
            case 5:
                truckKeyToThumb.put("cf_5|1_1_3|total", R.mipmap.fiv_1_1_3_43);
                truckKeyToThumb.put("cf_5|1_2_2|total", R.mipmap.five_1_2_2_43);
                truckKeyToThumb.put("cf_5|1_1_1_2|total", R.mipmap.t5_1_1_1_2);
                truckKeyToThumb.put("cf_5|1_1_1_2|2", R.mipmap.five_1_1_1_2_43);
                truckKeyToThumb.put("cf_5|1_1_3|total", R.mipmap.fiv_1_1_3_43);
                truckKeyToThumb.put("cf_5|1_1_3|3", R.mipmap.w5t1_1_3b3);
                truckKeyToThumb.put("cf_5|1_2_2|total", R.mipmap.wheel1_2_2);
                truckKeyToThumb.put("cf_5|1_2_2|2", R.mipmap.w5t1_2_2b2);
                break;
            case 6:
                truckKeyToThumb.put("cf_6|1_2_3|total", R.mipmap.wheel1_2_3);
                truckKeyToThumb.put("cf_6|1_2_3|2", R.mipmap.w6t1_2_3b2);
                truckKeyToThumb.put("cf_6|1_2_3|3", R.mipmap.w6t1_2_3b3);
                truckKeyToThumb.put("cf_6|1_1_1_3|total", R.mipmap.wheel1_1_1_3);
                truckKeyToThumb.put("cf_6|1_1_1_3|111", R.mipmap.w6t1_1_1_3b111);
                truckKeyToThumb.put("cf_6|1_1_1_3|3", R.mipmap.w6t1_1_1_3b13);
                break;
            default:
                break;
        }
    }

    private void initAxisView() {
        initAxixImg();
        LinearLayout axleLayout = new LinearLayout(this);
        axleLayout.setOrientation(LinearLayout.VERTICAL);
        setTitle(String.format("%d轴车-补偿系数设定 ", new Object[]{Integer.valueOf(compensationType)}));
        CompensationJsonHelper.TruckAxleType truckAxleType = compensationJsonHelper.getTruck();
        List <CompensationJsonHelper.TruckAxleType.TruckTypeCompensation> truckAxleTypeList = truckAxleType.getTypes();
        CompensationJsonHelper.TruckAxleType.TruckTypeCompensation truckTypeCompensation = truckAxleTypeList.get(compensationType - 2);
        truckTypeCompensation.getName();
        List <CompensationJsonHelper.TruckAxleType.TruckTypeCompensation.TypeFactor> typeFactorList = truckTypeCompensation.getItems();
        for (CompensationJsonHelper.TruckAxleType.TruckTypeCompensation.TypeFactor typeFactor : typeFactorList) {
            String name = typeFactor.getName();
            String key = typeFactor.getKey();
            typeFactor.setTotal(SharedPreferencesUtil.getFactorInt(key, typeFactor.getTotal()));
            FrameLayout compensation_view_layout = (FrameLayout) View.inflate(this, R.layout.setting_compensation_trucktype, null);
            ImageView truckTypeThumb = compensation_view_layout.findViewById(R.id.truckTypeThumb);
            TextView truckTypeTitle = compensation_view_layout.findViewById(R.id.truckTypeTitle);
            Log.d("cmf", typeFactor.getKey());
            truckTypeThumb.setImageResource(truckKeyToThumb.get(typeFactor.getKey()));
            LinearLayout truckTypetotal = compensation_view_layout.findViewById(R.id.truckTypetotal);
            LinearLayout containerTruckTypesFactors = compensation_view_layout.findViewById(R.id.containerTruckTypesFactors);
            LinearLayout lvTruckTypesFactors = compensation_view_layout.findViewById(R.id.lvTruckTypesFactors);
            truckTypeTitle.setText(String.format(getString(R.string.compensation_item_type_title), new Object[]{name}));
            CompensationFactorItem compensationFactorItem = CompensationFactorItem.create(name + "整体补偿", typeFactor.getTotal(), key, this, truckKeyToThumb.get(typeFactor.getKey()));
            truckTypetotal.addView(compensationFactorItem.getView());
            settingItems.add(compensationFactorItem);
            axleLayout.addView(compensation_view_layout);
            List <CompensationJsonHelper.TruckAxleType.TruckTypeCompensation.Factor> typeFactorAxleList = typeFactor.getFactors();
            for (CompensationJsonHelper.TruckAxleType.TruckTypeCompensation.Factor factor : typeFactorAxleList) {
                factor.setValue(SharedPreferencesUtil.getFactorInt(factor.getKey(), factor.getValue()));
                Log.d("lcj type", "" + factor.getKey() + truckKeyToThumb.get(factor.getKey() + factor.getName()));
                CompensationFactorItem factorItem = CompensationFactorItem.create(factor.getName(), factor.getValue(), factor.getKey(), this, truckKeyToThumb.get(factor.getKey()));
                lvTruckTypesFactors.addView(factorItem.getView());
                settingItems.add(factorItem);
            }
            //containerTruckTypesFactors.setVisibility(View.GONE);
        }
        compensationFactorSettingItemsLayout.addView(axleLayout);
    }


}