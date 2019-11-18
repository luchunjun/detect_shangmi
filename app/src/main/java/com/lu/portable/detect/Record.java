package com.lu.portable.detect;
import android.database.Cursor;

public class Record
{
    public int air_suspension;
    public String air_suspension_pic;
    public String axleNumber;
    public  boolean bSelect = false;
    public String bigPhoto;
    public String carNumber;
    public String checker;
    public String dateTime;
    public String detectLocation;
    public int id = -1;
    public String license_pic;
    public double limit_weight;
    public double overWeight;
    public String platePhoto;
    public int special_tag = 0;
    public double speed;
    public int sub_wheel;
    public String sub_wheel_pic;
    public String truckType;
    public int uploaded = 0;
    public double weight;
    public String weightAndOverWeight;

    public static Record CursorToRecord(Cursor cursor)
    {
        Record record = new Record();
        record.id = cursor.getInt(cursor.getColumnIndex("id"));
        record.dateTime = cursor.getString(cursor.getColumnIndex("detect_time"));
        record.carNumber = cursor.getString(cursor.getColumnIndex("plate_number"));
        record.weight = cursor.getDouble(cursor.getColumnIndex("weight"));
        record.overWeight = cursor.getDouble(cursor.getColumnIndex("over_weight"));
        record.truckType = cursor.getString(cursor.getColumnIndex("truck_type"));
        record.axleNumber = cursor.getString(cursor.getColumnIndex("axle_number"));
        record.speed = cursor.getDouble(cursor.getColumnIndex("speed"));
        record.checker = cursor.getString(cursor.getColumnIndex("name"));
        record.detectLocation = cursor.getString(cursor.getColumnIndex("detect_location"));
        record.platePhoto = cursor.getString(cursor.getColumnIndex("plate_photo"));
        record.bigPhoto = cursor.getString(cursor.getColumnIndex("truck_photo"));
        record.special_tag = cursor.getInt(cursor.getColumnIndex("special_tag"));
        record.limit_weight = cursor.getDouble(cursor.getColumnIndex("limit_weight"));
        record.license_pic = cursor.getString(cursor.getColumnIndex("license_pic"));
        record.sub_wheel = cursor.getInt(cursor.getColumnIndex("sub_wheel"));
        record.sub_wheel_pic = cursor.getString(cursor.getColumnIndex("sub_wheel_pic"));
        record.air_suspension = cursor.getInt(cursor.getColumnIndex("air_suspension"));
        record.air_suspension_pic = cursor.getString(cursor.getColumnIndex("air_suspension_pic"));
        return record;
    }

    public void assign(Record record)
    {
        id = record.id;
        dateTime = record.dateTime;
        carNumber = record.carNumber;
        axleNumber = record.axleNumber;
        weightAndOverWeight = record.weightAndOverWeight;
        truckType = record.truckType;
        weight = record.weight;
        speed = record.speed;
        overWeight = record.overWeight;
        checker = record.checker;
        detectLocation = record.detectLocation;
        platePhoto = record.platePhoto;
        bigPhoto = record.bigPhoto;
        uploaded = record.uploaded;
        special_tag = record.special_tag;
        limit_weight = record.limit_weight;
        license_pic = record.license_pic;
        sub_wheel = record.sub_wheel;
        sub_wheel_pic = record.sub_wheel_pic;
        air_suspension = record.air_suspension;
        air_suspension_pic = record.air_suspension_pic;
        bSelect = record.bSelect;
    }

    public Record copy()
    {
        Record record = new Record();
        record.id = id;
        record.dateTime = dateTime;
        record.carNumber = carNumber;
        record.axleNumber = axleNumber;
        record.weightAndOverWeight = weightAndOverWeight;
        record.truckType = truckType;
        record.weight = weight;
        record.speed = speed;
        record.overWeight = overWeight;
        record.checker = checker;
        record.detectLocation = detectLocation;
        record.platePhoto = platePhoto;
        record.bigPhoto = bigPhoto;
        record.uploaded = uploaded;
        record.special_tag = special_tag;
        record.limit_weight = limit_weight;
        record.license_pic = license_pic;
        record.sub_wheel = sub_wheel;
        record.sub_wheel_pic = sub_wheel_pic;
        record.air_suspension = air_suspension;
        record.air_suspension_pic = air_suspension_pic;
        record.bSelect = bSelect;
        return record;
    }

    public void reset()
    {
       id = -1;
       dateTime = "";
       carNumber = "未识别车牌";
       axleNumber = "";
       weightAndOverWeight = "";
       truckType = "";
       weight = 0.0D;
       speed = 0.0D;
       overWeight = 0.0D;
       checker = "";
       detectLocation = "";
       platePhoto = "";
       bigPhoto = "";
       uploaded = 0;
       special_tag = 0;
       limit_weight = 0.0D;
       license_pic = "";
       sub_wheel = 0;
       sub_wheel_pic = "";
       air_suspension = 0;
       air_suspension_pic = "";
       bSelect = false;
    }
}
