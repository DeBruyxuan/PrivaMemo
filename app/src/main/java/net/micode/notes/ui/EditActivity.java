package net.micode.notes.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import net.micode.notes.R;
import net.micode.notes.ui.bean.Note;
import net.micode.notes.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class EditActivity extends Activity {

    private Note note;
    private EditText etContent;

    private NoteDbOpenHelper mNoteDbOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        etContent = findViewById(R.id.sf_et_2);

        initData();



    }

    private void initData() {
        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("note");
        if(note!=null){
            etContent.setText(note.getContent());
        }

        mNoteDbOpenHelper = new NoteDbOpenHelper(this);

    }

    public void save(View view) {
        String content = etContent.getText().toString();

        note.setContent(content);
        note.setCreatedTime(getCurrentTimeFormat());

        long row = mNoteDbOpenHelper.updateData(note);

        if(row != -1 && row != 0){
            ToastUtil.toastShort(this,"保存成功");
            this.finish();
        }else{
            ToastUtil.toastShort(this,"保存失败");
        }
    }
    private String getCurrentTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd HH:mm:ss");

        TimeZone time = TimeZone.getTimeZone("Etc/GMT-8");  //转换为中国时区
        TimeZone.setDefault(time);
        Date date = new Date();

        return simpleDateFormat.format(date);
    }
}