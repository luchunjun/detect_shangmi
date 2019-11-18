package com.lu.portable.detect.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.BaseActivity;
import com.lu.portable.detect.util.SharedPreferencesUtil;
//ctrl+alt+o
public class PolicyActivity extends BaseActivity {
    private Button saveBtn;
    private Button resetButton;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_modify_policy_info);
        setTitle(R.string.policy_info);
        saveBtn = findViewById(R.id.saveBtn);
        resetButton = findViewById(R.id.clearBtn);
        final EditText policyName = findViewById(R.id.policyName);
        final EditText placeName = findViewById(R.id.placeName);
        policyName.setText(SharedPreferencesUtil.getPolicyName());
        placeName.setText(SharedPreferencesUtil.getPlaceName());
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                policyName.setText("");
                placeName.setText("");
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(policyName.getText().length()==0){
                    showToast(R.string.IDS_input_policy_info_hint);
                    return;
                }
                if(placeName.getText().length()==0){
                    showToast(R.string.IDS_input_place_info_hint);
                    return;
                }
                if(SharedPreferencesUtil.getPolicyName().equals(policyName.getText().toString())&& SharedPreferencesUtil.getPlaceName().equals(placeName.getText().toString())){
                    showToast(R.string.IDS_no_change);
                    return;
                }
                SharedPreferencesUtil.setPlaceName(placeName.getText().toString());
                SharedPreferencesUtil.setPolicyName(policyName.getText().toString());
                showToast(R.string.IDS_common_success);
                finish();
            }
        });
    }

}