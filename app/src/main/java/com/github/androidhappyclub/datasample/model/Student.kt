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

package com.github.androidhappyclub.datasample.model

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns
import android.provider.BaseColumns._ID
import java.net.URI

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2024/2/8
// Description: 定义存储在 Sqlite 内的学生对象
// Documentation: https://developer.android.com/training/data-storage/sqlite#DefineContract

class Student(private val id: Int, val name: String, val sex: String, private val age: Int) {

    fun id(): String = id.toString()
    fun age(): String = age.toString()

    companion object : BaseColumns {
        const val TABLE_NAME = "student"
        const val COLUMN_NAME = "name"
        const val COLUMN_SEX = "sex"
        const val COLUMN_AGE = "age"
        const val COLUMN_ID = _ID

        private const val SCHEME = "content://"
        const val AUTHORITY = "com.github.androidhappyclub.datasample.studentprovider"
        const val STUDENT_TABLE_MIME =
            "vnd.android.cursor.dir/vnd.${AUTHORITY}.student"
        const val STUDENT_MIME =
            "vnd.android.cursor.item/vnd.${AUTHORITY}.student"
        val AUTHORITY_URI = Uri.parse("$SCHEME$AUTHORITY")
        val CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME)
    }
}