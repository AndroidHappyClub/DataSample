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

import android.Manifest
import android.content.ContentProviderOperation
import android.content.ContentProviderResult
import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.RawContacts
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.annotation.RequiresPermission

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/12/16

/**
 * 通讯录联系人的原始联系人信息
 *
 * @property name 参考 [RawContacts.ACCOUNT_NAME] 。
 * @property type 参考 [RawContacts.ACCOUNT_TYPE] 。
 */
data class RawAccount(val name: String, val type: String)

/**
 * 通讯录联系人姓名信息
 *
 * @property displayName 参考 [StructuredName.DISPLAY_NAME] 。
 * @property givenName 参考 [StructuredName.GIVEN_NAME] 。
 * @property familyName 参考 [StructuredName.FAMILY_NAME] 。
 * @property prefix 参考 [StructuredName.PREFIX] 。
 * @property middleName 参考 [StructuredName.MIDDLE_NAME] 。
 * @property suffix 参考 [StructuredName.SUFFIX] 。
 * @property phoneticGivenName 参考 [StructuredName.PHONETIC_GIVEN_NAME] 。
 * @property phoneticMiddleName 参考 [StructuredName.PHONETIC_MIDDLE_NAME] 。
 * @property phoneticFamilyName 参考 [StructuredName.PHONETIC_FAMILY_NAME] 。
 * @see StructuredName
 */
data class ContactName @JvmOverloads constructor(
    internal val displayName: String,
    internal val givenName: String? = null,
    internal val familyName: String? = null,
    internal val prefix: String? = null,
    internal val middleName: String? = null,
    internal val suffix: String? = null,
    internal val phoneticGivenName: String? = null,
    internal val phoneticMiddleName: String? = null,
    internal val phoneticFamilyName: String? = null,
)

/**
 * 通讯录联系人电话信息。
 *
 * @property number 参考 [Phone.NUMBER] 。
 * @property type 参考 [Phone.TYPE] 。
 * @property label 参考 [Phone.LABEL] ，用来表示 [type] 为 [Phone.TYPE_CUSTOM] 的
 * 实际类型。
 */
data class ContactPhone @JvmOverloads constructor(
    internal val number: String,
    internal val type: Int = Phone.TYPE_MOBILE,
    private val label: String? = null,
) {
    internal fun label(): String? {
        if (Phone.TYPE_CUSTOM == type && null == label)
            throw IllegalArgumentException("Error value of label.")
        return label
    }
}

/**
 * 通讯录联系人邮件信息。
 *
 * @property address 参考 [Email.ADDRESS] 。
 * @property type 参考 [Email.TYPE] 。
 * @property label 参考 [Email.LABEL] ，用来表示 [type] 为 [Email.TYPE_CUSTOM] 的
 * 实际类型。
 */
data class ContactEmail @JvmOverloads constructor(
    internal val address: String,
    internal val type: Int = Email.TYPE_MOBILE,
    private val label: String? = null,
) {
    internal fun label(): String? {
        if (Email.TYPE_CUSTOM == type && null == label)
            throw IllegalArgumentException("Error value of label.")
        return label
    }
}

/**
 * 创建 [ContactsScope] 对象用来添加通讯录对象。
 */
@RequiresPermission(value = Manifest.permission.WRITE_CONTACTS)
inline fun ContentResolver.buildContactsScope(
    contactsScope: ContactsScope.() -> Unit
): Array<ContentProviderResult> {
    val mContactsScope = ContactsScope()
    return mContactsScope.apply(contactsScope).commit(this)
}

/**
 * 通讯录对象操作域。
 */
class ContactsScope {
    companion object {
        val URI: Uri = RawContacts.CONTENT_URI
        val DATA_URI: Uri = ContactsContract.Data.CONTENT_URI
    }

    private lateinit var ops: ArrayList<ContentProviderOperation>

    /**
     * 使用事务添加原始联系人信息，点击 [重要的原始联系人列](https://developer.android.com/guide/topics/providers/contacts-provider?hl=zh-cn#RawContactsColumns)
     * 获取更多信息。
     *
     * 该方法必须在最前面调用。
     */
    @JvmOverloads
    fun rawContacts(account: RawAccount? = null) {
        ops = ArrayList()
        ContentProviderOperation.newInsert(URI)
            .withValue(RawContacts.ACCOUNT_NAME, account?.name)
            .withValue(RawContacts.ACCOUNT_TYPE, account?.type)
            .build()
            .apply { ops.add(this) }
    }

    /**
     * 使用事务添加联系人的姓名信息。
     *
     * @param name 姓名信息对象。
     */
    fun name(name: ContactName) {
        ContentProviderOperation.newInsert(DATA_URI)
            .withValueBackReference(ContactsContract.Contacts.Entity.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
            .withValue(StructuredName.DISPLAY_NAME, name.displayName)
            .withValue(StructuredName.GIVEN_NAME, name.givenName)
            .withValue(StructuredName.FAMILY_NAME, name.familyName)
            .withValue(StructuredName.PREFIX, name.prefix)
            .withValue(StructuredName.MIDDLE_NAME, name.middleName)
            .withValue(StructuredName.SUFFIX, name.suffix)
            .withValue(StructuredName.PHONETIC_GIVEN_NAME, name.phoneticGivenName)
            .withValue(StructuredName.PHONETIC_MIDDLE_NAME, name.phoneticMiddleName)
            .withValue(StructuredName.PHONETIC_FAMILY_NAME, name.phoneticFamilyName)
            .build()
            .apply { ops.add(this) }
    }

    /**
     * 使用事务添加联系人的电话信息。
     *
     * @param phone 电话信息对象。
     */
    fun phone(phone: ContactPhone) {
        ContentProviderOperation.newInsert(DATA_URI)
            .withValueBackReference(ContactsContract.Contacts.Entity.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
            .withValue(Phone.NUMBER, phone.number)
            .withValue(Phone.TYPE, phone.type)
            .withValue(Phone.LABEL, phone.label())
            .build()
            .apply { ops.add(this) }
    }

    /**
     * 使用事务添加联系人的邮件信息。
     *
     * @param email 邮件信息对象。
     */
    fun email(email: ContactEmail) {
        ContentProviderOperation.newInsert(DATA_URI)
            .withValueBackReference(ContactsContract.Contacts.Entity.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
            .withValue(Email.ADDRESS, email.address)
            .withValue(Email.TYPE, email.type)
            .withValue(Email.LABEL, email.label())
            .build()
            .apply { ops.add(this) }
    }

    /**
     * 提交添加的联系人信息。
     */
    fun commit(contentResolver: ContentResolver): Array<ContentProviderResult> =
        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
}