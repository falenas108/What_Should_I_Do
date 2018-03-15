package com.falenas_apps.whatshouldido.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jayfman on 1/8/18.
 */

public class ActionHelper extends SQLiteOpenHelper {

    static public final String DATABASE_NAME = "ActionReader.db";
    static private final int VERSION_NUMBER = 1;


    public ActionHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Defines the table
        String SQL_CREATE_TABLE = "CREATE TABLE "+ActionContract.ActionEntry.TABLE_NAME+ "("+
                ActionContract.ActionEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                ActionContract.ActionEntry.ACTION+" STRING NOT NULL, "+
                ActionContract.ActionEntry.WEIGHT+" INTEGER DEFAULT 1);";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (newVersion>oldVersion){
            String SQL_DELETE_ENTRIES = "DROP TABLE "+ ActionContract.ActionEntry.TABLE_NAME;
            sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
            onCreate(sqLiteDatabase);
        }

    }
}
