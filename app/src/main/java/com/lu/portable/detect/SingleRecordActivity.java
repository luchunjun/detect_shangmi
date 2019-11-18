package com.lu.portable.detect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.database.DatabaseAdapter;
import com.lu.portable.detect.model.Car;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import com.lu.portable.detect.view.AxisTypeDialog;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;


public class SingleRecordActivity extends BaseActivity
        implements AxisTypeDialog.AxisTypeDialogListener {
    public  final static  String TAG ="SingleRecordActivity";
    final DecimalFormat df = new DecimalFormat("#####0.00");
    DecimalFormat speedDecimalFormat = new DecimalFormat("#####0.0");
    private double TON_UNIT = 1.0;
    private EditText editChecker;
    private EditText editGeneral;
    private EditText editLimitWeight;
    private TextView editOverWeight;
    private EditText editSpeed;
    private EditText editTime;
    private EditText editWeight;
    private int id;
    private DatabaseAdapter m_MyDatabaseAdapter;
    private int nSel;
    private Record recordTmp = null;
    private Record selRecord = null;
    private Switch switchIsOverWeight;
    private TextView truckTypes;
    private TextView truckLabel;
    private ImageView truckIcon;
    String axisesTypes[] = {"t3_2_1", "t3_1_2", "t3_1_1_1", "t3_1_1_1_s","t4_1_1_2_0", "t4_1_1_2", "t4_1_1_1_1", "t4_1_1_2_s", "t4_1_2_1",
            "t5_1_1_3", "t5_1_1_1_2", "t5_1_2_2", "t5_1_1_1_2_s", "t5_1_2_2_s", "t5_1_1_1_1_1", "t5_1_2_1_1","t6_1_2_3", "t6_1_2_3_49",
            "t6_1_1_1_3", "t6_1_2_3_s", "t6_1_2_3_s_49", "t6_1_1_2_2", "t6_1_1_2_2_49", "t6_1_1_2_1_1", "t6_1_1_2_1_1_49","big_car_1","big_car_2","big_car_3"};
    String label[] = {"21", "12+", "111", "111A","112+", "112", "1111A", "112A", "121A+","113", "1112", "122+", "1112A",
            "122A+", "11111A", "1211A+","123", "123+", "1113", "123A", "123A+", "1122A", "1122A+", "1211A", "1211A+","I型车","Ⅱ型车","Ⅲ型车"};
    int typeIcons[] = {R.mipmap.t3_2_1, R.mipmap.t3_1_2, R.mipmap.t3_1_1_1_0, R.mipmap.t3_1_1_1_s,R.mipmap.t4_1_1_2_0,
            R.mipmap.t4_1_1_2, R.mipmap.t4_1_1_1_1, R.mipmap.t4_1_1_2_s, R.mipmap.t4_1_2_1,R.mipmap.t5_1_1_3, R.mipmap.t5_1_1_1_2,
            R.mipmap.t5_1_2_2, R.mipmap.t5_1_1_1_2_s, R.mipmap.t5_1_2_2_s, R.mipmap.t5_1_1_1_1_1, R.mipmap.t5_1_2_1_1,R.mipmap.t6_1_2_3,
            R.mipmap.t6_1_2_3_49, R.mipmap.t6_1_1_1_3, R.mipmap.t6_1_2_3_s, R.mipmap.t6_1_2_3_s_49, R.mipmap.t6_1_1_2_2, R.mipmap.t6_1_1_2_2_49,
            R.mipmap.t6_1_1_2_1_1, R.mipmap.t6_1_1_2_1_1_49,R.mipmap.big_car_1,R.mipmap.big_car_2,R.mipmap.big_car_3};
    int Limit[] = {25, 25, 27, 27,31, 36, 36, 36,35,42, 43, 43, 43,43,43,43,46, 49, 46, 46, 49, 46, 49, 46, 49,0,0,0};

    private String getAxisNameByAxisNumber(String axiCount) {
        int count = Integer.parseInt(axiCount);
        if (count >= 2) {
            return axiCount + "轴车";
        }
        return "未识别车型";
    }

    public Record getSelRecord(int paramInt) {
        Cursor localCursor = m_MyDatabaseAdapter.getDetectRecordById(paramInt);
        if ((localCursor != null) && (localCursor.moveToFirst()))
            return Record.CursorToRecord(localCursor);
        return null;
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_single_record);
        setTitle(R.string.single_record);
        if (mPrintQueue == null) {
            initPortablePrinter();
        }
        editTime = findViewById(R.id.editTextTime);
        editGeneral = findViewById(R.id.editTextGeneral);
        editWeight = findViewById(R.id.editTextWeight);
        truckTypes = findViewById(R.id.truckTypes);
        truckIcon =findViewById(R.id.truckIcon);
        truckLabel =findViewById(R.id.truckLabel);
        truckTypes.setOnClickListener(v -> setAxisType(truckTypes));
        editSpeed = findViewById(R.id.editTextSpeed);
        editOverWeight = findViewById(R.id.editTextOverWeight);
        editLimitWeight = findViewById(R.id.editLimitWeight);
        editChecker = findViewById(R.id.editTextChecker);
        switchIsOverWeight = findViewById(R.id.editTextIsOverWeight);
        nSel = getIntent().getIntExtra("nSel", -1);
        id = getIntent().getIntExtra("id", -1);
        m_MyDatabaseAdapter = DatabaseAdapter.getDatabaseAdapter(this);
        new Thread(() -> {
            selRecord = getSelRecord(id);
            recordTmp = selRecord.copy();
            runOnUiThread(() -> resetSelInfo());
        }).start();
    }

    private String getLabel(String type){
        for(int i=0;i<axisesTypes.length;i++){
            if(axisesTypes[i].equals(type)){
                return label[i];
            }
        }
        return "21";
    }
    private int getIcon(String type){
        for(int i=0;i<axisesTypes.length;i++){
            if(axisesTypes[i].equals(type)){
                return typeIcons[i];
            }
        }
        return R.mipmap.t3_2_1;
    }
    public void setAxisType(View view) {
        Log.e(TAG,"setAxisType");
        if (recordTmp != null){
            AxisTypeDialog  axisTypeDialog = new AxisTypeDialog(this);
            axisTypeDialog.setTitle("请选择轴型");
            //axisTypeDialog.setView(Car.getAxis(lastedRecord.axleNumber), axisTypes);
            axisTypeDialog.setView(Car.getAxis("" + InstrumentConfig.getAxisNum()), "t2_1_1");
            axisTypeDialog.show();
            axisTypeDialog.setListener(this);
        }
    }

    public void delete(View view) {
        new AlertDialog.Builder(SingleRecordActivity.this).setTitle("删除记录").setMessage("确认删除当前记录吗？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                m_MyDatabaseAdapter.deleteDetectRecord(Integer.valueOf(id));
                Toast.makeText(SingleRecordActivity.this, "删除成功！", Toast.LENGTH_LONG).show();
                finish();
            }
        }).setNegativeButton("取消", null).show();
    }

    public void bigPic(View view) {
        if (selRecord.platePhoto != null && !selRecord.platePhoto.isEmpty()) {
            Log.i("tty", "bigPath: " + selRecord.bigPhoto);
            Intent intent = new Intent(SingleRecordActivity.this, SingleTouchImageViewActivity.class);
            intent.putExtra("path", selRecord.bigPhoto);
            startActivity(intent);
        }
    }

    public void print(View view) {
        StringBuilder sb = new StringBuilder();
        sb.append(" 超限检测报告单");
        sb.append("\n\n");
        sb.append("执法单位：").append(SharedPreferencesUtil.getPolicyName());
        sb.append("\n");

        sb.append("车牌：");
        sb.append(editGeneral.getText().toString());
        sb.append("\n");

        sb.append("日期：");
        sb.append(recordTmp.dateTime.substring(0, 10));
        sb.append("\n");

        sb.append("时间：");
        sb.append(recordTmp.dateTime.substring(10));
        sb.append("\n");

        sb.append(getString(R.string.axis_Type)).append(":");
        sb.append(truckTypes.getText().toString());
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
        sb.append(df.format(recordTmp.weight));
        sb.append("t");
        sb.append("\n");

        sb.append(getString(R.string.over_limit_value)).append(":");
        sb.append(df.format(recordTmp.overWeight));
        sb.append("t");
        sb.append("\n");

        sb.append("超限率：");
        if(recordTmp.limit_weight==0|| recordTmp.weight<=recordTmp.limit_weight){
            sb.append("0.00");
        }else{
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
        printText(sb);
    }

    public void onStop() {
        super.onStop();
    }

    public void save(View view) {
        recordTmp.carNumber = editGeneral.getText().toString();
        m_MyDatabaseAdapter.updateResult(recordTmp);
        showToast(R.string.save_success);
        finish();
    }

    @Override
    public void cancelClick() {

    }

    public void positiveClick(String paramString, double paramDouble) {
        recordTmp.truckType = paramString;
        recordTmp.overWeight = Car.getCarOverWeight(recordTmp, paramDouble);
        updateUi();
    }

    public void resetSelInfo() {
        if (nSel >= 0) {
            editTime.setText(selRecord.dateTime);
            editGeneral.setText(selRecord.carNumber);
            truckTypes.setText(getAxisNameByAxisNumber(selRecord.axleNumber));
            truckIcon.setBackgroundResource(getIcon(recordTmp.truckType));
            truckLabel.setText(getLabel(recordTmp.truckType));
            editWeight.setText(df.format(selRecord.weight / TON_UNIT));
            editSpeed.setText(df.format(selRecord.speed));

            if(recordTmp.truckType.contains("big")){
                editLimitWeight.setText(""+0.0);
                editOverWeight.setText(""+0.0);
            }else {
                editOverWeight.setText(df.format(selRecord.overWeight / TON_UNIT));
                editLimitWeight.setText(df.format(Car.getLimitWeight(selRecord) / TON_UNIT / 1000));
            }
            editChecker.setText(selRecord.checker);
            if (selRecord.overWeight > 0.0D && !selRecord.truckType.contains("big"))  {
                switchIsOverWeight.setChecked(true);
            } else {
                switchIsOverWeight.setChecked(false);
            }
            ImageView localImageView = findViewById(R.id.imageViewCarNumberPic);
            if ((selRecord.platePhoto == null) || (selRecord.platePhoto.isEmpty())) {
                localImageView.setImageResource(R.mipmap.no_plate);
            } else {
                try {
                    FileInputStream localFileInputStream = new FileInputStream(selRecord.platePhoto);
                    Bitmap localBitmap = BitmapFactory.decodeStream(localFileInputStream);
                    if (localBitmap != null) {
                        localImageView.setImageBitmap(localBitmap);
                    }
                    localFileInputStream.close();
                } catch (IOException e) {
                    localImageView.setImageResource(R.mipmap.no_plate);
                    e.printStackTrace();
                }
            }

        }
    }

    public void updateUi() {
        editOverWeight.setText(df.format(recordTmp.overWeight / TON_UNIT));
        editLimitWeight.setText(df.format(Car.getLimitWeight(recordTmp) / TON_UNIT));
        truckIcon.setBackgroundResource(getIcon(recordTmp.truckType));
        truckLabel.setText(getLabel(recordTmp.truckType));
        if (recordTmp.overWeight > 0.0D) {
            switchIsOverWeight.setChecked(true);
        } else {
            switchIsOverWeight.setChecked(false);
        }
    }
}