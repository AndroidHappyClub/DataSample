/*
 * MIT License
 *
 * Copyright (c) 2023 AndroidHappyClub
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.androidhappyclub.datasample.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper private constructor(
    private val context: Context,
    dataName: String? = null,
    factory: CursorFactory? = mFactory,
    version: Int = mVersion
) : SQLiteOpenHelper(context, dataName, factory, version) {
    private var _sd: SQLiteDatabase? = null // 声明SQLiteDatabase对象

    constructor(context: Context) : this(context, mDataName, null, mVersion)
    constructor(context: Context, version: Int) : this(context, mDataName, null, version)
    constructor(context: Context, database: String, version: Int) : this(
        context,
        database,
        mFactory,
        version
    )

    init {
        mDataName = dataName
        mFactory = factory
        mVersion = version
    }

    // 数据库第一次创建时被调用
    override fun onCreate(db: SQLiteDatabase) {
        _sd = db
        //创建表
        val sql = "CREATE TABLE student(id INTEGER PRIMARY KEY AUTOINCREMENT,name text,sex tex)"
        _sd?.execSQL(sql)
    }

    // 插入
    fun insert(tableName: String, values: ContentValues) {
        if (_sd == null || !_sd!!.isOpen) _sd = writableDatabase
        _sd!!.insert(tableName, null, values)
        _sd!!.close()
    }

    // 查询
    fun query(tableName: String, columns: Array<String>, limit: String): Cursor {
        if (_sd == null || !_sd!!.isOpen)
            _sd = writableDatabase
        return _sd!!.query(
            tableName, columns, null,
            null, null, null, null, limit
        )
    }

    // 删除
    fun delete(tableName: String, id: Int) {
        if (_sd == null || !_sd!!.isOpen) _sd = writableDatabase
        _sd!!.delete(tableName, "id=?", arrayOf(id.toString()))
        _sd!!.close()
    }

    fun getCount(tableName: String?): Long {
        if (_sd == null || !_sd!!.isOpen) _sd = writableDatabase
        val cursor = _sd!!.query(
            tableName, arrayOf("COUNT(*)"), null,
            null, null, null, null
        )
        cursor.moveToFirst()
        val ret = cursor.getLong(0)
        cursor.close()
        return ret
    }

    fun execSQL(sql: String?) {
        if (_sd == null || !_sd!!.isOpen) _sd = writableDatabase
        _sd!!.execSQL(sql)
    }

    fun isTableExist(tableName: String): Boolean {
        if (_sd == null || !_sd!!.isOpen) _sd = writableDatabase
        val cursor = _sd!!
            .rawQuery(
                "SELECT count(*) FROM sqlite_master WHERE type= 'table' AND name= 'tableName'",
                null
            )
        val result = cursor.getInt(0) == 0
        cursor.close()
        return result
    }

    // 关闭数据库
    override fun close() {
        _sd?.close()
    }

    // 软件版本号发生改变时调用
    override fun onUpgrade(sd: SQLiteDatabase, i: Int, i1: Int) {
        _sd = sd
        _sd!!.execSQL("ALTER TABLE student ADD age INTEGER")
    }

    fun deleteDb(tableName: String) {
        context.deleteDatabase(tableName)
    }

    companion object {
        private var mDataName: String? = "ds.db" // 数据库名称
        private var mFactory: CursorFactory? = null
        private var mVersion = 1
    }
}