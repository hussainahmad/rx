package rx.test;

import android.database.Cursor;

/**
 * Created by agoyal3 on 12/19/15.
 */
public class Question {

    private String i, q, a1, a2, a3, a4, a5;

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getA1() {
        return a1;
    }

    public void setA1(String a1) {
        this.a1 = a1;
    }

    public String getA2() {
        return a2;
    }

    public void setA2(String a2) {
        this.a2 = a2;
    }

    public String getA3() {
        return a3;
    }

    public void setA3(String a3) {
        this.a3 = a3;
    }

    public String getA4() {
        return a4;
    }

    public void setA4(String a4) {
        this.a4 = a4;
    }

    public String getA5() {
        return a5;
    }

    public void setA5(String a5) {
        this.a5 = a5;
    }

    @Override
    public String toString() {
        return "[" + i + "] " + q + " (" + a1 + ", " + a2 + ", " + a3 + ", " + a5 + ", " + a5 + ")";
    }

    public static Question fromCursor(Cursor c) {
        Question q = new Question();
        q.setI( c.getString(c.getColumnIndexOrThrow(SQLiteGovHelper.COL_I)));
        q.setQ( c.getString(c.getColumnIndexOrThrow(SQLiteGovHelper.COL_Q)));
        q.setA1( c.getString(c.getColumnIndexOrThrow(SQLiteGovHelper.COL_A1)));
        q.setA2( c.getString(c.getColumnIndexOrThrow(SQLiteGovHelper.COL_A2)));
        q.setA3( c.getString(c.getColumnIndexOrThrow(SQLiteGovHelper.COL_A3)));
        q.setA4( c.getString(c.getColumnIndexOrThrow(SQLiteGovHelper.COL_A4)));
        q.setA5( c.getString(c.getColumnIndexOrThrow(SQLiteGovHelper.COL_A5)));
        return q;
    }

}
