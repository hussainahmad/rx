package rx.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import rx.Observable;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private final String mTable = SQLiteGovHelper.TABLE_GOV_MEMBERS;
    private BriteDatabase mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "[onCreate]");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // test sqlBrite
        SqlBrite sqlBrite = SqlBrite.create();
        // http://www.vogella.com/tutorials/AndroidSQLite/article.html
        SQLiteGovHelper openHelper = new SQLiteGovHelper(this);
        mDB = sqlBrite.wrapDatabaseHelper(openHelper);

        // now read some data
        Observable<SqlBrite.Query> members = mDB.createQuery(mTable, "SELECT * FROM " + mTable);
        members.subscribe(new Action1<SqlBrite.Query>() {
            @Override
            public void call(SqlBrite.Query query) {
                final ColumnIndexCache cache = new ColumnIndexCache();
                Cursor c = query.run();
                Log.d(TAG, "[subscribe] c.getCount() " + c.getCount());
                // TODO parse data...
                while (c.moveToNext()) {
                //for (boolean hasItem = c.moveToFirst(); hasItem; hasItem = c.moveToNext()) {
                    // use cursor to work with current item
                    String name = c.getString(cache.getColumnIndex(c, SQLiteGovHelper.COLUMN_NAME));
                    String twitterid = c.getString(cache.getColumnIndex(c, SQLiteGovHelper.COLUMN_TWITTERID));
                    //Log.d(TAG, " " + name + ", @" + twitterid);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "[onResume]");
        // write some data
        final int deletedRows = mDB.delete(mTable, "1", null);
        Log.d(TAG, "[onResume] deletedRows: " + deletedRows);
        mDB.insert(mTable, createUser("Android", "Android"));
        mDB.insert(mTable, createUser("Android Developers", "AndroidDev"));
        mDB.insert(mTable, createUser("Google", "google"));
    }

    public ContentValues createUser(String name, String twitterid) {
        ContentValues cv = new ContentValues();
        cv.put(SQLiteGovHelper.COLUMN_NAME, name);
        cv.put(SQLiteGovHelper.COLUMN_TWITTERID, twitterid);
        return cv;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
