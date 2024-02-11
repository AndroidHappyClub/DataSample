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

package com.github.androidhappyclub.datasample.observer

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.provider.Telephony
import androidx.core.database.getStringOrNull
import com.github.androidhappyclub.datasample.ContentProviderActivity
import com.github.androidhappyclub.datasample.log.mLogFactory

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2024/2/7
// Description: 用来监听短信
// Documentation: https://www.yuque.com/mashangxiayu/gne1e3/tgxsm100xngx3tg6#m8sQ0

class SMSObserver(
    private val mHandler: Handler,
    private val mContext: Context
) : ContentObserver(mHandler) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        val uri = Telephony.Sms.Inbox.CONTENT_URI
        val projections = arrayOf(
            Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE
        )
        val cursor = mContext.contentResolver.query(
            uri, projections, null, null, "date desc"
        ) ?: return
        val sb = StringBuffer()
        with(cursor) {
            if (cursor.moveToFirst()) {
                sb.append("${getStringOrNull(getColumnIndex(Telephony.Sms.ADDRESS))}")
                sb.append("${getStringOrNull(getColumnIndex(Telephony.Sms.BODY))}")
                sb.append("${getStringOrNull(getColumnIndex(Telephony.Sms.DATE))}")
                mHandler
                    .obtainMessage(ContentProviderActivity.NEW_MESSAGE, sb.toString())
                    .sendToTarget()
                sb.delete(0, sb.length)
            }
            close()
        }
    }

}