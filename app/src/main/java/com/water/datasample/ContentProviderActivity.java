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
                    // ??????????????????
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

    // ????????????
    private void writeSms() {
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        ContentValues cvs = new ContentValues();
        cvs.put("address", "123456789");
        cvs.put("type", 1);
        cvs.put("date", System.currentTimeMillis());
        cvs.put("body", "no zuo no die why you try!");
        cr.insert(uri, cvs);

        Toast.makeText(this, "?????????????????????", Toast.LENGTH_LONG).show();
    }

    // ????????????
    private void readSms() {
        try {
            Uri uri = Uri.parse("content://sms/");
            ContentResolver cr = getContentResolver();
            //??????????????????????????????
            Cursor cursor = cr.query(uri, new String[]{"address", "type", "date", "body"},
                    null, null, null);

            StringBuilder sb = new StringBuilder();

            while (cursor.moveToNext()) {
                String address = cursor.getString(0);
                String date = cursor.getString(1);
                String type = cursor.getString(2);
                String body = cursor.getString(3);

                sb.append("\n??????:" + address);
                sb.append("\n??????:" + date);
                sb.append("\n??????:" + type);
                sb.append("\n??????:" + body);
                sb.append("\n================");
            }

            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();

            cursor.close();
        } catch (Exception ex) {
            Log.i("", ex.getMessage());
        }
    }

    // ?????????????????????
    private void readContacts() {
        //?????????raw_contacts?????????????????????id
        ContentResolver cr = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //?????????????????????
        Cursor cursor = cr.query(uri, null, null,
                null, null);

        StringBuilder sb = new StringBuilder();

        while (cursor.moveToNext()) {
            //?????????????????????,????????????
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String numuber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            sb.append("\n??????:" + name);
            sb.append("\n??????:" + numuber);
            sb.append("\n================");
        }

        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();

        cursor.close();
    }

    // ????????????????????????????????????
    private void queryContact(String number) {
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + number);
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(uri, new String[]{"display_name"}, null, null, null);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            System.out.println(number + "???????????????????????????" + name);
        }
        cursor.close();
    }

    // ???????????????????????????
    private void writeContacts() throws RemoteException, OperationApplicationException {
        //???????????????????????????
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        ContentResolver cr = getContentResolver();
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation one = ContentProviderOperation.newInsert(uri)
                .withValue("account_name", null)
                .build();
        operations.add(one);

        //?????????????????????????????????
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

        // ??????????????????????????????????????????
        cr.applyBatch("com.android.contacts", operations);
        Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
    }
}