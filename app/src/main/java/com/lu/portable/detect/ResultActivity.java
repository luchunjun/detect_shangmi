package com.lu.portable.detect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AndroidException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kernal.demo.plateid.R;
import com.kernal.plateid.CoreSetup;
import com.kernal.plateid.controller.ImportPicRecog;
import com.lu.portable.detect.configtools.ConfigTools;
import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.database.DatabaseAdapter;
import com.lu.portable.detect.model.Car;
import com.lu.portable.detect.util.DateUtil;
import com.lu.portable.detect.util.HomeListener;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import com.lu.portable.detect.view.AxisTypeDialog;
import com.lu.portable.detect.view.AxisTypeDialog.AxisTypeDialogListener;

import java.text.DecimalFormat;
import java.util.Date;

public class ResultActivity extends BaseActivity implements AxisTypeDialogListener {
    private static final String TAG = "ResultActivity";
    DecimalFormat decimalFormat = new DecimalFormat("#####0.00");
    DecimalFormat speedDecimalFormat = new DecimalFormat("#####0.0");
    AxisTypeDialog axisTypeDialog;
    private Record lastedRecord;
    private DatabaseAdapter m_MyDatabaseAdapter;
    private Button manualPrintButton;
    private TextView overWeightEditText;
    private ImageView plateImageView;
    private EditText plateNumberEditText;
    private EditText speedEditText;
    private TextView truckTypeEdit;
    private TextView truckLimit;
    private TextView overLimitTv;
    private EditText weightEditText;
    private  boolean needSave =false;
    private  boolean firstTime =true;
    private TextView truckTypeName;
    private ImageView truckTypeImage;
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_result);
        Log.e(TAG, InstrumentConfig.carPlateNumFullPhoto);
        if(SharedPreferencesUtil.getScaleMode()==2) {
            InstrumentConfig.carType = "";
        }
        m_MyDatabaseAdapter = DatabaseAdapter.getDatabaseAdapter(this);
        setTitle(R.string.detect_result);
        if (mPrintQueue == null) {
            initPortablePrinter();
        } else {
            Log.e(TAG, "mPrintQueue" + mPrintQueue);
        }
        lastedRecord = generateTempRecord();
        setupViews();
        registerListeners();
        _loadImage();
        updateCarUI(lastedRecord);
        String overload = "未超载";
        if (lastedRecord.overWeight > 0.0 && !InstrumentConfig.carType.contains("big")) {
            overload = "已超载";
            pronounce(plateNumberEditText.getText().toString() + "," + truckTypeEdit.getText().toString()
                    + "，车速" + speedEditText.getText().toString() + "千米每小时," + "车重" + weightEditText.getText().toString() + "吨，" + "超限" + overWeightEditText.getText().toString() + "吨," + overload);

        } else {
            pronounce(plateNumberEditText.getText().toString() + "," + truckTypeEdit.getText().toString()
                    + "，车速" + speedEditText.getText().toString() + "千米每小时," + "车重" + weightEditText.getText().toString() + "吨，" + overload);

        }

    }

   public void saveRecord(View view){
        lastedRecord.carNumber = plateNumberEditText.getText().toString();
        m_MyDatabaseAdapter.updateResult(lastedRecord);
        showToast(R.string.save_success);
   }

    private void _loadImage() {
        Log.d(TAG, "lcj load carplatePhoto" + InstrumentConfig.carPlateNumPhoto);
        plateNumberEditText.setText(lastedRecord.carNumber);
        if (!InstrumentConfig.carPlateNumPhoto.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(InstrumentConfig.carPlateNumPhoto);
            if (bitmap != null) {
                plateImageView.setImageBitmap(bitmap);
                plateImageView.setTag(InstrumentConfig.carPlateNumPhoto);
            }
        } else {
            plateImageView.setImageResource(R.mipmap.no_plate);
        }
    }


    private void insertRecord(Record record) {
        m_MyDatabaseAdapter.insertDetectRecord(1, record);
    }


    private void registerListeners() {
        HomeListener localHomeListener = new HomeListener(this);
        localHomeListener.setOnHomePressedListener(new HomeListener.OnHomePressedListener() {
            public void onHomeLongPressed() {
            }

            public void onHomePressed() {
                finish();
            }
        });

        manualPrintButton.setOnClickListener(paramView -> {
            manualPrintButton.setEnabled(false);
            printRecord();
        });
    }

    public void printRecord() {
        StringBuilder sb = new StringBuilder();
        sb.append(" 超限检测报告单");
        sb.append("\n\n");
        sb.append("执法单位：").append(SharedPreferencesUtil.getPolicyName());
        sb.append("\n");
        sb.append("车牌：");
        sb.append(plateNumberEditText.getText().toString());
        sb.append("\n");
        sb.append("日期：");
        sb.append(lastedRecord.dateTime.substring(0, 10));
        sb.append("\n");
        sb.append("时间：");
        sb.append(lastedRecord.dateTime.substring(10));
        sb.append("\n");

        sb.append(getString(R.string.axis_Type)).append(":");
        sb.append(truckTypeEdit.getText().toString());
        sb.append("\n");

        sb.append(getString(R.string.truck_limit)).append(":");
        sb.append(truckLimit.getText());
        sb.append("t");
        sb.append("\n");

        sb.append(getString(R.string.car_speed1)).append(":");
        sb.append(speedEditText.getText().toString());
        sb.append("Km/h");
        sb.append("\n");

        sb.append(getString(R.string.real_weight)).append(":");
        sb.append(weightEditText.getText().toString());
        sb.append("t");
        sb.append("\n");

        sb.append(getString(R.string.over_limit_value)).append(":");
        sb.append(overWeightEditText.getText().toString());
        sb.append("t");
        sb.append("\n");
        sb.append("超限率：");
        if(lastedRecord.limit_weight==0 ||lastedRecord.weight <=lastedRecord.limit_weight){
            sb.append(0.00);
        }else {
            double limitRate = (lastedRecord.weight  -lastedRecord.limit_weight)*100/lastedRecord.limit_weight;
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


    private void setupViews() {
        manualPrintButton = findViewById(R.id.print);
        plateNumberEditText = findViewById(R.id.plateNumberEditText);
        speedEditText = findViewById(R.id.speedEditText);
        weightEditText = findViewById(R.id.weightEditText);
        overWeightEditText = findViewById(R.id.overWeightEditText);
        truckTypeEdit = findViewById(R.id.truckType);
        truckLimit = findViewById(R.id.truckLimit);
        truckTypeName = findViewById(R.id.truckTypeName);
        truckTypeImage = findViewById(R.id.truckTypeImage);
        truckTypeEdit.setText(Car.AxisNumberToTruckType(InstrumentConfig.getAxisNum()));
        truckTypeEdit.setOnClickListener(v -> {
            if (SharedPreferencesUtil.getScaleMode()==4||InstrumentConfig.getAxisNum() <= 2 || InstrumentConfig.getAxisNum() > 6) {
                showToast(R.string.IDS_no_car_type_to_select);
            } else {
                axisTypeDialog = new AxisTypeDialog(ResultActivity.this);
                axisTypeDialog.setView(Car.getAxis("" + InstrumentConfig.getAxisNum()), InstrumentConfig.carType);
                axisTypeDialog.show();
                axisTypeDialog.setListener(ResultActivity.this);
            }

        });
        plateNumberEditText.setText(InstrumentConfig.carPlate);
        plateNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
              needSave=true;
            }
        });
        plateImageView = findViewById(R.id.plateImageView);
        overLimitTv =findViewById(R.id.overLimitTv);

    }
    public void bigPic(View view) {
        if (lastedRecord.bigPhoto != null && !lastedRecord.bigPhoto.isEmpty()) {
            Log.i("tty", "bigPath: " + lastedRecord.bigPhoto);
            Intent intent = new Intent(this, SingleTouchImageViewActivity.class);
            intent.putExtra("path", lastedRecord.bigPhoto);
            startActivity(intent);
        }else{
            showToast(R.string.no_image_hint);
        }
    }
    public void delete(View view) {
        m_MyDatabaseAdapter.deleteRecord(lastedRecord.dateTime);
        showToast(R.string.delete);
        finish();
    }
    private void updateCarUI(Record record) {
        truckTypeEdit.setText(Car.AxisNumberToTruckType(InstrumentConfig.getAxisNum()));
        if(InstrumentConfig.carTypeImage>0 && firstTime){
            truckTypeImage.setBackgroundResource(InstrumentConfig.carTypeImage);
            truckTypeImage.setVisibility(View.VISIBLE);
        }
        if(InstrumentConfig.carTypeName.length()>0 && firstTime){
            truckTypeName.setText(InstrumentConfig.carTypeName);
            truckTypeName.setVisibility(View.VISIBLE);
        }
        firstTime =false;

        weightEditText.setText(decimalFormat.format(record.weight));
        plateNumberEditText.setText(record.carNumber);
        speedEditText.setText(speedDecimalFormat.format(record.speed));
        if(InstrumentConfig.carType.contains("big")){
            overWeightEditText.setText(""+0.0);
            truckLimit.setText(""+0.0);
        }else {
            overWeightEditText.setText(decimalFormat.format(record.overWeight));
            truckLimit.setText(decimalFormat.format(record.limit_weight));
            try {
                if (record.overWeight > 0.0D) {
                    overLimitTv.setText(R.string.over_limit);
                    overLimitTv.setTextColor(Color.RED);
                }else{
                    overLimitTv.setText(R.string.not_over_limit);
                    overLimitTv.setTextColor(Color.BLACK);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private Record generateTempRecord() {
        Record record = new Record();
        record.bSelect = false;
        record.air_suspension = 5;
        record.axleNumber = "" + InstrumentConfig.getAxisNum();
        ConfigTools.writeLog("峰值计算轴数：" + record.axleNumber);
        record.platePhoto = InstrumentConfig.carPlateNumPhoto;
        record.weight = InstrumentConfig.getCarWeight();
        ConfigTools.writeLog("重量：" + record.weight);
        //LogUtil.e(TAG, "record.weight:" + record.weight);
        //"t_2_zh_1";
        record.truckType =InstrumentConfig.carType;
                //Car.getDefaultAxisType(record.axleNumber);
        Log.e("truckType", record.truckType);
        record.checker = SharedPreferencesUtil.getUserName();
        record.detectLocation = SharedPreferencesUtil.getPlaceName();
        record.bigPhoto = InstrumentConfig.carPlateNumFullPhoto;
        record.weightAndOverWeight = "";
        record.sub_wheel = 4;

        if(InstrumentConfig.carType.contains("big")) {
            record.limit_weight =0.0;
        }else{
            record.limit_weight = Car.getLimitWeight(record) / 1000;
        }
        Log.e("record.limit_weight", "" + record.limit_weight);
        record.license_pic = InstrumentConfig.carPlateNumFullPhoto;
        record.speed = InstrumentConfig.getSpeed();
        ConfigTools.writeLog("速度：" + record.speed);
        record.sub_wheel_pic = InstrumentConfig.carPlateNumFullPhoto;
        record.dateTime = DateUtil.getTimeStr(new Date());
        record.carNumber = InstrumentConfig.carPlate;
        record.air_suspension_pic = InstrumentConfig.carPlateNumFullPhoto;
        record.uploaded = 0;
        if (InstrumentConfig.getAxisNum() < 2 || InstrumentConfig.getAxisNum() > 6 || record.limit_weight >= record.weight || record.truckType.contains("big")) {
            record.overWeight = 0.0;
        } else {
            record.overWeight = record.weight - record.limit_weight;
        }
        lastedRecord = record;
        insertRecord(record);
        return record;
    }

    @Override
    public void cancelClick() {
        if (axisTypeDialog != null && axisTypeDialog.isShowing()) {
            axisTypeDialog.dismiss();
        }
    }

    public void positiveClick(String truckTypeInfo, double limit) {
        InstrumentConfig.carType = truckTypeInfo;
        if(InstrumentConfig.carTypeImage>0){
            truckTypeImage.setBackgroundResource(InstrumentConfig.carTypeImage);
            truckTypeImage.setVisibility(View.VISIBLE);
        }
        if(InstrumentConfig.carTypeName.length()>0){
            truckTypeName.setText(InstrumentConfig.carTypeName);
            truckTypeName.setVisibility(View.VISIBLE);
        }
        lastedRecord.weight  =InstrumentConfig.getCarWeight();
        lastedRecord.overWeight = Car.getCarOverWeight(lastedRecord, limit);
        lastedRecord.limit_weight = limit;
        lastedRecord.truckType = InstrumentConfig.carType ;
        m_MyDatabaseAdapter.updateDetectRecordLimit(lastedRecord);
        needSave =true;
        updateCarUI(lastedRecord);
    }

}