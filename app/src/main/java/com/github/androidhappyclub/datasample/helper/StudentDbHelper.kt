/*
 * MIT License
 *
 * Copyright (c) 2024 AndroidHappyClub
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
import android.net.Uri
import androidx.core.database.getLongOrNull
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ave.vastgui.core.extension.nothing_to_do
import com.github.androidhappyclub.datasample.log.mLogFactory
import com.github.androidhappyclub.datasample.model.Student

/**
 * [StudentDbHelper]
 */
class StudentDbHelper(
    private val mContext: Context,
    mDataName: String = DATABASENAME,
    mFactory: CursorFactory? = null,
    mVersion: Int = VERSION
) : SQLiteOpenHelper(mContext, mDataName, mFactory, mVersion) {
    private val mLogger = mLogFactory.getLog(StudentDbHelper::class.java)
    private lateinit var mSd: SQLiteDatabase

    // 数据库第一次创建时被调用
    override fun onCreate(db: SQLiteDatabase) {
        mSd = db
        //创建表
        val sql = """
        CREATE TABLE ${Student.TABLE_NAME}(
            ${Student.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Student.COLUMN_NAME} TEXT,
            ${Student.COLUMN_SEX} TEXT,
            ${Student.COLUMN_AGE} INTEGER
        )
        """.trimIndent()
        mSd.execSQL(sql)
    }

    /**
     * 插入一条新的学生数据。
     *
     * @return 新插入行的行 ID，如果发生错误则为 -1 。
     */
    inline fun insert(values: ContentValues.() -> Unit): Long = writableDatabase.use {
        it.insert(Student.TABLE_NAME, null, ContentValues().also(values))
    }

    /**
     * 插入一条新的学生数据。
     *
     * @return 新插入行的行 ID，如果发生错误则为 -1 。
     */
    fun insert(values: ContentValues?): Long = writableDatabase.use {
        it.insert(Student.TABLE_NAME, null, values)
    }

    /**
     * 更新 [id] 对应学生的数据。
     */
    inline fun update(id: Long, values: ContentValues.() -> Unit): Int = writableDatabase.use {
        it.update(
            Student.TABLE_NAME,
            ContentValues().also(values),
            "${Student.COLUMN_ID} = ?",
            arrayOf("$id")
        )
    }

    /**
     * 查询学生数据。
     *
     * @param columns 待查询学生的字段，支持 [Student.COLUMN_ID]，[Student.COLUMN_NAME]，
     * [Student.COLUMN_SEX]和[Student.COLUMN_AGE]。
     * @param limit 对返回数量的限制。
     */
    fun query(
        columns: Array<String> = arrayOf(
            Student.COLUMN_ID,
            Student.COLUMN_NAME,
            Student.COLUMN_SEX,
            Student.COLUMN_AGE
        ),
        limit: String? = null
    ): Cursor = writableDatabase.query(
        Student.TABLE_NAME, columns, null,
        null, null, null, null, limit
    )

    /**
     * 删除 [id] 指定的学生。
     *
     * @return 如果传入 whereClause ，则影响的行数，否则为 0 。
     */
    fun delete(id: Long): Int = writableDatabase.use {
        it.delete(Student.TABLE_NAME, "${Student.COLUMN_ID} = ?", arrayOf(id.toString()))
    }

    /**
     * 查询数据库中学生数据的总数。默认值为 0 。
     */
    fun getCount(): Long = with(readableDatabase) {
        val cursor: Cursor = query(
            Student.TABLE_NAME, arrayOf("COUNT(*)"), null,
            null, null, null, null
        )
        cursor.moveToFirst()
        val count = cursor.getLongOrNull(0) ?: 0
        cursor.close()
        return count
    }

    fun isTableExist(tableName: String): Boolean {
        if (!mSd.isOpen) mSd = writableDatabase
        val cursor = mSd
            .rawQuery(
                "SELECT count(*) FROM sqlite_master WHERE type= 'table' AND name= 'tableName'",
                null
            )
        val result = cursor.getInt(0) == 0
        cursor.close()
        return result
    }

    /**
     * 软件版本号发生改变时调用
     */
    override fun onUpgrade(sd: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        nothing_to_do()
    }

    fun deleteDb(tableName: String) {
        mContext.deleteDatabase(tableName)
    }

    companion object {
        private const val DATABASENAME: String = "student.db" // 数据库名称
        private const val VERSION = 1
    }
}