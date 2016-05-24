package com.sametaylak.guncelkartlar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME   = "guncelDB";
    private static final String TABLE_QUESTS = "questions";
    private static final String TABLE_FAVS = "favs";
    private static final String TABLE_IDK = "idk";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_QUESTS + "(id INTEGER PRIMARY KEY,tempId INTEGER,sender TEXT,quest TEXT" + ")";
        String sql2 = "CREATE TABLE " + TABLE_FAVS + "(id INTEGER PRIMARY KEY,favId INTEGER" + ")";
        String sql3 = "CREATE TABLE " + TABLE_IDK + "(id INTEGER PRIMARY KEY,idkId INTEGER" + ")";
        db.execSQL(sql);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IDK);
        onCreate(db);
    }

    public void deleteAllQuests() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_QUESTS);
    }

    public void insertQuest(Quest q) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("tempId", q.getId());
        values.put("sender", q.getSender());
        values.put("quest", q.getQuest());

        db.insert(TABLE_QUESTS, null, values);
        db.close();
    }

    public void insertFav(Quest q) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("favId", q.getId());

        db.insert(TABLE_FAVS, null, values);
        db.close();
    }

    public void insertIdk(Quest q) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_IDK + " WHERE idkId=" + q.getId(), null);
        if (!cursor.moveToNext()){
            ContentValues values = new ContentValues();
            values.put("idkId", q.getId());

            db.insert(TABLE_IDK, null, values);
        }

        db.close();
    }

    public void deleteFav(Quest q) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_FAVS + " WHERE favId=" + q.getId());

    }

    public void deleteIdk(Quest q) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_IDK + " WHERE idkId=" + q.getId());

    }

    public boolean searchQuest(Quest q) {
        boolean isAvailable = false;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVS + " WHERE favId=" + q.getId(), null);
        if (cursor.moveToNext()){
            isAvailable = true;
        }

        return isAvailable;
    }

    public List<Quest> getAllQuests() {
        List<Quest> questions = new ArrayList<Quest>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_QUESTS, new String[]{"tempId", "sender", "quest"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Quest country = new Quest(cursor.getInt(0), cursor.getString(1), cursor.getString(2), 0);
            questions.add(country);
        }

        long seed = System.nanoTime();
        Collections.shuffle(questions, new Random(seed));

        return questions;
    }

    public List<Quest> getAllFavs() {
        List<Quest> questions = new ArrayList<Quest>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_QUESTS, new String[]{"tempId", "sender", "quest"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Cursor cursor2 = db.rawQuery("SELECT * FROM " + TABLE_FAVS + " WHERE favId=" + cursor.getInt(0), null);
            if(cursor2.moveToNext()){
                questions.add(
                        new Quest(cursor.getInt(0), cursor.getString(1), cursor.getString(2), 0)
                );
            }
        }

        return questions;
    }

    public List<Quest> getAllIdk() {
        List<Quest> questions = new ArrayList<Quest>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_QUESTS, new String[]{"tempId", "sender", "quest"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Cursor cursor2 = db.rawQuery("SELECT * FROM " + TABLE_IDK + " WHERE idkID=" + cursor.getInt(0), null);
            if(cursor2.moveToNext()){
                questions.add(
                        new Quest(cursor.getInt(0), cursor.getString(1), cursor.getString(2), 0)
                );
            }
        }

        return questions;
    }
}
