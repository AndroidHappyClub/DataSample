package com.water.datasample.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private SharedPreferences _sp;
    private Context _ctx;

    public SharedPreferencesHelper(Context ctx, String sp) {
        this._ctx = ctx;
        _sp = _ctx.getSharedPreferences(sp, Context.MODE_PRIVATE);
    }

    // 定义一个保存数据的方法
    public void save(String key, String value) {
        SharedPreferences.Editor editor = _sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void save(String key, boolean value) {
        SharedPreferences.Editor editor = _sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    // 定义一个读取SP文件的方法
    public String readString(String key) {
        return _sp.getString(key, "");
    }

    public boolean readBoolean(String key) {
        return _sp.getBoolean(key, false);
    }

    public void clear(){
        SharedPreferences.Editor editor = _sp.edit();
        editor.clear();
        editor.commit();
    }
}
