package rx.test;

import android.database.Cursor;

/**
 * Created by agoyal3 on 12/17/15.
 */
public class ModelGovMember {
    private String name;
    private String twitterId;

    public ModelGovMember(String name, String twitterId) {
        this.name = name;
        this.twitterId = twitterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public static ModelGovMember fromCursor(Cursor c) {
        String name = c.getString(c.getColumnIndexOrThrow(SQLiteGovHelper.COLUMN_NAME));
        String twitterid = c.getString(c.getColumnIndexOrThrow(SQLiteGovHelper.COLUMN_TWITTERID));
        return new ModelGovMember(name, twitterid);
    }
}
