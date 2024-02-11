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
package com.github.androidhappyclub.datasample.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.os.Build
import com.ave.vastgui.tools.config.ToolsConfig.init
import com.github.androidhappyclub.datasample.helper.StudentDbHelper
import com.github.androidhappyclub.datasample.model.Student
import com.github.androidhappyclub.datasample.model.Student.Companion.STUDENT_MIME
import com.github.androidhappyclub.datasample.model.Student.Companion.STUDENT_TABLE_MIME
import java.lang.NullPointerException

class StudentProvider : ContentProvider() {

    private lateinit var mStudentDbHelper: StudentDbHelper
    private val mContext by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireContext()
        } else {
            context
                ?: throw NullPointerException("Context only available once onCreate has been called")
        }
    }

    override fun onCreate(): Boolean {
        mStudentDbHelper = StudentDbHelper(mContext, mVersion = 1)
        return true
    }

    /**
     * 根据 [uri] 和其他条件查询学生。
     *
     * @throws IllegalArgumentException 如果 [uri] 不是指向学生表或者学生的，
     * 则会抛出该异常。
     */
    @Throws(IllegalArgumentException::class)
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when (mUriMatcher.match(uri)) {
            STUDENT_TABLE -> mStudentDbHelper.readableDatabase
                .query(
                    Student.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )

            STUDENT -> mStudentDbHelper.readableDatabase
                .query(
                    Student.TABLE_NAME,
                    projection,
                    "${Student.COLUMN_ID} == ?",
                    arrayOf(ContentUris.parseId(uri).toString()),
                    null,
                    null,
                    null
                )

            else -> throw IllegalArgumentException("Please check if $uri is correct.")
        }
    }

    /**
     * 根据 [uri] 将 [values] 内的值作为新行插入学生数据库。
     *
     * @param values 目前支持的字段有 [Student.COLUMN_NAME] ，[Student.COLUMN_SEX]
     * 和 [Student.COLUMN_AGE] 。
     * @return 如果插入失败则返回 null 。
     * @throws IllegalArgumentException 如果 [uri] 不是指向学生表的，
     * 则会抛出该异常。
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (STUDENT_TABLE != mUriMatcher.match(uri)) {
            throw IllegalArgumentException("Please check if $uri is correct.")
        }
        val index = mStudentDbHelper.insert(values)
        return if (-1L == index) {
            null
        } else {
            ContentUris.withAppendedId(Student.CONTENT_URI, index).apply {
                mContext.contentResolver.notifyChange(this, null)
            }
        }
    }

    /**
     * 根据 [uri] 和判断条件删除指定的学生。
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return when (mUriMatcher.match(uri)) {
            STUDENT -> mStudentDbHelper.delete(ContentUris.parseId(uri))
            STUDENT_TABLE -> mStudentDbHelper
                .writableDatabase.delete(Student.TABLE_NAME, selection, selectionArgs)

            else -> 0
        }
    }

    /**
     * 根据 [uri] 将 [values] 内的值更新到指定的学生。
     */
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if (null == values) return 0
        return when (mUriMatcher.match(uri)) {
            STUDENT -> mStudentDbHelper
                .writableDatabase.update(
                    Student.TABLE_NAME,
                    values,
                    "${Student.COLUMN_ID}=?",
                    arrayOf(ContentUris.parseId(uri).toString())
                )

            STUDENT_TABLE -> mStudentDbHelper
                .writableDatabase.update(Student.TABLE_NAME, values, selection, selectionArgs)

            else -> 0
        }
    }

    override fun getType(uri: Uri): String = when (mUriMatcher.match(uri)) {
        STUDENT_TABLE -> STUDENT_TABLE_MIME
        STUDENT -> STUDENT_MIME
        else -> throw IllegalArgumentException("Please check if the Uri is correct.")
    }

    /**
     * Companion
     *
     * @property STUDENT_TABLE 表示 URI 指向学生数据表
     * @property STUDENT 表示 URI 指向学生实体
     */
    companion object {
        private val mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private const val STUDENT_TABLE = 0x01
        private const val STUDENT = 0x02

        init {
            mUriMatcher.apply {
                addURI(Student.AUTHORITY, Student.TABLE_NAME, STUDENT_TABLE)
                addURI(Student.AUTHORITY, "${Student.TABLE_NAME}/#", STUDENT)
            }
        }
    }
}
