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

import com.squareup.okhttp.OkHttpClient;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
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

        //testSqlBrite();
        testRetrofit();
    }

    private void testRetrofit() {
        // http://square.github.io/retrofit, documentation
        OkHttpClient client = new OkHttpClient();
        //client.interceptors().add();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://goanuj.freeshell.org")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GovService service = retrofit.create(GovService.class);

        // do a HTTP:GET, observe result and print it out
        Log.d(TAG, " call before: "  + System.currentTimeMillis());
        Call<MyTest> call = service.getMyTest();
        Log.d(TAG, " call after: "  + System.currentTimeMillis());

        call.enqueue(new Callback<MyTest>() {
            @Override
            public void onResponse(Response<MyTest> response, Retrofit retrofit) {
                int statusCode = response.code();
                MyTest t = response.body();
                Log.d(TAG, " code: "  + statusCode);
                Log.d(TAG, " body: "  + t.toString());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, " error: " + t.getMessage());
            }
        });
        // save it to database
    }

    public class GovMember {
        private String name;
        private String twitterId;
    }

    public class MyTest {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String toString() {
            return "Name: " + this.name;
        }
    }

    public interface GovService {
        @GET("/txt2lrn/sat/index_1.json")
        Call<MyTest> getMyTest();

        @GET("/txt2lrn/sat/index_1.json")
        Observable<MyTest> getMyTestObservable();

        //@GET("/gists/{id}")
        //Observable<GistDetail> gist(@Path("id") String id);
    }



    private void testSqlBrite() {
        SqlBrite sqlBrite = SqlBrite.create();  // should probably move to Application
        // http://www.vogella.com/tutorials/AndroidSQLite/article.html
        SQLiteGovHelper openHelper = new SQLiteGovHelper(this);
        mDB = sqlBrite.wrapDatabaseHelper(openHelper);
        // now read some data
        final ColumnIndexCache cache = new ColumnIndexCache();
        Observable<SqlBrite.Query> members = mDB.createQuery(mTable, "SELECT * FROM " + mTable);
        members.subscribe(new Action1<SqlBrite.Query>() {
            @Override
            public void call(SqlBrite.Query query) {
                Cursor c = query.run();
                Log.d(TAG, "[subscribe] c.getCount() " + c.getCount());
                // TODO parse data...
                while (c.moveToNext()) {
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
        //final int deletedRows = mDB.delete(mTable, "1", (String)null);
        //Log.d(TAG, "[onResume] deletedRows: " + deletedRows);
        //insertViaTransaction();
    }

    public ContentValues createUser(String name, String twitterid) {
        ContentValues cv = new ContentValues();
        cv.put(SQLiteGovHelper.COLUMN_NAME, name);
        cv.put(SQLiteGovHelper.COLUMN_TWITTERID, twitterid);
        return cv;
    }

    public void insertViaTransaction() {
        BriteDatabase.Transaction transaction = mDB.newTransaction();
        try {
            mDB.insert(mTable, createUser("Android", "Android"));
            mDB.insert(mTable, createUser("Android Developers", "AndroidDev"));
            mDB.insert(mTable, createUser("Google", "google"));
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
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
