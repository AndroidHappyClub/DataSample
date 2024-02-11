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

package com.github.androidhappyclub.datasample

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.ave.vastgui.adapter.widget.AdapterClickListener
import com.ave.vastgui.tools.view.dialog.MaterialAlertDialogBuilder
import com.ave.vastgui.tools.viewbinding.viewBinding
import com.github.androidhappyclub.datasample.adapter.ContentProviderAdapter
import com.github.androidhappyclub.datasample.databinding.ActivityStudentDbBinding
import com.github.androidhappyclub.datasample.helper.StudentDbHelper
import com.github.androidhappyclub.datasample.log.mLogFactory
import com.github.androidhappyclub.datasample.model.Student
import com.google.android.material.textfield.TextInputEditText

class StudentDbActivity : AppCompatActivity(R.layout.activity_student_db) {

    private val mBinding by viewBinding(ActivityStudentDbBinding::bind)
    private val mLogger = mLogFactory.getLog(StudentDbActivity::class.java)
    private val mDialogBuilder by lazy {
        MaterialAlertDialogBuilder(this)
    }
    private val mAdapter = ContentProviderAdapter(this)
    private lateinit var mStudentDbHelper: StudentDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViews()
    }

    override fun onDestroy() {
        mStudentDbHelper.close()
        super.onDestroy()
    }

    private fun bindViews() {
        mStudentDbHelper = StudentDbHelper(this)

        mAdapter.registerClickEvent(object : AdapterClickListener {
            override fun onItemClick(view: View, pos: Int) {
                mDialogBuilder
                    .setMessage("真的要删除该记录吗？")
                    .setPositiveButton("是") { _, _ ->
                        //删除数据
                        mStudentDbHelper.delete(pos.toLong())
                        updateListView("0,19")
                    }
                    .setNegativeButton("否", null)
                    .show()
            }
        })

        mBinding.lvStudents.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@StudentDbActivity)
        }

        mBinding.addStudent.setOnClickListener {
            mDialogBuilder.apply {
                setTitle("新增学生")
                setView(R.layout.dialog_add_student)
                setPositiveButton("确定") { _, _ ->
                    val result = mStudentDbHelper.insert {
                        put(
                            Student.COLUMN_NAME,
                            findViewById<TextInputEditText>(R.id.stuName).text.toString()
                        )
                        put(
                            Student.COLUMN_SEX,
                            findViewById<TextInputEditText>(R.id.stuSex).text.toString()
                        )
                        put(
                            Student.COLUMN_AGE,
                            findViewById<TextInputEditText>(R.id.stuAge).text.toString()
                        )
                    }
                    if (-1L != result) {
                        val count = mStudentDbHelper.getCount()
                        updateListView(
                            String.format("%d,%d", if (count - 19 < 0) 0 else count - 19, count)
                        )
                    }
                }
                show()
            }
        }

        mBinding.addStudentByProvider.setOnClickListener {
            mDialogBuilder.apply {
                setTitle("新增学生")
                setView(R.layout.dialog_add_student)
                setPositiveButton("确定") { _, _ ->
                    val uri = contentResolver.insert(Student.CONTENT_URI, ContentValues().apply {
                        put(
                            Student.COLUMN_NAME,
                            findViewById<TextInputEditText>(R.id.stuName).text.toString()
                        )
                        put(
                            Student.COLUMN_SEX,
                            findViewById<TextInputEditText>(R.id.stuSex).text.toString()
                        )
                        put(
                            Student.COLUMN_AGE,
                            findViewById<TextInputEditText>(R.id.stuAge).text.toString()
                        )
                    })
                    uri?.apply {
                        contentResolver.notifyChange(this, null)
                        val count = mStudentDbHelper.getCount()
                        updateListView(
                            String.format("%d,%d", if (count - 19 < 0) 0 else count - 19, count)
                        )
                    }
                }
                show()
            }
        }

        mBinding.queryStudent.setOnClickListener {
            val count = mStudentDbHelper.getCount()
            mLogger.d("目前数据总数 $count")
            updateListView(String.format("%d,%d", (count - 19).coerceAtLeast(0), count))
        }

        //查询数据，获取游标
        updateListView()
    }

    private fun updateListView(limit: String? = null) {
        if (0 != mBinding.lvStudents.childCount) {
            mBinding.lvStudents.removeAllViews()
            mAdapter.clear()
        }
        val columns = arrayOf(
            BaseColumns._ID, Student.COLUMN_NAME,
            Student.COLUMN_SEX, Student.COLUMN_AGE
        )
        mStudentDbHelper.query(columns, limit).use {
            while (it.moveToNext()) {
                mAdapter.addStudent(it.newStudent())
            }
        }
    }

    private fun Cursor.newStudent(): Student {
        val id: Int = getIntOrNull(getColumnIndex(Student.COLUMN_ID)) ?: 0
        val name: String = getStringOrNull(getColumnIndex(Student.COLUMN_NAME)) ?: ""
        val sex: String = getStringOrNull(getColumnIndex(Student.COLUMN_SEX)) ?: ""
        val age: Int = getIntOrNull(getColumnIndex(Student.COLUMN_AGE)) ?: 0
        return Student(id, name, sex, age)
    }
}