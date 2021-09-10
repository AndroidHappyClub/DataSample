package com.water.datasample.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.water.datasample.helper.SQLiteHelper;

public class CustomContentProvider extends ContentProvider {
    private static UriMatcher _um = new UriMatcher(UriMatcher.NO_MATCH);
    private SQLiteHelper _sh;

    static{
        _um.addURI("com.water.datasample.customprovider", "insert", 1);
    }

    @Override
    public boolean onCreate() {
        _sh = new SQLiteHelper(this.getContext(), 1);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch(_um.match(uri)){
            //把数据库打开放到里面是想证明uri匹配完成
            case 1:
                SQLiteDatabase db = _sh.getReadableDatabase();
                long rowindex = db.insert("student", null, values);

                if(rowindex > 0)
                {
                    //在前面已有的Uri后面追加ID
                    Uri nameUri = ContentUris.withAppendedId(uri, rowindex);
                    //通知数据已经发生改变
                    getContext().getContentResolver().notifyChange(nameUri, null);
                    return nameUri;
                }
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}
