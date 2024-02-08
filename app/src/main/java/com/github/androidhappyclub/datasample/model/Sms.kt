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

package com.github.androidhappyclub.datasample.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/12/16

/**
 * 获取到的短信对象。
 *
 * @property address 短信发送者。
 * @property date 短信接收日期。
 * @property type 短信类型。
 * @property body 短信内容。
 */
data class Sms(
    val name: String?,
    val address: String?,
    private val date: Long?,
    private val type: Int?,
    val body: String?
) {
    fun date(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        .format(Date(date ?: System.currentTimeMillis()))

    private fun type(): String = type.toString()
}