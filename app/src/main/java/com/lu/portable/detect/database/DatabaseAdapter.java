package com.lu.portable.detect.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lu.portable.detect.Record;
import com.lu.portable.detect.util.Md5;

import java.io.File;
import java.util.ArrayList;


public class DatabaseAdapter {
    public static final String T_DETECTRECORD = "detect_record";
    public static final String T_CALIBRATEDATA = "calibrate_data";
    public static final String T_USERS = "users";
    private static final String DB_NAME = "detect.sqlite";
    private static SQLiteDatabase mSqLiteDatabase;
    private static DatabaseAdapter m_DatabaseAdapter;
    private Context mContext;

    public DatabaseAdapter(Context context) {
        mContext = context;
    }

    public static DatabaseAdapter getDatabaseAdapter(Context context) {
        if (m_DatabaseAdapter == null) {
            m_DatabaseAdapter = new DatabaseAdapter(context);
        }
        if (mSqLiteDatabase == null) {
            open(context);
        }
        return m_DatabaseAdapter;
    }

    public static void open(Context context)
            throws SQLException {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context, DB_NAME);
        if (mSqLiteDatabase != null) {
            mSqLiteDatabase.close();
            Log.d("DatabaseAdapter", "mSqLiteDatabase.close()");
        } else {
            mSqLiteDatabase = dataBaseHelper.openDataBase();
        }
    }

    public long addStaticValue(String sensorName, double loadedWeight, long value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("sensor", sensorName);
        contentValues.put("loadedWeight", loadedWeight);
        contentValues.put("value", value);
        return mSqLiteDatabase.insert(T_CALIBRATEDATA, null, contentValues);
    }

    public void close() {
        if (mSqLiteDatabase != null) {
            mSqLiteDatabase.close();
        }
    }

    public void deleteAllStaticValue(String sensorName) {
        mSqLiteDatabase.delete(T_CALIBRATEDATA, "sensor = ?", new String[]{sensorName});
    }
    public void deleteUser(Integer id) {
        try {
            Cursor cursor = getDetectRecordById(id);
            cursor.moveToFirst();
            mSqLiteDatabase.delete(T_USERS, "id = ?", new String[]{id.toString()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteDetectRecord(Integer id) {
        try {
            Cursor cursor = getDetectRecordById(id);
            cursor.moveToFirst();
            deleteFileFromLocal(cursor.getString(cursor.getColumnIndex("plate_photo")));
            deleteFileFromLocal(cursor.getString(cursor.getColumnIndex("truck_photo")));
            mSqLiteDatabase.delete(T_DETECTRECORD, "id = ?", new String[]{id.toString()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFileFromLocal(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteStaticValue(String sensorName, double loadedWeight) {
        mSqLiteDatabase.delete("calibrate_data", "sensor = ? and loadedWeight=?", new String[]{sensorName, Double.toString(loadedWeight)});
    }

    public Cursor findPassword(long userId) {
        return mSqLiteDatabase.rawQuery("select password from users where id=" + userId, null);
    }

    public Cursor getAllUsersInfo() {
        String queryAllUsersSql = "select * from " + T_USERS;
        return mSqLiteDatabase.rawQuery(queryAllUsersSql, null);
    }

    public Cursor getDetectRecordByDate() {
        return mSqLiteDatabase.rawQuery("select strftime('%Y-%m', detect_time), strftime('%Y', detect_time), strftime('%m', detect_time) from detect_record group by strftime('%Y-%m', detect_time)", null);
    }

    public Cursor getDetectRecordById(int paramInt) {
        return mSqLiteDatabase.rawQuery("select dc.*, u.name from detect_record as dc join users as u on dc.user_id = u.id where dc.id = '" + paramInt + "'", null);
    }

    public Cursor getDetectRecordByTime() {
        return mSqLiteDatabase.rawQuery("select strftime('%Y-%m', detect_time), strftime('%Y', detect_time), strftime('%m', detect_time), count(*) from detect_record where over_weight > 0 group by strftime('%Y-%m', detect_time)", null);
    }

    public Cursor getDetectRecordByTime(String paramString) {
        return mSqLiteDatabase.rawQuery("select strftime('%Y-%m', detect_time), strftime('%Y', detect_time), strftime('%m', detect_time), count(*) from detect_record where over_weight > 0 and truck_type = '" + paramString + "' group by strftime('%Y-%m', detect_time)", null);
    }

    public Cursor getDetectRecordByTimeAll() {
        return mSqLiteDatabase.rawQuery("select strftime('%Y-%m', detect_time), strftime('%Y', detect_time), strftime('%m', detect_time), count(*) from detect_record group by strftime('%Y-%m', detect_time)", null);
    }

    public Cursor getDetectRecordInfo(String sql) {
        return mSqLiteDatabase.rawQuery(sql, null);
    }

    public Cursor getDistinctLocation() {
        return mSqLiteDatabase.rawQuery("select distinct detect_location from detect_record", null);
    }

    public Integer getLocationCount(String paramString) {
        Cursor cursor = mSqLiteDatabase.rawQuery("select * from detect_record where over_weight > 0 and detect_location = '" + paramString + "'", null);
        Integer locationCount = cursor.getCount();
        cursor.close();
        return locationCount;
    }

    public Cursor getMaxDateFromDetectRecord() {
        return mSqLiteDatabase.rawQuery("select max(strftime('%Y-%m', detect_time)), strftime('%Y', detect_time), strftime('%m', detect_time) from detect_record where over_weight > 0", null);
    }

    public int getMaxId() {
        int j = -1;
        try {
            Cursor cursor = mSqLiteDatabase.rawQuery("select max(id) from detect_record", null);
            int i = j;
            if (cursor != null) {
                i = j;
                if (cursor.moveToFirst())
                    i = cursor.getInt(0);
            }
            return i;
        } catch (Exception localException) {
        }
        return -1;
    }

    public Cursor getMaxIdFromDetectRecord() {
        return mSqLiteDatabase.rawQuery("select max(id) from detect_record", null);
    }

    public Cursor getMinDateFromDetectRecord() {
        return mSqLiteDatabase.rawQuery("select min(strftime('%Y-%m', detect_time)), strftime('%Y', detect_time), strftime('%m', detect_time) from detect_record where over_weight > 0", null);
    }

    public ArrayList <Double> getSensorCalibrationValue(String sensorName) {
        ArrayList <Double> sensorCalibrationValueList = new ArrayList();
        String sql = "select * from " + T_CALIBRATEDATA + " where ";
        String sensor;
        if (sensorName.equals("A")) {
            sensor = "A号";
        } else {
            sensor = "B号";
        }
        sql += " '" + sensor + "' order by value";
        Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
        if ((sensorName != null) && (cursor.moveToFirst()))
            do
                sensorCalibrationValueList.add(Double.valueOf(cursor.getInt(cursor.getColumnIndex("value"))));
            while (cursor.moveToNext());
        cursor.close();
        return sensorCalibrationValueList;
    }

    public ArrayList <Double> getSensorLoadValue(String sensorName) {
        ArrayList <Double> sensorLoadValues = new ArrayList <>();
        String sql = "select * from calibrate_data where sensor = '";
        String sensor;
        if (sensorName.equals("A")) {
            sensor = "A号";
        } else {
            sensor = "B号";
        }
        sql += sensor + "' order by loadedWeight";
        Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
        if ((cursor != null) && (cursor.moveToFirst())) {
            do
                sensorLoadValues.add(cursor.getDouble(cursor.getColumnIndex("loadedWeight")));
            while (cursor.moveToNext());
            cursor.close();
        }
        return sensorLoadValues;
    }

    public int getSomeOneDetectRecordByTime(String time) {
        String sql = "select id from detect_record  where detect_time = '" + time + "'";
        Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
        if ((cursor != null) && (cursor.moveToFirst()))
            return cursor.getInt(cursor.getColumnIndex("id"));
        return -1;
    }

    public Cursor getStaticValue() {
        return mSqLiteDatabase.rawQuery("select * from calibrate_data order by sensor,loadedWeight", null);
    }

    public int getSum() {
        String sql = "select count(*) from " + T_DETECTRECORD;
        Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        return -1;
    }

    public Cursor getTruckType() {
        String sql = "SELECT DISTINCT truck_type FROM " + T_DETECTRECORD;
        return mSqLiteDatabase.rawQuery(sql, null);
    }

    public Cursor getTruckTypeCursor() {
        String sql = "select DISTINCT truck_type, count(*) from detect_record \nWHERE truck_type <> '未识别车型'\nGROUP BY truck_type ;";
        return mSqLiteDatabase.rawQuery(sql, null);
    }

    public Record insertDetectRecord(int userId, Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("detect_time", record.dateTime);
        contentValues.put("user_id", userId);
        contentValues.put("detect_location", record.detectLocation);
        contentValues.put("plate_number", record.carNumber);
        contentValues.put("plate_photo", record.platePhoto);
        contentValues.put("axle_number", record.axleNumber);
        contentValues.put("truck_type", record.truckType);
        contentValues.put("speed", record.speed);
        contentValues.put("weight", record.weight);
        contentValues.put("limit_weight", record.limit_weight);
        contentValues.put("over_weight", record.overWeight);
        contentValues.put("truck_photo", record.bigPhoto);
        mSqLiteDatabase.beginTransaction();
        try {
            mSqLiteDatabase.insert(T_DETECTRECORD, null, contentValues);
            record.id = getMaxId();
            mSqLiteDatabase.setTransactionSuccessful();
            return record;
        } catch (Exception e) {
            e.printStackTrace();
            return record;
        } finally {
            mSqLiteDatabase.endTransaction();
        }
    }

    public boolean isStaticValueExists(String sensorName, double loadedWeight) {
        String sql = "select * from " + T_CALIBRATEDATA + " where sensor=? and loadedWeight=?";
        Cursor cursor = mSqLiteDatabase.rawQuery(sql, new String[]{sensorName, Double.toString(loadedWeight)});
        boolean exist = false;
        if (cursor != null && cursor.moveToFirst()) {
            exist = true;
        }
        cursor.close();
        return exist;
    }

    public void open()
            throws SQLException {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext, DB_NAME);
        if (mSqLiteDatabase == null) {
            mSqLiteDatabase = dataBaseHelper.openDataBase();
        } else {
            mSqLiteDatabase.close();
        }
    }

    public long updateDetectRecord(Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("detect_time", record.dateTime);
        contentValues.put("user_id", 1);
        contentValues.put("detect_location", record.detectLocation);
        contentValues.put("plate_number", record.carNumber);
        contentValues.put("plate_photo", record.platePhoto);
        contentValues.put("axle_number", record.axleNumber);
        contentValues.put("truck_type", record.truckType);
        contentValues.put("speed", record.speed);
        contentValues.put("weight", record.weight);
        contentValues.put("over_weight", record.overWeight);
        contentValues.put("truck_photo", record.bigPhoto);
        contentValues.put("uploaded", 0);
        return updateResult(record, contentValues);
    }

    public long updateDetectRecordAxis(Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("special_tag", record.special_tag);
        contentValues.put("limit_weight", record.limit_weight);
        contentValues.put("license_pic", record.license_pic);
        contentValues.put("sub_wheel", record.sub_wheel);
        contentValues.put("sub_wheel_pic", record.sub_wheel_pic);
        contentValues.put("air_suspension", record.air_suspension);
        contentValues.put("air_suspension_pic", record.air_suspension_pic);
        contentValues.put("over_weight", record.overWeight);
        return updateResult(record, contentValues);
    }

    private int updateResult(Record record, ContentValues contentValues) {// rows updated
        if (record.id != -1) {
            return mSqLiteDatabase.update(T_DETECTRECORD, contentValues, "id=?", new String[]{String.valueOf(record.id)});
        } else {
            return mSqLiteDatabase.update(T_DETECTRECORD, contentValues, "detect_time=?", new String[]{record.dateTime});
        }
    }
    public void deleteRecord(String detect_time) {
        mSqLiteDatabase.delete(T_DETECTRECORD, "detect_time = ?", new String[]{detect_time});
    }

    public long updateDetectRecordLimit(Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("weight", record.weight);
        contentValues.put("over_weight", record.overWeight);
        contentValues.put("truck_type", record.truckType);
        contentValues.put("limit_weight", record.limit_weight);
        return mSqLiteDatabase.update(T_DETECTRECORD, contentValues, "detect_time=?", new String[]{record.dateTime});
    }

    public long updateResult(Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("plate_number", record.carNumber);
        return mSqLiteDatabase.update(T_DETECTRECORD, contentValues, "detect_time=?", new String[]{record.dateTime});
    }
    public void updateDetectStatus(int id) {
        String sql = "update detect_record set uploaded =1 where id =" + id;
        mSqLiteDatabase.execSQL(sql);
    }

    public long updateStaticValue(String sensorName, double loadedWeight, long value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("value", value);
        String str = Double.toString(loadedWeight);
        return mSqLiteDatabase.update(T_CALIBRATEDATA, contentValues, "sensor=? and loadedWeight=?", new String[]{sensorName, str});
    }

    public int updateUserPw(long userId, String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", userId);
        contentValues.put("password", new Md5(password).get32());
        return mSqLiteDatabase.update(T_USERS, contentValues, null, null);
    }

    public boolean addUser(String userName, String password) {
        String passwordEncrypt = new Md5(password).get32();
        String sql = "insert into " + T_USERS + " values (";
        sql += getMaxUserId() + 1;
        sql += ",\"" + userName + "\",";
        sql += "\"" + passwordEncrypt + "\"," + "1)";
        mSqLiteDatabase.execSQL(sql);
        return true;
    }

    public String getAdminPassword() {
        String sql = "select password from " + T_USERS + " where id =1";
        Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
        String password = new Md5("123456").get32();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Log.d("getAdminPassword", cursor.getString(0) + ":" + cursor.getColumnName(0));
                password = cursor.getString(0);
            }
            cursor.close();
        }
        return password;
    }

    public boolean isUserNameExist(String userName) {
        String sql = "select * from " + T_USERS + " where name = '"+userName +"' ";
        Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Log.d("getAdminPassword", cursor.getString(0) + ":" + cursor.getColumnName(0));
                cursor.close();
                return true;
            }else{
                cursor.close();
                return false;
            }

        }else{
            return false;
        }

    }

    public String getUsers() {
        String sql = "select name from " + T_USERS + " where id <>1";
        Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
        String password = new Md5("123456").get32();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Log.d("getAdminPassword", cursor.getString(0) + ":" + cursor.getColumnName(0));
                password = cursor.getString(0);
            }
            cursor.close();
        }
        return password;
    }
    public int getMaxUserId() {
        int j = -1;
        try {
            Cursor cursor = mSqLiteDatabase.rawQuery("select max(id) from " + T_USERS, null);
            int i = j;
            if (cursor != null) {
                i = j;
                if (cursor.moveToFirst())
                    i = cursor.getInt(0);
            }
            return i;
        } catch (Exception localException) {
        }
        return -1;
    }

}