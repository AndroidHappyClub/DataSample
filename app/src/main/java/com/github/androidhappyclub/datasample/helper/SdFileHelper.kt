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

import com.ave.vastgui.core.ResultCompat
import com.ave.vastgui.tools.manager.filemgr.FileMgr
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class SdFileHelper {

    // 往SD卡写入文件
    fun sava(filename: String, content: String): ResultCompat<String> {
        val file = File(FileMgr.appInternalFilesDir(), filename)
        return FileMgr.saveFile(file).apply {
            result.onSuccess {
                // 不要用openFileOutput了,那个是往手机内存中写数据的
                val output = FileOutputStream(file, true)
                // 将String字符串以字节流的形式写入到输出流中
                output.write(content.toByteArray())
                //关闭输出流
                output.close()
            }
        }
    }

    // 读取SD卡中文件
    fun read(filename: String): String {
        val file = File(FileMgr.appInternalFilesDir(), filename)
        val sb = StringBuilder()
        //打开文件输入流
        val input = FileInputStream(file)
        val temp = ByteArray(1024)
        var len: Int
        //读取文件内容:
        while (input.read(temp).also { len = it } > 0) {
            sb.append(String(temp, 0, len))
        }
        //关闭输入流
        input.close()
        return sb.toString()
    }

    fun delete(filename: String): Boolean {
        return FileMgr.deleteFile(File(FileMgr.appInternalFilesDir(), filename)).isFailure
    }

}