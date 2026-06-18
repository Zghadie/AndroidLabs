package com.example.androidlabs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TodoDatabase";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "todos";

    public static final String COL_ID = "_id";
    public static final String COL_TEXT = "todo_text";
    public static final String COL_URGENT = "urgent";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TEXT + " TEXT NOT NULL, "
                + COL_URGENT + " INTEGER NOT NULL)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertTodo(String text, boolean urgent) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TEXT, text);
        values.put(COL_URGENT, urgent ? 1 : 0);

        return db.insert(TABLE_NAME, null, values);
    }

    public Cursor getAllTodos() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                COL_ID + " ASC"
        );
    }

    public int deleteTodo(long id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_NAME,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }
}