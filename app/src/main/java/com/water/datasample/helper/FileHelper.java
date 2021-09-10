package com.water.datasample.helper;

import android.content.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {
    private Context _ctx;

    public FileHelper(Context ctx) {
        this._ctx = ctx;
    }

    // 向指定的文件中写入指定的数据
    public void save(String filename, String message)
            throws IOException{
        FileOutputStream fout = _ctx.openFileOutput(filename,
                Context.MODE_PRIVATE + Context.MODE_APPEND);//获得FileOutputStream

        //将要写入的字符串转换为byte数组
        byte[] bytes = message.getBytes();
        fout.write(bytes);//将byte数组写入文件
        fout.close();//关闭文件输出流
    }

    /*
     * 这里定义的是文件读取的方法
     * */
    public String read(String filename) throws IOException {
        //打开文件输入流
        FileInputStream fis = _ctx.openFileInput(filename);
        byte[] temp = new byte[1024];
        StringBuilder sb = new StringBuilder("");
        int len = 0;

        //读取文件内容:
        while ((len = fis.read(temp)) > 0) {
            sb.append(new String(temp, 0, len));
        }

        //关闭输入流
        fis.close();
        return sb.toString();
    }

    public boolean delete(String filename) {
        return  _ctx.deleteFile(filename);
    }

    public String[] fileList() {
        return  _ctx.fileList();
    }
}