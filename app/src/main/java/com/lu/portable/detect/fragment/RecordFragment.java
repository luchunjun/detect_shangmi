package com.lu.portable.detect.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.lu.portable.detect.Record;
import com.lu.portable.detect.SingleRecordActivity;
import com.lu.portable.detect.adaptor.DetectRecordAdaptor;
import com.lu.portable.detect.database.DatabaseAdapter;
import com.lu.portable.detect.model.Car;
import com.lu.portable.detect.ui.HomeActivity;
import com.lu.portable.detect.util.DateUtil;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import com.kernal.demo.plateid.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class RecordFragment extends Fragment {
    public static ArrayList <Record> records = new ArrayList <>();
    static RecordFragment recordFragment;
    DecimalFormat decimalFormat = new DecimalFormat("#####0.00");
    DecimalFormat speedDecimalFormat = new DecimalFormat("#####0.0");
    private CheckBox checkBoxShowOnlyOverWeight;
    private DatabaseAdapter m_MyDatabaseAdapter;
    DetectRecordAdaptor detectRecordAdaptor;
    private Spinner spinnerAxisType;
    private EditText carNumberEdit;
    private TextView detectDateTV;
    ArrayList <HashMap<String,Object>> dataList= new ArrayList <>();
    public static RecordFragment newInstance() {
        if (recordFragment == null) {
            recordFragment = new RecordFragment();
        }
        return recordFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container,false);
        Button   multiButtonPrint = view.findViewById(R.id.buttonPrint);
        Button  delBtn = view.findViewById(R.id.buttonDelete);
        delBtn.setOnClickListener(v -> {
            Iterator<HashMap<String,Object>> it_b=dataList.iterator();
            while(it_b.hasNext()){
                HashMap<String,Object> hashMap=it_b.next();
                if((Boolean) hashMap.get(DetectRecordAdaptor.SWITCH)){
                    DatabaseAdapter m_MyDatabaseAdapter = DatabaseAdapter.getDatabaseAdapter(getContext());
                    m_MyDatabaseAdapter.deleteUser((Integer) hashMap.get(DetectRecordAdaptor.CODE));
                    it_b.remove();
                }
            }
            detectRecordAdaptor.notifyDataSetChanged();
        });
        detectDateTV = view.findViewById(R.id.detect_date);
        detectDateTV.setOnClickListener(v -> showDate());
        multiButtonPrint.setOnClickListener(button -> printRecord());
        makeSpinner(view);
        ListView   listView = view.findViewById(R.id.listViewTable);
        detectRecordAdaptor = new DetectRecordAdaptor(dataList,getContext());
        listView.setAdapter(detectRecordAdaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DetectRecordAdaptor.ViewHolder viewHolder = (DetectRecordAdaptor.ViewHolder) view.getTag();
                viewHolder.checkBox.toggle();
                dataList.get(position).put(DetectRecordAdaptor.SWITCH, viewHolder.checkBox.isChecked());
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), SingleRecordActivity.class);
                intent.putExtra("nSel", position);
                intent.putExtra("id", (int)dataList.get(position).get(DetectRecordAdaptor.CODE));
                startActivity(intent);
                return false;
            }
        });
        return view;
    }


    public void printRecord() {
       HomeActivity homeActivity = (HomeActivity)getActivity();
        StringBuilder sb = new StringBuilder();
        int count =0;
        for(int i=0;i<dataList.size();i++){
            HashMap<String,Object> data = dataList.get(i);
            Boolean isChecked= (Boolean) data.get(DetectRecordAdaptor.SWITCH);
            if(isChecked) {
                count++;
                Record recordTmp  = records.get(i);
                sb.append(" 超限检测报告单");
                sb.append("\n\n");
                sb.append("执法单位：").append(SharedPreferencesUtil.getPolicyName());
                sb.append("\n");
                sb.append("车牌：");
                sb.append(recordTmp.carNumber);
                sb.append("\n");
                sb.append("日期：");
                sb.append(recordTmp.dateTime.substring(0, 10));
                sb.append("\n");
                sb.append("时间：");
                sb.append(recordTmp.dateTime.substring(10));
                sb.append(getString(R.string.axis_Type)).append(":");
                sb.append(recordTmp.axleNumber+"轴车");
                sb.append("\n");
                sb.append(getString(R.string.truck_limit)).append(":");
                sb.append((int)recordTmp.limit_weight);
                sb.append("t");
                sb.append("\n");

                sb.append(getString(R.string.car_speed1)).append(":");
                sb.append(speedDecimalFormat.format(recordTmp.speed));
                sb.append("Km/h");
                sb.append("\n");
                sb.append(getString(R.string.real_weight)).append(":");
                sb.append(decimalFormat.format(recordTmp.weight));
                sb.append("t");
                sb.append("\n");
                sb.append(getString(R.string.over_limit_value)).append(":");
                sb.append(decimalFormat.format(recordTmp.overWeight));
                sb.append("t");
                sb.append("\n");
                sb.append("超限率：");
                if(recordTmp.limit_weight==0 ||recordTmp.weight <=recordTmp.limit_weight){
                    sb.append(0.00);
                }else {
                    double limitRate = (recordTmp.weight  -recordTmp.limit_weight)*100/recordTmp.limit_weight;
                    sb.append(speedDecimalFormat.format(limitRate));
                }
                sb.append("%");
                sb.append("\n");
                sb.append("检测员签字：");
                sb.append("\n\n\n");
                sb.append("驾驶员签字：");
                sb.append("\n\n\n");
                sb.append("检测地点：");
                sb.append(SharedPreferencesUtil.getPlaceName());
                sb.append("\n\n");
                sb.append("\n\n");
            }
        }
        if(count>0){
            homeActivity.printText(sb);
        }else{
            homeActivity.showToast(R.string.IDS_select_record_to_print);
        }
    }

    private Object[] getAxisType() {
        String[] axisType = {getString(R.string.IDS_all), getString(R.string.IDS_2_axis), getString(R.string.IDS_3_axis), getString(R.string.IDS_4_axis), getString(R.string.IDS_5_axis), getString(R.string.IDS_6_axis), getString(R.string.IDS_other)};
        return axisType;
    }


    private ArrayList <String> getYears() {
        ArrayList <String> years = new ArrayList <>();
        years.add(getString(R.string.IDS_all));
        int year = Calendar.getInstance().get(Calendar.YEAR);
        while (year > 2018) {
            years.add(year + "");
            year--;
        }
        return years;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTable();
    }

    private void showDate() {
        TimePickerView pvTime = new TimePickerView.Builder(getActivity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String time = DateUtil.getTime(date);
                detectDateTV.setText(time);
            }
        }).setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .build();

        pvTime.show();
    }


    public void makeSpinner(final View parent) {
        spinnerAxisType = parent.findViewById(R.id.spinnerAxisType);
        carNumberEdit = parent.findViewById(R.id.editTextCarNumber);
        ArrayAdapter axisTypeAdaptor = new ArrayAdapter(getContext(), R.layout.custom_spinner_item, getAxisType());
        axisTypeAdaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerAxisType.setAdapter(axisTypeAdaptor);
        spinnerAxisType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        parent.findViewById(R.id.buttonQuery).setOnClickListener(view -> updateTable());
        parent.findViewById(R.id.buttonReset).setOnClickListener(paramView -> {
            detectDateTV.setText(DateUtil.getTime(new Date()));
            carNumberEdit.setText("");
            spinnerAxisType.setSelection(0);
            checkBoxShowOnlyOverWeight.setChecked(false);
            detectDateTV.setText(R.string.IDS_all);
            updateTable();
        });
        checkBoxShowOnlyOverWeight = parent.findViewById(R.id.checkBoxShowOnlyOverWeight);
        checkBoxShowOnlyOverWeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                updateTable();
            }
        });
        m_MyDatabaseAdapter = DatabaseAdapter.getDatabaseAdapter(getContext());
        parent.findViewById(R.id.buttonTotalSelection).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                for(HashMap hashMap :dataList){
                    hashMap.put(DetectRecordAdaptor.SWITCH,true);
                }
                detectRecordAdaptor.notifyDataSetChanged();
            }
        });
    }

    private String timeToString(String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(timeStr);
            String newTimeStr = new SimpleDateFormat("MM-dd HH:mm").format(date);
            return newTimeStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return timeStr;
        }
    }

    private String AxisAndLimit(Record record) {

        return String.format("%s/%d", new Object[]{Car.getAxisNumber(record), Integer.valueOf((int) (Car.getLimitWeight(record) / 1000.0D))});
    }

    public void updateTable() {
        records.clear();
        dataList.clear();
        m_MyDatabaseAdapter = DatabaseAdapter.getDatabaseAdapter(getContext());
        String sql = makeSqlString();
        Cursor  cursor = m_MyDatabaseAdapter.getDetectRecordInfo(sql);
        if (cursor != null) {
            if (cursor.moveToFirst())
                do
                    records.add(Record.CursorToRecord(cursor));
                while (cursor.moveToNext());
            cursor.close();
        }
        for (Record record : records) {
            HashMap<String,Object> hashMap =new HashMap<>();
            hashMap.put(DetectRecordAdaptor.CODE,record.id);
            hashMap.put(DetectRecordAdaptor.PLATE_NUM,record.carNumber);
            hashMap.put(DetectRecordAdaptor.TIME,timeToString(record.dateTime));
            hashMap.put(DetectRecordAdaptor.AXIS_NUM,AxisAndLimit(record));
            hashMap.put(DetectRecordAdaptor.WEIGHT,decimalFormat.format(record.weight));
            hashMap.put(DetectRecordAdaptor.SWITCH,false);
            dataList.add(hashMap);
        }
        detectRecordAdaptor.notifyDataSetChanged();
    }


    public String makeSqlString() {
        String carNumber = carNumberEdit.getText().toString();
        String axisType = spinnerAxisType.getSelectedItem().toString();
        String date = "";
        if (!detectDateTV.getText().equals(getString(R.string.IDS_all))) {
            date = detectDateTV.getText().toString();
        }
        String sqlString = "SELECT dc.*, u.name FROM [detect_record] AS dc JOIN [users] AS u ON dc.user_id = u.id" + " WHERE detect_time LIKE '%" + date + "%' AND plate_number LIKE '%" + carNumber + "%' ";
        if (checkBoxShowOnlyOverWeight.isChecked()) {
            sqlString += "AND over_weight > 0";
        }
        String axisTypeString = "";

        if (axisType.equals("2轴车")) {
            axisTypeString = "AND (axle_number=2 )";
        } else if (axisType.equals("3轴车")) {
            axisTypeString = "AND (axle_number=3 )";
        } else if (axisType.equals("4轴车")) {
            axisTypeString = "AND (axle_number=4 )";
        } else if (axisType.equals("5轴车")) {
            axisTypeString = "AND (axle_number=5 )";
        } else if (axisType.equals("6轴车")) {
            axisTypeString = "AND (axle_number=6)";
        } else if (axisType.equals("多轴车")) {
            axisTypeString = "AND (axle_number>6)";
        }
        Log.e("axisTypeString",axisTypeString);
        return sqlString +"  "+ axisTypeString + " ORDER BY detect_time DESC";
    }
}
