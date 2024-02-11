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
import androidx.appcompat.app.AppCompatActivity
import com.ave.vastgui.tools.view.toast.SimpleToast
import com.ave.vastgui.tools.viewbinding.viewBinding
import com.github.androidhappyclub.datasample.databinding.ActivitySdFileBinding
import com.github.androidhappyclub.datasample.helper.SdFileHelper
import java.io.IOException

class SdFileActivity : AppCompatActivity(R.layout.activity_sd_file) {

    private val binding by viewBinding(ActivitySdFileBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnSdClean.setOnClickListener {
            binding.etSdContent.setText("")
            binding.etSdFileName.setText("")
        }

        binding.btnSdSave.setOnClickListener {
            val sfn = binding.etSdFileName.text.toString()
            val sContent = binding.etSdContent.text.toString()
            val sfhSave = SdFileHelper()
            if (sfhSave.sava(sfn, sContent).isSuccess) {
                SimpleToast.showShortMsg("数据写入成功")
            } else {
                SimpleToast.showShortMsg("数据写入失败")
            }
        }

        binding.btnSdRead.setOnClickListener {
            var rContent = ""
            val helper = SdFileHelper()
            try {
                val rfn = binding.etSdFileName.text.toString()
                rContent = helper.read(rfn)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            SimpleToast.showShortMsg(rContent)
        }
    }

}