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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ave.vastgui.tools.view.toast.SimpleToast
import com.ave.vastgui.tools.viewbinding.viewBinding
import com.github.androidhappyclub.datasample.databinding.ActivityFileBinding
import com.github.androidhappyclub.datasample.helper.FileHelper
import java.io.IOException

class FileActivity : AppCompatActivity(R.layout.activity_file) {

    private val binding by viewBinding(ActivityFileBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnClean.setOnClickListener {
            binding.etContent.setText("")
            binding.etFileName.setText("")
        }

        binding.btnSave.setOnClickListener {
            val helper = FileHelper(this)
            val saveName = binding.etFileName.text.toString()
            val saveContent = binding.etContent.text.toString()
            try {
                helper.save(saveName, saveContent)
                SimpleToast.showShortMsg("数据写入成功")
            } catch (e: Exception) {
                SimpleToast.showShortMsg("数据写入失败")
            }
        }

        binding.btnRead.setOnClickListener {
            val helper = FileHelper(this)
            var readContent = ""
            try {
                val readName = binding.etFileName.text.toString()
                readContent = helper.read(readName)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            SimpleToast.showShortMsg(if (readContent === "") "文件读取失败！" else readContent)
        }

        binding.btnDelete.setOnClickListener {
            val helper = FileHelper(this)
            var deleteContent = false
            try {
                val deleteName = binding.etFileName.text.toString()
                deleteContent = helper.delete(deleteName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            SimpleToast.showShortMsg(if (deleteContent) "删除文件成功！" else "删除文件失败！")
        }

        binding.btnFileList.setOnClickListener {
            val helper = FileHelper(this)
            var fileName = ""
            try {
                val fs = helper.fileList()
                for (file in fs) {
                    fileName += file + "\n"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            SimpleToast.showShortMsg(if (fileName === "") "没有找到文件！" else fileName)
        }
    }

}