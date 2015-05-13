package com.wu;

import java.io.StringWriter;

import org.codehaus.jackson.map.ObjectMapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {
	
	private static final String KEY_OBJECT = "object";
    private static final String TAG = "DbAdapter";
    private DatabaseHelper SDD_DbHelper;
    private SQLiteDatabase mDb;
    private static ObjectMapper mapper = new ObjectMapper(); // JACKSON
    
    private static final String FFT_TABLE_CREATE =
        "create table FFT_DATA_SET (id integer primary key,"
        + "object text not null);";
    
    private static final String DB4_TABLE_CREATE =
        "create table DB4_DATA_SET (id integer primary key,"
        + "object text not null);";
    
    private static final String HAAR_TABLE_CREATE =
        "create table HAAR_DATA_SET (id integer primary key,"
        + "object text not null);";

    private static final String DATABASE_NAME = "MYDB_DATABASE";
    private static final String FFT_TABLE = "FFT_DATA_SET";
    private static final String DB4_TABLE = "DB4_DATA_SET";
    private static final String HAAR_TABLE = "HAAR_DATA_SET";
    private static final int DATABASE_VERSION = 2;

    private final Context context;
    
    //DatabaseHelper
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(FFT_TABLE_CREATE);
            db.execSQL(DB4_TABLE_CREATE);
            db.execSQL(HAAR_TABLE_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS MYDB_DATABASE");
            onCreate(db);
        }
    }
    
    public DbAdapter(Context context) {
        this.context = context;
    }
 
    public DbAdapter open() throws SQLException {
    	SDD_DbHelper = new DatabaseHelper(context);
    	mDb = SDD_DbHelper.getWritableDatabase();
    	Log.e("db", "成功得到一个可写的数据库");
        return this;
    }
    
    public void close() {
    	SDD_DbHelper.close();
    	Log.e("db", "关闭了数据库");

    }
    
    public void addLearnSet(int mode,String type, double max, double mean, double[] ed) {
        ContentValues value = new ContentValues();
        StringWriter writer =  new StringWriter();

        DataSet set = new DataSet(mode,type,max,mean,ed);
        try {
			mapper.writeValue(writer, set);
		} 
        catch (Exception e) {
			e.printStackTrace();
        }
        value.put(KEY_OBJECT, writer.toString());
    	String table = findTable(mode);
        mDb.insert(table, null, value);
        Log.e("db", "添加一个"+table+":"+type+"set成功！");
        Log.e("db", "放入数据库的string 是 "+writer.toString());

    }
    
    public Cursor fetchAllSet(int mode) throws SQLException {
    	String table = findTable(mode);
        Cursor c = mDb.query(true, table, new String[] {KEY_OBJECT}, null, null,
                    null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            Log.e("db","指针不为空");
        }
        else
        {
        	Log.e("db", "指针是空的并没有搜寻到数据");
        }
        return c;
    }
    
    private String findTable(int mode){
    	String rightTable = null;
        if(mode == 1)
        {
            rightTable = DB4_TABLE;
        }
        else if(mode == 2)
        {
        	rightTable = HAAR_TABLE;
        }
        else if(mode == 3)
        {
        	rightTable = FFT_TABLE;
        }
        else
        {
        	Log.e("db", "学习数据并没有声明采样类型");//调试
        }
        return rightTable;
    }
    
}
