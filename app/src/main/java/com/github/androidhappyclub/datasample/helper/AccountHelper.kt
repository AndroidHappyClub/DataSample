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

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.ave.vastgui.tools.utils.permission.requestPermission

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/12/15
// Description: 
// Documentation:
// Reference:

class AccountHelper(val componentActivity: ComponentActivity) {

    @RequiresApi(Build.VERSION_CODES.O)
    lateinit var getAccounts: ActivityResultLauncher<Intent>
    var mCallback: (ActivityResult) -> Unit = {}

    /**
     * 通过 [type] 获取该类型下的账户列表，针对 [Build.VERSION_CODES.O] 以上的版本。
     * 该方法同时会检查应用是否拥有 [Manifest.permission.GET_ACCOUNTS] ，如果没有则
     * 会进行申请。
     *
     * @param callback 回调函数，用于获取返回的用户列表。
     */
    inline fun getAccounts(
        type: String = "com.google",
        crossinline callback: (Array<Account>) -> Unit = {}
    ) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mCallback = {
                if (it.resultCode == Activity.RESULT_OK) {
                    callback(AccountManager.get(componentActivity).getAccountsByType(type))
                }
            }
            componentActivity.requestPermission(Manifest.permission.GET_ACCOUNTS) {
                granted = {
                    getAccounts.launch(newChooseAccountIntent(allowableAccountTypes = arrayOf(type)))
                }
            }
        } else {
            componentActivity.requestPermission(Manifest.permission.GET_ACCOUNTS) {
                granted = {
                    callback(AccountManager.get(componentActivity).getAccountsByType(type))
                }
            }
        }
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getAccounts = componentActivity
                .registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    mCallback(it)
                }
        }
    }
}

/**
 * 适用于 [AccountManager.newChooseAccountIntent] 的拓展方法。
 */
fun newChooseAccountIntent(
    selectedAccount: Account? = null,
    allowableAccounts: List<Account>? = null,
    allowableAccountTypes: Array<String>? = null,
    descriptionOverrideText: String? = null,
    addAccountAuthTokenType: String? = null,
    addAccountRequiredFeatures: Array<String>? = null,
    addAccountOptions: Bundle? = null
): Intent? = AccountManager.newChooseAccountIntent(
    selectedAccount, allowableAccounts, allowableAccountTypes,
    descriptionOverrideText, addAccountAuthTokenType, addAccountRequiredFeatures,
    addAccountOptions
)