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

import android.content.Context
import java.io.IOException

class FileHelper(private val context: Context) {

    /**
     * 向指定的文件中写入指定的数据
     */
    fun save(filename: String, message: String) {
        // 获得FileOutputStream
        val fileOutputStream =
            context.openFileOutput(filename, Context.MODE_PRIVATE + Context.MODE_APPEND)
        val bytes = message.toByteArray() // 将要写入的字符串转换为byte数组
        fileOutputStream.write(bytes) //将byte数组写入文件
        fileOutputStream.close() //关闭文件输出流
    }

    /**
     * 这里定义的是文件读取的方法
     */
    fun read(filename: String): String {
        //打开文件输入流
        val fis = context.openFileInput(filename)
        val temp = ByteArray(1024)
        val sb = StringBuilder()
        var len: Int

        //读取文件内容:
        while (fis.read(temp).also { len = it } > 0) {
            sb.append(String(temp, 0, len))
        }

        //关闭输入流
        fis.close()
        return sb.toString()
    }

    fun delete(filename: String): Boolean {
        return context.deleteFile(filename)
    }

    fun fileList(): Array<String> {
        return context.fileList()
    }
}