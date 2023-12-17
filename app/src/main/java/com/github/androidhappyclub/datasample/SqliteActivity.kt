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

package com.github.androidhappyclub.datasample

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.CursorAdapter
import android.widget.SimpleCursorAdapter
import androidx.appcompat.app.AppCompatActivity
import com.ave.vastgui.tools.view.dialog.MaterialAlertDialogBuilder
import com.ave.vastgui.tools.viewbinding.viewBinding
import com.github.androidhappyclub.datasample.databinding.ActivitySqliteBinding
import com.github.androidhappyclub.datasample.helper.SQLiteHelper
import com.github.androidhappyclub.datasample.log.mLogFactory

class SqliteActivity : AppCompatActivity(R.layout.activity_sqlite) {

    private val binding by viewBinding(ActivitySqliteBinding::bind)
    private val logger = mLogFactory.getLog(SqliteActivity::class.java)
    private val mDialogBuilder by lazy {
        MaterialAlertDialogBuilder(this)
    }
    private lateinit var sqliteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "浏览学生信息"
        bindViews()
    }

    private fun bindViews() {
        sqliteHelper = SQLiteHelper(this)
        //查询数据，获取游标
        fillListView("0,19")
        //设置ListView单击监听器
        binding.lvStudents.onItemClickListener =
            OnItemClickListener { _: AdapterView<*>?, _: View?, _: Int, position: Long ->
                mDialogBuilder
                    .setMessage("真的要删除该记录吗？")
                    .setPositiveButton("是") { _, _ ->
                        //删除数据
                        sqliteHelper.delete("student", position.toInt())
                        fillListView("0,19")
                    }
                    .setNegativeButton("否", null)
                    .show()
            }

        binding.btnAdd.setOnClickListener {
            try {
                val cvs = ContentValues().apply {
                    put("name", "Lisi")
                    put("sex", "男")
                }
                sqliteHelper.insert("student", cvs)
                val count = sqliteHelper.getCount("student")
                fillListView(String.format("%d,%d", if (count - 19 < 0) 0 else count - 19, count))
            } catch (ex: Exception) {
                logger.i(ex.message.toString())
            }
        }

        binding.btnSearch.setOnClickListener {
            try {
                val count = sqliteHelper.getCount("student")
                fillListView(String.format("%d,%d", (count - 19).coerceAtLeast(0), count))
            } catch (ex: Exception) {
                logger.i(ex.message.toString())
            }
        }
    }

    private fun fillListView(limit: String) {
        val columns = arrayOf("id as _id", "name", "sex")
        // 查询数据，获取游标
        val cursor = sqliteHelper.query("student", columns, limit)
        //列表项数据
        val from = arrayOf("_id", "name", "sex")
        // 列表项ID
        val to = intArrayOf(R.id.tvID, R.id.tvName, R.id.tvSex)
        try {
            // 适配器
            val adapter = SimpleCursorAdapter(this, R.layout.list_view_item, cursor, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
            // 为列表视图添加适配器
            binding.lvStudents.adapter = adapter
        } catch (ex: Exception) {
            logger.i(ex.message.toString())
        }
    }
}