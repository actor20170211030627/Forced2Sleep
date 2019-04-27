package com.actor.forced2sleep.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kevin.
 *
 * 创建程序锁数据库
 *
 */

public class AppLockOpenHelper extends SQLiteOpenHelper {

    public AppLockOpenHelper(Context context) {
        //Context context, String name, CursorFactory factory, int version
        super(context, "applock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表 保存已加锁app的包名, package,
        String sql = "create table applock(_id integer primary key autoincrement, package " +
                "varchar(50))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
