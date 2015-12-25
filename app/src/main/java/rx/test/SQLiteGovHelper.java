package rx.test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by agoyal3 on 12/13/15.
 */
public class SQLiteGovHelper extends SQLiteOpenHelper {

    public static final String TABLE_GOV_MEMBERS = "t_gov";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TWITTERID = "twitter_id";
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_GOV_MEMBERS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TWITTERID + " text not null, "
            + COLUMN_NAME + " text not null);";

    public static final String TABLE_QUESTION = "t_q";
    public static final String COL_ID = "_id";
    public static final String COL_I = "i";
    public static final String COL_Q = "q";
    public static final String COL_A1 = "a1";
    public static final String COL_A2 = "a2";
    public static final String COL_A3 = "a3";
    public static final String COL_A4 = "a4";
    public static final String COL_A5 = "a5";
    // Database creation sql statement
    private static final String DATABASE_CREATE_2 = "create table "
            + TABLE_QUESTION + "("
            + COL_ID + " integer primary key autoincrement, "
            + COL_I  + " integer not null, "
            + COL_Q  + " text not null, "
            + COL_A1 + " text not null, "
            + COL_A2 + " text not null, "
            + COL_A3 + " text not null, "
            + COL_A4 + " text not null, "
            + COL_A5 + " text not null);";

    private static final String DATABASE_NAME = "gov.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteGovHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteGovHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOV_MEMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        onCreate(db);
    }

}

