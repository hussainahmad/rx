package rx.test;

import android.database.Cursor;
import android.support.v4.util.ArrayMap;

/**
 * Created by agoyal3 on 12/13/15.
 */
// https://medium.com/android-news/using-a-cache-to-optimize-data-retrieval-from-cursors-56f9eaa1e0d2#.uf7r9e977
public class ColumnIndexCache {
    private ArrayMap<String, Integer> mMap = new ArrayMap<>();

    public int getColumnIndex(Cursor cursor, String columnName) {
        if (!mMap.containsKey(columnName))
            mMap.put(columnName, cursor.getColumnIndex(columnName));
        return mMap.get(columnName);
    }

    public void clear() {
        mMap.clear();
    }
}
