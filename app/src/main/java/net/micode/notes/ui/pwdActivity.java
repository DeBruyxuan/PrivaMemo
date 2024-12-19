package net.micode.notes.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.micode.notes.R;
import net.micode.notes.ui.adapter.MyAdapter;
import net.micode.notes.ui.bean.Note;
import net.micode.notes.util.ToastUtil;
import net.micode.notes.ui.SimpleDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class pwdActivity extends NotesListActivity {

    private SimpleDbHelper dbHelper;

    public Button mBtnpwd;
    public EditText mEtpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);
        dbHelper = new SimpleDbHelper(this);
        
        mBtnpwd = findViewById(R.id.btn_pwd);
        mEtpwd = findViewById(R.id.et_1);


        mBtnpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd = mEtpwd.getText().toString();
                Intent intent = null;
                String secret_pwd = dbHelper.hashPasswordWithSHA256(pwd);
                int userId = 1;

                Cursor cursor = dbHelper.getUser(userId);

                if (cursor != null && cursor.moveToFirst()) { // 确保Cursor不为空且能移动到第一行
                    String password = cursor.getString(cursor.getColumnIndexOrThrow("pwd"));
                    if (password.equals(secret_pwd)) {
                        intent = new Intent(pwdActivity.this, SafeFolderActivity.class);
                        startActivity(intent);
                    } else {
                        ToastUtil.toastShort(pwdActivity.this, "密码错误");
                    }
                    cursor.close(); // 使用完后关闭Cursor
                } else {
                    ToastUtil.toastShort(pwdActivity.this, "用户不存在或查询错误");
                }
            }
        });


    }





}