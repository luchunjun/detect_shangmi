package com.lu.portable.detect;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lu.portable.detect.adaptor.DeleteUserAdaptor;
import com.lu.portable.detect.adaptor.DetectRecordAdaptor;
import com.lu.portable.detect.database.DatabaseAdapter;
import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DelUserActivity extends BaseActivity implements View.OnClickListener {
    ArrayList<HashMap<String, Object>> list = new ArrayList<>();
    DeleteUserAdaptor mDeleteUserAdaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        setTitle(R.string.del_user);
        Log.d("DelUserActivity","onCreate");
        ListView listView = findViewById(R.id.list_view);
         updateUserList();
         mDeleteUserAdaptor = new DeleteUserAdaptor(list, this);
        listView.setAdapter(mDeleteUserAdaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeleteUserAdaptor.ViewHolder viewHolder = (DeleteUserAdaptor.ViewHolder) view.getTag();
                viewHolder.checkBox.toggle();
                list.get(position).put(DeleteUserAdaptor.SWITCH, viewHolder.checkBox.isChecked());
            }
        });
    }

    private void updateUserList(){
        DatabaseAdapter m_MyDatabaseAdapter = DatabaseAdapter.getDatabaseAdapter(this);
        Cursor cursor = m_MyDatabaseAdapter.getAllUsersInfo();
        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    if(cursor.getInt(0)>1) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(DeleteUserAdaptor.TITLE, cursor.getString(1));
                        hashMap.put(DeleteUserAdaptor.CODE, cursor.getInt(0));
                        hashMap.put(DeleteUserAdaptor.SWITCH, false);
                        list.add(hashMap);
                    }
                }
                while (cursor.moveToNext());
            cursor.close();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectAll:
                for(HashMap hashMap :list){
                    hashMap.put(DeleteUserAdaptor.SWITCH,true);
                }
                mDeleteUserAdaptor.notifyDataSetChanged();
                break;
            case R.id.unSelect:
                for(HashMap hashMap :list){
                    boolean isChecked= (Boolean) hashMap.get(DeleteUserAdaptor.SWITCH);
                    hashMap.put(DeleteUserAdaptor.SWITCH,!isChecked);
                }
                mDeleteUserAdaptor.notifyDataSetChanged();
                break;
            case R.id.allUnSelect:
                for(HashMap hashMap :list){
                    hashMap.put(DeleteUserAdaptor.SWITCH,false);
                }
                mDeleteUserAdaptor.notifyDataSetChanged();
                break;
            case R.id.delete:
                Iterator<HashMap<String,Object>> it_b=list.iterator();
                while(it_b.hasNext()){
                    HashMap<String,Object> hashMap=it_b.next();
                    if((Boolean) hashMap.get(DetectRecordAdaptor.SWITCH)){
                        DatabaseAdapter m_MyDatabaseAdapter = DatabaseAdapter.getDatabaseAdapter(this);
                        m_MyDatabaseAdapter.deleteUser((Integer) hashMap.get(DetectRecordAdaptor.CODE));
                        it_b.remove();
                    }
                }
                mDeleteUserAdaptor.notifyDataSetChanged();
                break;
            default:
                break;
        }

    }
}