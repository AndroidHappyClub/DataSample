package com.water.datasample;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.water.datasample.helper.SQLiteHelper;

public class SqliteActivity extends AppCompatActivity {
    private ListView lvStudents;
    private Button btnAdd;
    private Button btnSearch;
    private SQLiteHelper _sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);

        bindViews();
    }

    private void bindViews() {
        lvStudents = findViewById(R.id.lvStudents);
        btnAdd = findViewById(R.id.btnAdd);
        btnSearch = findViewById(R.id.btnSearch);

        this.setTitle("浏览学生信息");
        _sh = new SQLiteHelper(this);

        //查询数据，获取游标
        fillListView("0,19");

        //提示对话框
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);

        //设置ListView单击监听器
        lvStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final long temp=arg3;

                builder.setMessage("真的要删除该记录吗？").setPositiveButton("是",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                //删除数据
                                _sh.delete("student", (int) temp);

                                fillListView("0,19");
                            }
                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    ContentValues cvs = new ContentValues();
                    cvs.put("name", "Lisi");
                    cvs.put("sex", "男");
                    _sh.insert("student", cvs);

                    long count = _sh.getCount("student");

                    fillListView(String.format("%d,%d",(count - 19) < 0? 0 : count - 19, count));
                }
                catch (Exception ex)
                {
                    Log.i("", ex.getMessage());
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    long count = _sh.getCount("student");

                    fillListView(String.format("%d,%d",(count - 19) < 0? 0 : count - 19, count));
                }
                catch (Exception ex)
                {
                    Log.i("", ex.getMessage());
                }
            }
        });
    }
    private void fillListView(String limit){
        String[] columns = {"id as _id","name","sex"};
        // 查询数据，获取游标
        Cursor cursor=_sh.query("student", columns, limit);

        //列表项数据
        String[] from={"_id","name","sex"};

        // 列表项ID
        int[] to ={R.id.tvID, R.id.tvName, R.id.tvSex};

        try {
            // 适配器
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    getApplicationContext(),
                    R.layout.list_view_item, cursor, from, to,
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

            //为列表视图添加适配器
            lvStudents.setAdapter(adapter);
        }
        catch (Exception ex)
        {
            Log.i("", ex.getMessage());
        }
    }
}