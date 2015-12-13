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

    private static final String DATABASE_NAME = "gov.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_GOV_MEMBERS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TWITTERID + " text not null, "
            + COLUMN_NAME + " text not null);";

    public SQLiteGovHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteGovHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOV_MEMBERS);
        onCreate(db);
    }

}

