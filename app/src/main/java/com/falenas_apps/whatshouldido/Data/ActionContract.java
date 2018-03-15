package com.falenas_apps.whatshouldido.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jayfman on 1/8/18.
 */

public final class ActionContract {

    //Strings that other classes will call
    public static final String CONTENT_AUTHORITY = "com.falenas_apps.whatshouldido";
    public static final Uri BASE_CONTENT_AUTHORITY = Uri.parse("content://" +CONTENT_AUTHORITY);
    public static final String PATH_ACTIONS = "actions";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_AUTHORITY,PATH_ACTIONS);

    public static class ActionEntry implements BaseColumns{

        public static final String TABLE_NAME = "actions";
        public static final String _ID = BaseColumns._ID;
        public static final String ACTION = "action";
        public static final String WEIGHT = "weight";
    }

}
