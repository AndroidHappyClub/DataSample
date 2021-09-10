package com.water.datasample.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SdFileHelper {
    private Context _ctx;

    public SdFileHelper(Context ctx) {
        _ctx = ctx;
    }

    // 往SD卡写入文件
    public void sava(String filename, String content)
            throws Exception {
        // 如果手机已插入sd卡,且app具有读写sd卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            File file = new File(path, filename);

            if(!file.exists())
                file.createNewFile();

            // 不要用openFileOutput了,那个是往手机内存中写数据的
            FileOutputStream output = new FileOutputStream(file, true);
            // 将String字符串以字节流的形式写入到输出流中
            output.write(content.getBytes());
            //关闭输出流
            output.close();
        }
        else
            Toast.makeText(_ctx, "SD卡不存在或者不可读写", Toast.LENGTH_SHORT).show();
    }

    // 读取SD卡中文件
    public String read(String filename) throws IOException {
        StringBuilder sb = new StringBuilder("");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filename = Environment.getExternalStorageDirectory().getCanonicalPath() +
                    File.separator + filename;
            //打开文件输入流
            FileInputStream input = new FileInputStream(filename);
            byte[] temp = new byte[1024];

            int len = 0;
            //读取文件内容:
            while ((len = input.read(temp)) > 0) {
                sb.append(new String(temp, 0, len));
            }
            //关闭输入流
            input.close();
        }
        return sb.toString();
    }

    public boolean delete(String filename) {
        return  _ctx.deleteFile(filename);
    }

    public String[] fileList() {
        return  _ctx.fileList();
    }
}