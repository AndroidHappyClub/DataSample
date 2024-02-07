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

package com.github.androidhappyclub.datasample.os

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.ave.vastgui.core.extension.SingletonHolder
import com.github.androidhappyclub.datasample.ContentProviderActivity
import java.lang.ref.WeakReference

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2024/2/7
// Description: 用来监听短信
// Documentation: https://www.yuque.com/mashangxiayu/gne1e3/tgxsm100xngx3tg6#m8sQ0

class SMSHandler private constructor(mActivity: ContentProviderActivity) :
    Handler(Looper.getMainLooper()) {

    private val mWr: WeakReference<ContentProviderActivity> = WeakReference(mActivity)

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            ContentProviderActivity.NEW_MESSAGE -> {
                mWr.get()?.mLogger?.d(msg.obj.toString())
            }
        }
    }

    companion object : SingletonHolder<SMSHandler, ContentProviderActivity>(::SMSHandler)
}