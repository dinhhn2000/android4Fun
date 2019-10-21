package com.example.notedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class DataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "note_list";
    //    NOTE TABLE
    private static final String NOTE_TABLE = "note";
    private static final String ID = "id"; // Use Timestamp
    private static final String HEADER = "name";
    private static final String CONTENT = "email";
    //    TAG TABLE
    private static final String TAG_TABLE = "tag";
    private static final String TAG_CONTENT = "tag_content";

    private Context context;

    DataBase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        Log.d("Database", "Database: ");
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
//        Create NOTE_TABLE
        String sqlQuery = "CREATE TABLE " + NOTE_TABLE + " (" +
                ID + " long primary key, " +
                HEADER + " TEXT, " +
                CONTENT + " TEXT)";
        db.execSQL(sqlQuery);
        Toast.makeText(context, "Create note table successfylly", Toast.LENGTH_SHORT).show();
//        Create TAG_TABLE
        sqlQuery = "CREATE TABLE " + TAG_TABLE + " (" +
                ID + " long, " +
                TAG_CONTENT + " TEXT)";
        db.execSQL(sqlQuery);
        Toast.makeText(context, "Create tag table successfylly", Toast.LENGTH_SHORT).show();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TAG_TABLE);
        onCreate(db);
        Toast.makeText(context, "Drop successfylly", Toast.LENGTH_SHORT).show();
    }

//    Get all notes

    ArrayList<Note> getAllNote() {
        ArrayList<Note> listNote = new ArrayList<Note>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + NOTE_TABLE + " ORDER BY " + ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Note data = getNoteById(cursor.getLong(0));
                listNote.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listNote;
    }

//    Add new a note

    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues note_values = new ContentValues();
        note_values.put(ID, note.getEditTime().getTime());
        note_values.put(HEADER, note.getHeader());
        note_values.put(CONTENT, note.getContent());

        db.insert(NOTE_TABLE, null, note_values);

        ArrayList<String> tagArr = note.getTags();
        ContentValues tag_values = new ContentValues();

        for (int i = 0; i < tagArr.size(); i++) {
            tag_values.put(ID, note.getEditTime().getTime());
            tag_values.put(TAG_CONTENT, tagArr.get(i));
            db.insert(TAG_TABLE, null, tag_values);
            tag_values.clear();
        }

        db.close();
    }

//     Select tag by ID

    public ArrayList<String> getTagById(long id) {
        ArrayList<String> tags = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TAG_TABLE + " WHERE " + ID + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(1);
                tags.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tags;
    }

//     Select a note by ID

    public Note getNoteById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(NOTE_TABLE, new String[]{ID,
                        HEADER, CONTENT}, ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ArrayList<String> tagList = getTagById(cursor.getLong(0));
        Date date = new Date();
        date.setTime(cursor.getLong(0));
        Note note = new Note(cursor.getString(1), cursor.getString(2), tagList, date);
        cursor.close();
        db.close();
        return note;
    }

//    Delete note

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOTE_TABLE, ID + " = ?",
                new String[]{String.valueOf(note.getEditTime().getTime())});
        db.close();
    }

//    Update note

    public void update(Note oldNote, Note newNote) {
        deleteNote(oldNote);
        addNote(newNote);
    }

//    Search by headers

    public ArrayList<Note> getNotesByHeaders(ArrayList<String> headers) {
        ArrayList<Note> notes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + NOTE_TABLE + " WHERE " + HEADER + " LIKE '%" + headers.get(0) + "%'";
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 1; i < headers.size(); i++) {
            String subQuery = HEADER + " LIKE '%" + headers.get(i) + "%'";
            selectQuery += " OR " + subQuery;
        }

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Date date = new Date();
                date.setTime(cursor.getLong(0));
                ArrayList<String> tagList = getTagById(cursor.getLong(0));
                Note addNote = new Note(cursor.getString(1), cursor.getString(2), tagList, date);
                notes.add(addNote);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notes;
    }

    //    Search by tags
    public ArrayList<Note> getNotesByTags(ArrayList<String> tags) {
        ArrayList<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

//        Get id array from tag
        ArrayList<Long> id = getIdByTags(tags, db);
        if (id.isEmpty())
            return notes;
        String selectQuery = "SELECT * FROM " + NOTE_TABLE + " WHERE " + ID + " = " + id.get(0);

        for (int i = 1; i < id.size(); i++) {
            String subQuery = ID + " = " + id.get(i);
            selectQuery += " OR " + subQuery;
        }

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Date date = new Date();
                date.setTime(cursor.getLong(0));
                ArrayList<String> tagList = getTagById(cursor.getLong(0));
                Note addNote = new Note(cursor.getString(1), cursor.getString(2), tagList, date);
                notes.add(addNote);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notes;
    }

//    Search id by tags

    public ArrayList<Long> getIdByTags(ArrayList<String> tags, SQLiteDatabase db) {
        ArrayList<Long> id = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TAG_TABLE + " WHERE " + TAG_CONTENT + " LIKE '%" + tags.get(0) + "%'";

        for (int i = 1; i < tags.size(); i++) {
            String subQuery = TAG_CONTENT + " LIKE '%" + tags.get(i) + "%'";
            selectQuery += " OR " + subQuery;
        }

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                id.add(cursor.getLong(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return id;
    }
}
