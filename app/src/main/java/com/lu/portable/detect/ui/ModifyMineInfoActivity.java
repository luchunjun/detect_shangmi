package com.lu.portable.detect.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.BaseActivity;
import com.lu.portable.detect.PortableBalanceApplication;
import com.lu.portable.detect.database.DatabaseAdapter;
import com.lu.portable.detect.util.Md5;
import com.lu.portable.detect.util.SharedPreferencesUtil;

public class ModifyMineInfoActivity extends BaseActivity {
    private DatabaseAdapter m_MyDatabaseAdapter;
    private Button saveBtn;
    private Button resetButton;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_modify_password);
        int type = getIntent().getIntExtra("type", 0);
        int titleId = getIntent().getIntExtra("title", R.string.modify_password);
        setTitle(titleId);
        saveBtn = findViewById(R.id.BtnPwCommit);
        resetButton = findViewById(R.id.BtnPwReset);
        switch (type) {
            case 0:
                initModifyPasswordView();
                break;
            case 1:
                initCompanyInfoView();
                break;
            case 2:
                initAddUserView();
                break;
            default:
                initModifyPasswordView();
                break;
        }
    }

    private void initModifyPasswordView() {
        final EditText editTextOldPass = findViewById(R.id.editTextOldPass);
        final EditText editTextNewPass = findViewById(R.id.editTextNewPass);
        final EditText editTextRepeatPass = findViewById(R.id.editTextRepeatPass);
        resetButton.setOnClickListener(view -> {
            editTextOldPass.setText("");
            editTextNewPass.setText("");
            editTextRepeatPass.setText("");
        });
        saveBtn.setOnClickListener(view -> {
            String newPassword = editTextNewPass.getText().toString();
            long userId = PortableBalanceApplication.getUserId();
            if (newPassword.equals(editTextRepeatPass.getText().toString())) {
                m_MyDatabaseAdapter = DatabaseAdapter.getDatabaseAdapter(getParent());
                Cursor cursor = m_MyDatabaseAdapter.findPassword(userId);
                if (cursor.getCount() != 0) {
                    if (cursor.moveToFirst()) {
                        String str1 = editTextOldPass.getText().toString();
                        String str2 = cursor.getString(0);
                        cursor.close();
                        if (!new Md5(str1).get32().equals(str2)) {
                            Toast.makeText(getApplicationContext(), "原始密码错误！", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            m_MyDatabaseAdapter.updateUserPw(userId, newPassword);
                            Toast.makeText(getApplicationContext(), "密码修改成功！", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(ModifyMineInfoActivity.this, "数据库无当前用户！", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                Toast.makeText(getApplicationContext(), "两次新密码不相同！", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initCompanyInfoView() {
        findViewById(R.id.oldPasswordLayout).setVisibility(View.GONE);
        findViewById(R.id.companyLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.newPasswordLayout).setVisibility(View.GONE);
        findViewById(R.id.confirmPasswordLayout).setVisibility(View.GONE);
        final EditText editCompanyName = findViewById(R.id.companyNameEdit);
        resetButton.setOnClickListener(view -> editCompanyName.setText(""));
        saveBtn.setOnClickListener(view -> {
            String companyName = editCompanyName.getText().toString();
            if (!companyName.isEmpty()) {
                SharedPreferencesUtil.setCompanyName(companyName);
                showToast(R.string.IDS_common_success);
                finish();
            }
        });
    }

    private void initAddUserView() {
        findViewById(R.id.oldPasswordLayout).setVisibility(View.GONE);
        findViewById(R.id.userNameLayout).setVisibility(View.VISIBLE);
        final EditText editTextUserName = findViewById(R.id.userNameEdit);
        final EditText editTextNewPass = findViewById(R.id.editTextNewPass);
        final EditText editTextRepeatPass = findViewById(R.id.editTextRepeatPass);
        resetButton.setOnClickListener(view -> {
            editTextUserName.setText("");
            editTextNewPass.setText("");
            editTextRepeatPass.setText("");
        });
        saveBtn.setOnClickListener(view -> {
            String userName = editTextUserName.getText().toString();
            if (userName.isEmpty()) {
                showToast(R.string.login_input_name);
                return;
            }
            String newPassword = editTextNewPass.getText().toString();
            String confirmPassword = editTextRepeatPass.getText().toString();
            if (newPassword.isEmpty()) {
                showToast(R.string.login_input_password);
                return;
            }
            if (!confirmPassword.equals(newPassword)) {
                showToast(R.string.confirm_input_password);
                return;
            }

            if (!DatabaseAdapter.getDatabaseAdapter(getApplicationContext()).isUserNameExist(userName)) {
                boolean addResult = DatabaseAdapter.getDatabaseAdapter(getApplicationContext()).addUser(userName, newPassword);
                if (addResult) {
                    showToast(R.string.IDS_common_success);
                    finish();
                } else {
                    showToast(R.string.IDS_common_fail);
                }
            } else {
                showToast("用户名已经存在，请重新输入");
                editTextUserName.setText("");
                editTextUserName.requestFocus();
            }
        });
    }


}