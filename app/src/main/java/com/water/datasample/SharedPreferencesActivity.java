package com.water.datasample;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.water.datasample.helper.SharedPreferencesHelper;

public class SharedPreferencesActivity extends AppCompatActivity {
    private EditText etName;
    private EditText etPassword;
    private CheckBox chkSavePwd;
    private Button btnLogin;
    private String _name;
    private String _password;
    private SharedPreferencesHelper _sph;
    private Context _ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_preferences);
        _ctx = getApplicationContext();
        _sph = new SharedPreferencesHelper(_ctx,
                "SharedPreferencesActivity");
        // _sph.clear();

        bindViews();
    }

    private void bindViews() {
        etName = (EditText) findViewById(R.id.etAccount);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        chkSavePwd = findViewById(R.id.chkSavePwd);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _name = etName.getText().toString();
                _password = etPassword.getText().toString();

                if (_name.length() > 0) {
                    _sph.save("UserName", _name);

                    if (chkSavePwd.isChecked())
                        _sph.save("Password", _password);
                    else
                        _sph.save("Password", "");

                    _sph.save("IsChecked", chkSavePwd.isChecked());

                    Toast.makeText(_ctx, "用户和密码保存成功！",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(_ctx, "用户名不能为空！",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        etName.setText(_sph.readString("UserName"));
        etPassword.setText(_sph.readString("Password"));
        chkSavePwd.setChecked(_sph.readBoolean("IsChecked"));
    }
}