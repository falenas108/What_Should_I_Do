package com.falenas_apps.whatshouldido.Data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.falenas_apps.whatshouldido.Data.ActionContract.CONTENT_AUTHORITY;
import static com.falenas_apps.whatshouldido.Data.ActionContract.CONTENT_URI;
import static com.falenas_apps.whatshouldido.Data.ActionContract.PATH_ACTIONS;

/**
 * Created by jayfman on 1/8/18.
 */

public class ActionProvider extends ContentProvider{

    //Variable to determine if accessing entire databes or just one row
    private static final int ACTION_TABLE = 100;
    private static final int ACTION_ID = 101;
    //Variable to tell if the URI matches
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    //MIME type for list of actions
    public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACTIONS;
    //MIME type of a single person
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACTIONS;

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY,PATH_ACTIONS,ACTION_TABLE);
        sUriMatcher.addURI(CONTENT_AUTHORITY,PATH_ACTIONS+"/#",ACTION_ID);
    }

    private ActionHelper mActionHelper;
    @Override
    public boolean onCreate() {
        mActionHelper = new ActionHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mActionHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        Cursor cursor;

        switch (match){
            case ACTION_TABLE:
                cursor = db.query(ActionContract.ActionEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ACTION_ID:
                selection = ActionContract.ActionEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ActionContract.ActionEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        getContext().getContentResolver().notifyChange(uri,null);
        switch (match){
            case ACTION_TABLE:
                return CONTENT_LIST_TYPE;
            case ACTION_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: "+uri+" with match "+match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = mActionHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        //UI updates on insert
        getContext().getContentResolver().notifyChange(uri,null);

        switch (match){
            case ACTION_TABLE:
                long idEntered = db.insert(ActionContract.ActionEntry.TABLE_NAME,null,contentValues);
                return ContentUris.withAppendedId(uri,idEntered);
            default:
                throw new IllegalArgumentException("Invalid Uri "+uri);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mActionHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        //UI updates on delete
        getContext().getContentResolver().notifyChange(uri,null);
        long actionId;
        switch (match){
            case ACTION_TABLE:
                actionId = db.delete(ActionContract.ActionEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case ACTION_ID:
                selection = ActionContract.ActionEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                actionId = db.delete(ActionContract.ActionEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri "+uri);
        }

        return (int) actionId;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mActionHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        getContext().getContentResolver().notifyChange(uri,null);
        long actionID;
        switch (match){
            case ACTION_ID:
                selection = ActionContract.ActionEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                actionID = db.update(ActionContract.ActionEntry.TABLE_NAME,contentValues,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri "+uri);

        }
        return (int) actionID;
    }
}
