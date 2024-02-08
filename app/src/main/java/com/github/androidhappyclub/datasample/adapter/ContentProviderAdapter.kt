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

package com.github.androidhappyclub.datasample.adapter

import android.accounts.Account
import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.BindingAdapter
import com.ave.vastgui.adapter.VastBindAdapter
import com.ave.vastgui.tools.view.avatar.Avatar
import com.ave.vastgui.tools.view.extension.refreshWithInvalidate
import com.github.androidhappyclub.datasample.BR
import com.github.androidhappyclub.datasample.model.Contact
import com.github.androidhappyclub.datasample.model.Sms
import com.github.androidhappyclub.datasample.log.mLogFactory
import com.github.androidhappyclub.datasample.model.Student

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/12/16

// 用于打印日志
private val logger = mLogFactory.getLog(ContentProviderAdapter::class.java)

class ContentProviderAdapter(context: Context) :
    VastBindAdapter(ArrayList(), context) {

    companion object {
        @JvmStatic
        @BindingAdapter("app:avatar_text")
        fun setAvatarText(view: Avatar, text: String?) {
            text?.also {
                view.refreshWithInvalidate {
                    mText = it[0].toString()
                }
            }
        }
    }

    override fun setVariableId(): Int = BR.item

    /**
     * 添加设备账户信息
     */
    fun addAccount(account: Account) {
        val index = itemCount
        mDataSource.add(AccountWrapper(account))
        notifyItemChanged(index)
    }

    /**
     * 添加短信对象
     */
    fun addSms(sms: Sms) {
        val index = itemCount
        mDataSource.add(SmsWrapper(sms))
        notifyItemChanged(index)
    }

    /**
     * 添加联系人对象
     */
    fun addContact(contact: Contact) {
        val index = itemCount
        mDataSource.add(ContactWrapper(contact))
        notifyItemChanged(index)
    }

    /**
     * 添加学生对象
     */
    fun addStudent(stu: Student) {
        val index = itemCount
        mDataSource.add(StuWrapper(stu))
        notifyItemChanged(index)
    }

    /**
     * 清空列表
     */
    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        mDataSource.clear()
        notifyDataSetChanged()
        logger.i("列表已清空")
    }

}