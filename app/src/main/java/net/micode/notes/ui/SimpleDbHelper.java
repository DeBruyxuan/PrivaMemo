package net.micode.notes.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SimpleDbHelper extends SQLiteOpenHelper {
    private Context context; // 添加一个成员变量来存储上下文

    private static final String DATABASE_NAME = "note.db";
    private static final int DATABASE_VERSION = 4;

    public SimpleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context; // 保存传入的上下文引用
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库表和初始数据的SQL语句
        // 例如: db.execSQL("CREATE TABLE user_table (id INTEGER PRIMARY KEY, pwd TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 在这里处理数据库的版本更新
    }

    // 使用SHA-256散列密码的方法
    public String hashPasswordWithSHA256(String passwordToHash) {
        String generatedPassword = null;
        try {
            // 创建 MessageDigest 实例用于SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // 计算哈希值
            byte[] bytes = md.digest(passwordToHash.getBytes());
            // 将字节转换为十六进制格式
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            // 获得SHA-256散列的密码
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    // 更新密码的方法
    // 更新密码的方法
    public void updatePassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 使用 SHA-256 散列新密码
        String hashedPassword = hashPasswordWithSHA256(newPassword);

        // 创建 ContentValues 对象来存放待更新的数据
        ContentValues values = new ContentValues();
        values.put("pwd", hashedPassword); // pwd 是您数据库中存放密码的列名

        // 定义我们想要更新的记录的ID
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(userId)}; // 使用传入的userId

        // 更新数据库中的密码
        int count = db.update(
                "pwd",   // 表名
                values,         // 新的值
                selection,      // 更新的记录的选择条件
                selectionArgs); // 选择条件的参数

        if (count > 0) {
            // 更新成功
            Toast.makeText(context, "修改成功！", Toast.LENGTH_SHORT).show();
        } else {
            // 更新失败
            Toast.makeText(context, "修改失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor getUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // 定义要返回的列
        String[] projection = {
                "id",   // 用户ID列
                "pwd"   // 用户密码列
        };

        // 定义筛选条件
        String selection = "id = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        // 执行查询
        Cursor cursor = db.query(
                "pwd",   // 表名
                projection,     // 返回的列
                selection,      // 列的筛选条件
                selectionArgs,  // 筛选条件的参数
                null,           // groupBy
                null,           // having
                null            // orderBy
        );

        return cursor;
    }

}
