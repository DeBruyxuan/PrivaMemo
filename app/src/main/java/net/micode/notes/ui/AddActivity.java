package net.micode.notes.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import net.micode.notes.R;
import net.micode.notes.ui.bean.Note;
import net.micode.notes.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends Activity {

    private EditText etContent;

    private NoteDbOpenHelper mNoteDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etContent = findViewById(R.id.sf_et_1);

        mNoteDbOpenHelper = new NoteDbOpenHelper(this);

    }

    public void add(View view) {
        String content = etContent.getText().toString();

            Note note = new Note();
            note.setContent(content);
            note.setCreatedTime(getCurrentTimeFormat());

            long row = mNoteDbOpenHelper.insertData(note);
            if(row != -1){
                ToastUtil.toastShort(this,"添加成功");
                this.finish();
            }else{
                ToastUtil.toastShort(this,"添加失败");
            }


    }

    private String getCurrentTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }


}