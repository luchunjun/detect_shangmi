package com.lu.portable.detect.ui;


import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.Record;
import com.lu.portable.detect.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class RecordListActivity extends ListActivity {
    private Cursor cursor;
    private DatabaseAdapter m_MyDatabaseAdapter;
    private ArrayList<Record> records = new ArrayList();

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main_recorder_list);
        getActionBar().setDisplayShowHomeEnabled(false);
        Object localObject1 = getIntent().getStringExtra("queryString");
        ArrayList<HashMap<String, String>> recordList = new ArrayList<>();
        this.m_MyDatabaseAdapter = DatabaseAdapter.getDatabaseAdapter(this);
        this.cursor = this.m_MyDatabaseAdapter.getDetectRecordInfo((String) localObject1);
        Object localObject2;
        if (this.cursor != null) {
            if (this.cursor.moveToFirst())
                do {
                    int i = this.cursor.getInt(this.cursor.getColumnIndex("id"));
                    localObject1 = this.cursor.getString(this.cursor.getColumnIndex("detect_time"));
                    localObject2 = this.cursor.getString(this.cursor.getColumnIndex("plate_number"));
                    String str1 = this.cursor.getString(this.cursor.getColumnIndex("axle_number"));
                    Double localDouble1 = Double.valueOf(this.cursor.getDouble(this.cursor.getColumnIndex("weight")));
                    Double localDouble2 = Double.valueOf(this.cursor.getDouble(this.cursor.getColumnIndex("over_weight")));
                    String str2 = localDouble1.toString() + "/" + localDouble2.toString();
                    String str3 = this.cursor.getString(this.cursor.getColumnIndex("truck_type"));
                    double d = this.cursor.getDouble(this.cursor.getColumnIndex("speed"));
                    String str4 = this.cursor.getString(this.cursor.getColumnIndex("name"));
                    String str5 = this.cursor.getString(this.cursor.getColumnIndex("detect_location"));
                    String str6 = this.cursor.getString(this.cursor.getColumnIndex("plate_photo"));
                    Record localRecord = new Record();
                    localRecord.id = i;
                    localRecord.dateTime = ((String) localObject1);
                    localRecord.carNumber = ((String) localObject2);
                    localRecord.axleNumber = str1;
                    localRecord.weightAndOverWeight = str2;
                    localRecord.weight = localDouble1.doubleValue();
                    localRecord.overWeight = localDouble2.doubleValue();
                    localRecord.truckType = str3;
                    localRecord.speed = Double.valueOf(d).doubleValue();
                    localRecord.checker = str4;
                    localRecord.detectLocation = str5;
                    localRecord.platePhoto = str6;
                    this.records.add(localRecord);
                }
                while (this.cursor.moveToNext());
            this.cursor.close();
        }
        int i = 0;
        while (i < this.records.size()) {
            Record record = records.get(i);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("axle_number", record.axleNumber);
            hashMap.put("plate_number", record.carNumber);
            hashMap.put("record_time", record.dateTime);
            hashMap.put("over_weight", Double.toString(record.overWeight));
            hashMap.put("total_weight", Double.toString(record.weight));
            recordList.add(hashMap);
            i += 1;
        }
        setListAdapter(new SimpleAdapter(this, recordList, R.layout.record_list_item, new String[]{"axle_number", "plate_number", "record_time", "over_weight", "total_weight"}, new int[]{R.id.sensor_id, R.id.plate_number, R.id.record_time, R.id.over_weight, R.id.total_weight}));
    }

    protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong) {
        super.onListItemClick(paramListView, paramView, paramInt, paramLong);
        System.out.println("id = " + paramLong);
        System.out.println("position = " + paramInt);
    }
}