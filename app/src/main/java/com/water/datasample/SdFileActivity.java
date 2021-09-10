package com.water.datasample;

import android.Manifest;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.water.datasample.helper.SdFileHelper;
import com.water.utilities.PermissionHelper;

import java.io.IOException;

public class SdFileActivity extends AppCompatActivity
        implements View.OnClickListener{
    private EditText etSdFileName;
    private EditText etSdContent;
    private Button btnSdSave;
    private Button btnSdClean;
    private Button btnSdRead;
    private Context _ctx;

    private String[] _permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sd_file);
        bindViews();
    }

    private void bindViews() {
        _ctx = SdFileActivity.this;

        etSdFileName = (EditText) findViewById(R.id.etSdFileName);
        etSdContent = (EditText) findViewById(R.id.etSdContent);
        btnSdSave = (Button) findViewById(R.id.btnSdSave);
        btnSdClean = (Button) findViewById(R.id.btnSdClean);
        btnSdRead = (Button) findViewById(R.id.btnSdRead);

        btnSdSave.setOnClickListener(this);
        btnSdClean.setOnClickListener(this);
        btnSdRead.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSdClean:
                etSdContent.setText("");
                etSdFileName.setText("");
                break;
            case R.id.btnSdSave:
                String sfn = etSdFileName.getText().toString();
                String scontent = etSdContent.getText().toString();
                SdFileHelper sfhSave = new SdFileHelper(this);
                PermissionHelper.checkPermission(_ctx, _permission);

                try
                {
                    sfhSave.sava(sfn, scontent);
                    Toast.makeText(_ctx, "数据写入成功", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(_ctx, "数据写入失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnSdRead:
                String rcontent = "";
                SdFileHelper sfhread = new SdFileHelper(this);
                PermissionHelper.checkPermission(_ctx, _permission);

                try
                {
                    String rfn = etSdFileName.getText().toString();
                    rcontent = sfhread.read(rfn);
                }
                catch(IOException e){
                    e.printStackTrace();
                }

                Toast.makeText(_ctx, rcontent, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
