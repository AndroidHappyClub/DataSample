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

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.provider.ContactsContract
import android.provider.ContactsContract.RawContacts
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.ave.vastgui.tools.utils.IntentUtils
import com.ave.vastgui.tools.utils.permission.requestPermission
import com.ave.vastgui.tools.viewbinding.viewBinding
import com.github.androidhappyclub.datasample.accounts.AccountHelper
import com.github.androidhappyclub.datasample.adapter.ContentProviderAdapter
import com.github.androidhappyclub.datasample.adapter.model.Contact
import com.github.androidhappyclub.datasample.adapter.model.Sms
import com.github.androidhappyclub.datasample.databinding.ActivityContentProviderBinding
import com.github.androidhappyclub.datasample.log.mLogFactory
import com.github.androidhappyclub.datasample.provider.ContactEmail
import com.github.androidhappyclub.datasample.provider.ContactName
import com.github.androidhappyclub.datasample.provider.ContactPhone
import com.github.androidhappyclub.datasample.provider.buildContactsScope

class ContentProviderActivity : AppCompatActivity(R.layout.activity_content_provider) {

    private val binding by viewBinding(ActivityContentProviderBinding::bind)
    private val accountHelper = AccountHelper(this)
    private val cpAdapter = ContentProviderAdapter(this)
    private val logger = mLogFactory.getLog(ContentProviderActivity::class.java)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.contents.layoutManager = LinearLayoutManager(this)
        // 获取的是哪些列的信息
        binding.contents.adapter = cpAdapter
        binding.getAccounts.setOnClickListener {
            accountHelper.getAccounts {
                if (binding.contents.childCount > 0) {
                    binding.contents.removeAllViews()
                    cpAdapter.clear()
                }
                it.forEach { account ->
                    cpAdapter.addAccount(account)
                }
            }
        }
        binding.btnWriteSms.setOnClickListener {
            IntentUtils.sendMmsMessage(
                this,
                "这是一条测试短信",
                "123456789"
            )
        }
        binding.btnReadSms.setOnClickListener {
            requestPermission(Manifest.permission.READ_SMS) {
                granted = {
                    readSms()
                }
            }
        }
        binding.btnWriteContacts.setOnClickListener {
            requestPermission(Manifest.permission.WRITE_CONTACTS) {
                granted = {
                    writeContacts()
                }
            }
        }
        binding.btnReadContacts.setOnClickListener {
            requestPermission(Manifest.permission.READ_CONTACTS) {
                granted = {
                    readContacts()
                }
            }
        }
    }

    // 读取短信
    @RequiresPermission(value = Manifest.permission.READ_SMS)
    private fun readSms() {
        if (binding.contents.childCount > 0) {
            binding.contents.removeAllViews()
            cpAdapter.clear()
        }
        val cursor =
            contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, null) ?: return
        with(cursor) {
            while (moveToNext()) {
                val address = getStringOrNull(getColumnIndex(Telephony.Sms.ADDRESS))
                val date = getLongOrNull(getColumnIndex(Telephony.Sms.DATE))
                val type = getIntOrNull(getColumnIndex(Telephony.Sms.TYPE))
                val body = getStringOrNull(getColumnIndex(Telephony.Sms.BODY))
                val name = getNameFromAddress(address)
                logger.d("$address 对应的名字为 $name")
                cpAdapter.addSms(Sms(name, address, date, type, body))
            }
        }
        cursor.close()
    }

    // 读取手机联系人
    @RequiresPermission(value = Manifest.permission.READ_CONTACTS)
    private fun readContacts() {
        if (binding.contents.childCount > 0) {
            binding.contents.removeAllViews()
            cpAdapter.clear()
        }
        // 查询raw_contacts表获得联系人的id
        // 查询联系人数据
        val cursor = contentResolver.query(Phone.CONTENT_URI, null, null, null, null) ?: return
        with(cursor) {
            while (moveToNext()) {
                //获取联系人姓名,手机号码
                val name = getStringOrNull(getColumnIndex(Phone.DISPLAY_NAME))
                val number = getStringOrNull(getColumnIndex(Phone.NUMBER))
                cpAdapter.addContact(Contact(name, number))
            }
        }
        cursor.close()
    }

    // 查询指定电话的联系人信息
    private fun queryContact(number: String) {
        val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, number)
        val cursor = contentResolver.query(
            uri,
            arrayOf(
                Phone.DISPLAY_NAME,
                RawContacts.ACCOUNT_NAME,
                RawContacts.ACCOUNT_TYPE
            ),
            null,
            null,
            null
        ) ?: return
        with(cursor) {
            if (moveToFirst()) {
                val name = getStringOrNull(getColumnIndex(Phone.DISPLAY_NAME))
                val accountName = getStringOrNull(getColumnIndex(RawContacts.ACCOUNT_NAME))
                val accountType = getStringOrNull(getColumnIndex(RawContacts.ACCOUNT_TYPE))
                binding.content.text = "$number 对应的联系人名称：$name $accountName $accountType"
            }
        }
        cursor.close()
    }

    // 添加一个新的联系人
    @SuppressLint("MissingPermission")
    private fun writeContacts() {
        // 使用事务添加联系人
        // 将下列内容添加到手机联系人中
        contentResolver.buildContactsScope {
            rawContacts(null)
            name(ContactName(displayName = "android"))
            phone(ContactPhone(number = "13888888888"))
            email(ContactEmail(address = "13888888888@qq.com"))
        }
        Toast.makeText(applicationContext, "添加成功", Toast.LENGTH_SHORT).show()
    }

    // 通过查询电话查找表从地址中获取姓名
    private fun getNameFromAddress(address: String?): String? {
        if (address == null) {
            return null
        }
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(address)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        val cursor = contentResolver.query(uri, projection, null, null, null) ?: return null
        var name: String? = null
        if (cursor.moveToFirst()) {
            name =
                cursor.getStringOrNull(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
        }
        cursor.close()
        return name
    }
}