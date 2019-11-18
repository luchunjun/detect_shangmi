package com.lu.portable.detect.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lu.portable.detect.util.Md5;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int CURRENT_VERSION = 1;
    private final Context myContext;
    private final String myDBName;
    private SQLiteDatabase myDataBase;

    public DataBaseHelper(Context context, String dbName) {
        super(context, dbName, null, CURRENT_VERSION);
        myContext = context;
        myDBName = dbName;
    }

    private void createDatabase(SQLiteDatabase database) {
        try {
            String userCreateSql = "CREATE TABLE IF NOT EXISTS " + DatabaseAdapter.T_USERS;
            userCreateSql += "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, password TEXT NOT NULL,roleType boolean NOT NULL default 1)";
            database.execSQL(userCreateSql);
            String addAdminSql = "insert into " + DatabaseAdapter.T_USERS;
            addAdminSql += " values(1 ,'管理员' ,'" + new Md5("123456").get32() + "',0)";
            database.execSQL(addAdminSql);
            String detectCreateSql = "CREATE TABLE IF NOT EXISTS " + DatabaseAdapter.T_DETECTRECORD;
            detectCreateSql += " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, detect_time TEXT NOT NULL, user_id INTEGER NOT NULL,detect_location TEXT NOT NULL,";
            detectCreateSql += " plate_number TEXT NOT NULL,truck_photo TEXT,plate_photo TEXT,axle_number INTEGER NOT NULL DEFAULT 0,truck_type TEXT NOT NULL DEFAULT '1 - 1',";
            detectCreateSql += " speed REAL NOT NULL DEFAULT 0.0,weight REAL NOT NULL DEFAULT 0.0,over_weight REAL NOT NULL DEFAULT 0.0,length REAL NOT NULL DEFAULT 0.0,";
            detectCreateSql += " over_length REAL NOT NULL DEFAULT 0.0, width REAL NOT NULL DEFAULT 0.0, over_width REAL NOT NULL DEFAULT 0.0, height REAL NOT NULL DEFAULT 0.0,";
            detectCreateSql += " over_height REAL NOT NULL DEFAULT 0.0,uploaded boolean default 0,";
            detectCreateSql += " special_tag boolean default 0,limit_weight real default 0.0,license_pic TEXT,sub_wheel INTEGER default 0,sub_wheel_pic TEXT,air_suspension boolean default 0,air_suspension_pic TEXT)";
            database.execSQL(detectCreateSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param database The database.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        createDatabase(database);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase openDataBase() {
        myDataBase = getReadableDatabase();
        // createDatabase(myDataBase);
        Log.i("tty", "database version" + myDataBase.getVersion());
        return myDataBase;
    }
}