package com.example.todolistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todolistapp.ModelClasses.TasksModel;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ToDoListDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TASKS = "tasks";
    private static final String TASKS_ID = "id";
    private static final String TASKS_TITLE = "title";
    private static final String TASKS_CATEGORY = "category";
    private static final String TASKS_STATUS = "status";
    private static final String TABLE_CATEGORY = "category";
    private static final String CATEGORY_ID = "id";
    private static final String CATEGORY_NAME = "name";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TASKS + "("
                + TASKS_ID + " INTEGER PRIMARY KEY,"
                + TASKS_TITLE + " TEXT,"
                + TASKS_CATEGORY + " TEXT,"
                + TASKS_STATUS + " TEXT"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_CATEGORY + "("
                + CATEGORY_ID + " INTEGER PRIMARY KEY,"
                + CATEGORY_NAME + " TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(db);
    }

    public void addTask(TasksModel tasksModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASKS_TITLE, tasksModel.getTitle());
        values.put(TASKS_CATEGORY, tasksModel.getCategory());
        values.put(TASKS_STATUS, tasksModel.getStatus());

        db.insert(TABLE_TASKS, null, values);
    }

    public ArrayList<TasksModel> fetchTasks(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        if (category.equals("All")) {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);
        } else {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE category = ?", new String[]{category});
        }

        ArrayList<TasksModel> arrTasks = new ArrayList<>();

        while (cursor.moveToNext()) {
            TasksModel model = new TasksModel();
            model.setId(cursor.getInt(0));
            model.setTitle(cursor.getString(1));
            model.setCategory(cursor.getString(2));
            model.setStatus(cursor.getString(3));
            arrTasks.add(model);
        }
        return arrTasks;
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_TASKS, TASKS_ID + " = ? ", new String[]{String.valueOf(id)});
    }

    public void editTask(int id, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASKS_TITLE, title);

        db.update(TABLE_TASKS, contentValues, TASKS_ID + " = ?", new String[]{String.valueOf(id)});

    }

    public void updateTaskStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASKS_STATUS, status);

        db.update(TABLE_TASKS, contentValues, TASKS_ID + " = ?", new String[]{String.valueOf(id)});

    }

    public String getTaskStatus(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TASKS_STATUS + " FROM " + TABLE_TASKS + " WHERE " + TASKS_ID + " = '" + id + "'", null);
        cursor.moveToNext();
        String status = cursor.getString(cursor.getColumnIndexOrThrow(TASKS_STATUS));

        return status;
    }

    public void addCategory(String category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CATEGORY_NAME, category);

        db.insert(TABLE_CATEGORY, null, values);
    }

    public ArrayList<String> fetchCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + CATEGORY_NAME + " FROM " + TABLE_CATEGORY, null);

        ArrayList<String> arrCategories = new ArrayList<>();
        arrCategories.add("All");
        while (cursor.moveToNext()) {
            arrCategories.add(cursor.getString(cursor.getColumnIndexOrThrow(CATEGORY_NAME)));
        }

        return arrCategories;
    }

}
