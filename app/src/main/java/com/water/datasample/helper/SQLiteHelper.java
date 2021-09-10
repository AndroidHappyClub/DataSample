package com.water.datasample.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static String _dname = "ds.db";// 数据库名称
    private static SQLiteDatabase.CursorFactory _factory = null;
    private static int _version = 1;

    private SQLiteDatabase _sd; // 声明SQLiteDatabase对象
    private Context _ctx;

    public SQLiteHelper(Context ctx) {

        this(ctx, 1);
    }

    public SQLiteHelper(Context ctx, int version) {
        this(ctx, _dname, null, version);
    }

    public SQLiteHelper(Context ctx, String database) {
        this(ctx, database, _factory, _version);
    }

    public SQLiteHelper(Context ctx, String database, int version) {
        this(ctx, database, _factory, version);
    }

    public SQLiteHelper(Context ctx, String database,
                        SQLiteDatabase.CursorFactory factory,
                        int version) {
        super(ctx, database, factory, version);
        _ctx = ctx;
        _dname = database;
        _factory = factory;
        _version = version;
    }

    // 数据库第一次创建时被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        _sd = db;
        //创建表
        String sql = "CREATE TABLE student(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name text, sex text)";
        _sd.execSQL(sql);
    }
    // 插入
    public void insert(String tablename, ContentValues values){
        if (_sd == null || !_sd.isOpen())
            _sd = getWritableDatabase();

        long count = _sd.insert(tablename,null,values);
        _sd.close();
    }

    // 查询
    public Cursor query(String tablename, String[] columns,
                        String limit){
        if (_sd == null || !_sd.isOpen())
            _sd = getWritableDatabase();

        return _sd.query(tablename, columns,null,
                null,null,null,null, limit);
    }

    // 删除
    public void delete(String tablename, int id){
        if (_sd == null || !_sd.isOpen())
            _sd = getWritableDatabase();

        _sd.delete(tablename,"id=?", new String[]{String.valueOf(id)});
        _sd.close();
    }

    public long getCount(String tablename){
        if (_sd == null || !_sd.isOpen())
            _sd = getWritableDatabase();

        Cursor cursor =  _sd.query(tablename, new String[]{"COUNT(*)"},null,
                null,null,null,null);
        cursor.moveToFirst();
        long ret = cursor.getLong(0);
        cursor.close();
        return ret;
    }

    public void execSQL(String sql)
    {
        if (_sd == null || !_sd.isOpen())
            _sd = getWritableDatabase();

        _sd.execSQL(sql);
    }

    public boolean IsTableExist(String tablename) {
        if (_sd == null || !_sd.isOpen())
            _sd = getWritableDatabase();

        Cursor cursor = _sd.rawQuery("SELECT count(*) FROM sqlite_master " +
                "WHERE type='table' AND name='" + tablename + "'", null);

        boolean result = cursor.getInt(0)==0;

        cursor.close();

        return result;
    }

    // 关闭数据库
    public void close(){
        if (_sd != null || _sd.isOpen())
            _sd.close();
    }

    // 软件版本号发生改变时调用
    @Override
    public void onUpgrade(SQLiteDatabase sd, int i, int i1) {
        _sd = sd;
        _sd.execSQL("ALTER TABLE student ADD age INTEGER");
    }

    public void deleteDb(String tablename) {

        _ctx.deleteDatabase(tablename);
    }
}