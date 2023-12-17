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

package com.github.androidhappyclub.datasample.provider

import android.content.ContentResolver
import android.provider.ContactsContract
import android.provider.ContactsContract.RawContacts
import androidx.core.database.getStringOrNull
import java.lang.StringBuilder

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/12/15
// Description: 
// Documentation:
// Reference:

private val rawContactColumns = arrayOf(
    RawContacts.ACCOUNT_NAME,
    RawContacts.ACCOUNT_TYPE
)

fun ContentResolver.getRawContacts(projection: Array<String> = rawContactColumns): String {
    val uri = RawContacts.CONTENT_URI
    val sb = StringBuilder()
    val cursor =
        query(uri, projection, null, null, null) ?: return sb.toString()
    while (cursor.moveToNext()) {
        val name = cursor.getStringOrNull(cursor.getColumnIndex(RawContacts.ACCOUNT_NAME))
        val type = cursor.getStringOrNull(cursor.getColumnIndex(RawContacts.ACCOUNT_TYPE))
        sb.append("$name $type \n")
    }
    cursor.close()
    return sb.toString()
}