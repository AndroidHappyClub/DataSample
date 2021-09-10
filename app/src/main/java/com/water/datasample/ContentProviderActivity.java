package com.water.datasample;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.water.utilities.PermissionHelper;

import java.util.ArrayList;

public class ContentProviderActivity extends AppCompatActivity
        implements View.OnClickListener {
    private Button btnReadSms;
    private Button btnWriteSms;
    private Button btnReadContacts;
    private Button btnWriteContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider);

        bindViews();
    }

    private void bindViews() {
        btnReadSms = findViewById(R.id.btnReadSms);
        btnWriteSms = findViewById(R.id.btnWriteSms);
        btnReadContacts = findViewById(R.id.btnReadContacts);
        btnWriteContacts = findViewById(R.id.btnWriteContacts);

        btnReadSms.setOnClickListener(this);
        btnWriteSms.setOnClickListener(this);
        btnReadContacts.setOnClickListener(this);
        btnWriteContacts.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnWriteSms:
                writeSms();
                break;
            case R.id.btnReadSms:
                PermissionHelper.checkPermission(this,
                        Manifest.permission.READ_SMS);
                readSms();
                break;
            case R.id.btnWriteContacts:
                try {
                    // 必须申请权限
                    PermissionHelper.checkPermission(this,
                            Manifest.permission.WRITE_CONTACTS);
                    writeContacts();
                } catch (Exception ex)
                {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnReadContacts:
                PermissionHelper.checkPermission(this,
                        Manifest.permission.READ_CONTACTS);
                readContacts();
                break;
        }
    }

    // 插入短信
    private void writeSms() {
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        ContentValues cvs = new ContentValues();
        cvs.put("address", "123456789");
        cvs.put("type", 1);
        cvs.put("date", System.currentTimeMillis());
        cvs.put("body", "no zuo no die why you try!");
        cr.insert(uri, cvs);

        Toast.makeText(this, "短信插入完毕！", Toast.LENGTH_LONG).show();
    }

    // 读取短信
    private void readSms() {
        try {
            Uri uri = Uri.parse("content://sms/");
            ContentResolver cr = getContentResolver();
            //获取的是哪些列的信息
            Cursor cursor = cr.query(uri, new String[]{"address", "type", "date", "body"},
                    null, null, null);

            StringBuilder sb = new StringBuilder();

            while (cursor.moveToNext()) {
                String address = cursor.getString(0);
                String date = cursor.getString(1);
                String type = cursor.getString(2);
                String body = cursor.getString(3);

                sb.append("\n地址:" + address);
                sb.append("\n时间:" + date);
                sb.append("\n类型:" + type);
                sb.append("\n内容:" + body);
                sb.append("\n================");
            }

            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();

            cursor.close();
        } catch (Exception ex) {
            Log.i("", ex.getMessage());
        }
    }

    // 读取手机联系人
    private void readContacts() {
        //①查询raw_contacts表获得联系人的id
        ContentResolver cr = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //查询联系人数据
        Cursor cursor = cr.query(uri, null, null,
                null, null);

        StringBuilder sb = new StringBuilder();

        while (cursor.moveToNext()) {
            //获取联系人姓名,手机号码
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String numuber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            sb.append("\n姓名:" + name);
            sb.append("\n号码:" + numuber);
            sb.append("\n================");
        }

        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();

        cursor.close();
    }

    // 查询指定电话的联系人信息
    private void queryContact(String number) {
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + number);
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(uri, new String[]{"display_name"}, null, null, null);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            System.out.println(number + "对应的联系人名称：" + name);
        }
        cursor.close();
    }

    // 添加一个新的联系人
    private void writeContacts() throws RemoteException, OperationApplicationException {
        //使用事务添加联系人
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        ContentResolver cr = getContentResolver();
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation one = ContentProviderOperation.newInsert(uri)
                .withValue("account_name", null)
                .build();
        operations.add(one);

        //依次是姓名，号码，邮编
        ContentProviderOperation two = ContentProviderOperation.newInsert(dataUri)
                .withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", "vnd.android.cursor.item/name")
                .withValue("data2", "android")
                .build();
        operations.add(two);

        ContentProviderOperation three = ContentProviderOperation.newInsert(dataUri)
                .withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", "vnd.android.cursor.item/phone_v2")
                .withValue("data1", "13888888888")
                .withValue("data2", "2")
                .build();
        operations.add(three);

        ContentProviderOperation four = ContentProviderOperation.newInsert(dataUri)
                .withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", "vnd.android.cursor.item/email_v2")
                .withValue("data1", "1000@qq.com")
                .withValue("data2", "2")
                .build();
        operations.add(four);

        // 将上述内容添加到手机联系人中
        cr.applyBatch("com.android.contacts", operations);
        Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
    }
}