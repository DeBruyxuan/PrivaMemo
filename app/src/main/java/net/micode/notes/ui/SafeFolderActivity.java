package net.micode.notes.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.micode.notes.R;
import net.micode.notes.ui.adapter.MyAdapter;
import net.micode.notes.ui.bean.Note;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SafeFolderActivity extends Activity {

    private RecyclerView mRecyclerView;
    private Button mBtnNew;
    private List<Note> mNotes;

    private MyAdapter mMyAdapter;

    private NoteDbOpenHelper mNoteDbOpenHelper;

    private SimpleDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_folder);

        initView();
        initData();
        initEvent();
        dbHelper = new SimpleDbHelper(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshDataFromDb();

    }

    private void refreshDataFromDb() {
        mNotes = getDataFromDB();
        mMyAdapter.refreshData(mNotes);
    }

    private void initEvent() {
        mMyAdapter = new MyAdapter(this,mNotes);
        mRecyclerView.setAdapter(mMyAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initData() {
        mNotes = new ArrayList<>();
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);

    }

    private List<Note> getDataFromDB() {
        return mNoteDbOpenHelper.queryAllFromDb();
    }

    private String getCurrentTimeFormat(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }


    private void initView(){

        mRecyclerView = findViewById(R.id.sf_list);
    }

    public void NewNotes(View view) {
        Intent intent = new Intent(SafeFolderActivity.this,AddActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == R.id.sf_menu_change_password) {
            // 弹出对话框的代码
            showChangePasswordDialog();
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }
    private void showChangePasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_change_password, null);

        EditText newPasswordInput = view.findViewById(R.id.et_new_password);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改密码");
        builder.setView(view);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = newPasswordInput.getText().toString();

                try {
                    dbHelper.updatePassword(1, newPassword); // 假设我们更新ID为1的用户
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}