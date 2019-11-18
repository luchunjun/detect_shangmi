package com.lu.portable.detect.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.portable.detect.BigCarCompensationActivity;
import com.lu.portable.detect.CompensationFactorListActivity;
import com.lu.portable.detect.PortableBalanceApplication;
import com.lu.portable.detect.StaticWeightingActivity;
import com.lu.portable.detect.adaptor.IconMenuAdaptor;
import com.lu.portable.detect.database.DatabaseAdapter;
import com.lu.portable.detect.ui.HomeActivity;
import com.lu.portable.detect.ui.PolicyActivity;
import com.lu.portable.detect.ui.PortSettingActivity;
import com.lu.portable.detect.util.FileUtils;
import com.lu.portable.detect.util.Md5;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import com.lu.portable.detect.view.MyImageView;
import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;


public class MineFragment extends Fragment {
    static MineFragment mineFragment;
    MyImageView changeHeadImage;
    private TextView companyNameTv;
    private TextView userNameTv;

    public static MineFragment newInstance() {
        if (mineFragment == null) {
            mineFragment = new MineFragment();
        }
        return mineFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mineFragment = inflater.inflate(R.layout.fragment_mine, container,false);
        companyNameTv = mineFragment.findViewById(R.id.companyNameTv);
        userNameTv = mineFragment.findViewById(R.id.usernameTv);
        changeHeadImage = mineFragment.findViewById(R.id.changeHeadImage);
        ListView listView = mineFragment.findViewById(R.id.list_view);
        ArrayList <HashMap <String, Object>> list = new ArrayList <>();
        //final int[] titleIds = {R.string.compensation_factor, R.string.static_weight, R.string.policy_info,R.string.other_setting, R.string.logout};
        final int[] titleIds = {R.string.compensation_factor, R.string.static_weight, R.string.policy_info, R.string.other_setting,R.string.logout};
        //final int[]  imgIds= {R.drawable.ic_factor, R.drawable.ic_weight,R.drawable.ic_policy, R.drawable.ic_settings, R.drawable.ic_logout_24dp};
        final int[]  imgIds= {R.drawable.ic_factor, R.drawable.ic_weight,R.drawable.ic_policy, R.drawable.ic_settings,R.drawable.ic_logout_24dp};
        for (int i = 0; i < titleIds.length; i++) {//overlook member
            HashMap <String, Object> map = new HashMap <>();
            map.put(IconMenuAdaptor.TITLE, getString(titleIds[i]));
            map.put(IconMenuAdaptor.IMAGE, getResources().getDrawable(imgIds[i]));
            list.add(map);
        }
        IconMenuAdaptor mIconMenuAdaptor = new IconMenuAdaptor(list, getContext());
        listView.setAdapter(mIconMenuAdaptor);
        listView.setOnItemClickListener((arg0, arg1, position, arg3) -> {
            if (position == titleIds.length - 1) {
                HomeActivity activity =(HomeActivity) getActivity();
                activity.logOUt();
            } else if (position == 0) {
                View confirm = LayoutInflater.from(getContext()).inflate(R.layout.compensation_comfrim, null);
                final EditText passwordText = confirm.findViewById(R.id.password_confirm);
                new AlertDialog.Builder(getContext()).setTitle(R.string.input_admin_password).setView(confirm).setPositiveButton(R.string.IDS_common_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        String password = passwordText.getText().toString();
                        String adminPassword = DatabaseAdapter.getDatabaseAdapter(getActivity().getApplicationContext()).getAdminPassword();
                        if (new Md5(password).get32().equals(adminPassword)) {
                            if(SharedPreferencesUtil.getScaleMode()==2) {
                                startActivity(new Intent(getContext(), CompensationFactorListActivity.class));
                            }else{
                                startActivity(new Intent(getContext(), BigCarCompensationActivity.class));
                            }
                        } else {
                            Toast toast = Toast.makeText(getContext(), R.string.err_password, Toast.LENGTH_LONG);
                            toast.setGravity(17, 0, 0);
                            toast.show();
                        }
                    }
                }).setNegativeButton(R.string.IDS_common_cancel, null).show();

            } else if (position == 1) {
                startActivity(new Intent(getContext(), StaticWeightingActivity.class));
            } else if (position == 2) {
                startActivity(new Intent(getContext(), PolicyActivity.class));
            }else if(position==3){
               // startActivity(new Intent(getContext(), OtherSettingActivity.class));
                startActivityForResult(new Intent(getContext(), PortSettingActivity.class),100);
            }
        });
        return mineFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        companyNameTv.setText(SharedPreferencesUtil.getCompanyName());
        userNameTv.setText(PortableBalanceApplication.getUserName());
        Bitmap bitmap = FileUtils.getImageDrawable(getActivity(), SharedPreferencesUtil.getUserId());
        if (bitmap != null) {
            changeHeadImage.setImageBitmap(bitmap);
        }
    }


}
