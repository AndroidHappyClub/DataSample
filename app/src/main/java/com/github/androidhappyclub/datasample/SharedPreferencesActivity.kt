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

package com.github.androidhappyclub.datasample

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.ave.vastgui.tools.view.toast.SimpleToast
import com.ave.vastgui.tools.viewbinding.viewBinding
import com.github.androidhappyclub.datasample.databinding.ActivitySharedPreferencesBinding
import com.github.androidhappyclub.datasample.helper.SharedPreferencesHelper
import com.github.androidhappyclub.datasample.log.mLogFactory

class SharedPreferencesActivity : AppCompatActivity(R.layout.activity_shared_preferences) {

    private val binding by viewBinding(ActivitySharedPreferencesBinding::bind)
    private val logger = mLogFactory.getLog(SharedPreferencesActivity::class.java)
    private val etName: EditText
        get() = binding.etAccount
    private val etPassword: EditText
        get() = binding.etPassword
    private val chkSavePwd: CheckBox
        get() = binding.chkSavePwd
    private val btnLogin: Button
        get() = binding.btnLogin

    private lateinit var _name: String
    private lateinit var _password: String
    private lateinit var _sph: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _sph = SharedPreferencesHelper("SharedPreferencesActivity")
        // _sph.clearAllKV()
        bindViews()
    }

    private fun bindViews() {
        btnLogin.setOnClickListener {
            _name = etName.text.toString()
            _password = etPassword.text.toString()

            _sph.isChecked = binding.chkSavePwd.isChecked
            if (_name.isNotEmpty()) {
                _sph.username = _name

                if (_sph.isChecked)
                    _sph.password = _password
                else
                    _sph.password = ""

                SimpleToast.showShortMsg("用户和密码保存成功")
            } else {
                SimpleToast.showShortMsg("用户名不能为空！")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        etName.setText(_sph.username)
        etPassword.setText(_sph.password)
        chkSavePwd.isChecked = _sph.isChecked
    }
}