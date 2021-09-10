package com.water.datasample;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.water.datasample.helper.FileHelper;

import java.io.IOException;

public class FileActivity extends AppCompatActivity
        implements View.OnClickListener{
    private EditText etFileName;
    private EditText etContent;
    private Button btnSave;
    private Button btnClean;
    private Button btnRead;
    private Button btnDelete;
    private Button btnFileList;
    private Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        bindViews();
    }


    private void bindViews() {
        _context = getApplicationContext();

        etContent = (EditText) findViewById(R.id.etContent);
        etFileName = (EditText) findViewById(R.id.etFileName);
        btnClean = (Button) findViewById(R.id.btnClean);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnRead = (Button) findViewById(R.id.btnRead);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnFileList = (Button) findViewById(R.id.btnFileList);

        btnClean.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnFileList.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClean:
                etContent.setText("");
                etFileName.setText("");
                break;
            case R.id.btnSave:
                FileHelper fhsave = new FileHelper(_context);
                String savename = etFileName.getText().toString();
                String savecontent = etContent.getText().toString();

                try {
                    fhsave.save(savename, savecontent);
                    Toast.makeText(_context, "数据写入成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(_context, "数据写入失败", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnRead:
                String readcontent = "";
                FileHelper fhread = new FileHelper(_context);

                try {
                    String readname = etFileName.getText().toString();
                    readcontent = fhread.read(readname);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(_context, readcontent == ""?"文件读取失败！":
                        readcontent, Toast.LENGTH_SHORT).show();

                break;
            case R.id.btnDelete:
                FileHelper fhdelete = new FileHelper(_context);
                boolean deletecontent = false;
                try {
                    String deletename = etFileName.getText().toString();
                    deletecontent = fhdelete.delete(deletename);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(_context, deletecontent?"删除文件成功！":
                        "删除文件失败！", Toast.LENGTH_SHORT).show();

                break;
            case  R.id.btnFileList:
                FileHelper fhlist = new FileHelper(_context);
                String filename = "";

                try {
                    String[] fs = fhlist.fileList();

                    for (String file:fs) {
                        filename += file + "\n";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(_context, filename == ""?"没有找到文件！":
                        filename, Toast.LENGTH_SHORT).show();

                break;
        }
    }
}
