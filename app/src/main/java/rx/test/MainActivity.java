package rx.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private final String mTable = SQLiteGovHelper.TABLE_GOV_MEMBERS;
    private BriteDatabase mDB;
    private ArrayList<ModelGovMember> mGovList;
    private AdapterGovCursor mAdapter;
    private RecyclerView mRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "[onCreate]");
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRV = (RecyclerView) findViewById(R.id.rv);
        mRV.setLayoutManager(new LinearLayoutManager(this));// setup LayoutManager
        mRV.setItemAnimator(new DefaultItemAnimator());// setup ItemAnimator
        mAdapter = new AdapterGovCursor(this, null);
        mRV.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // setup data for ListView
        mGovList = new ArrayList<>();

        //testSqlBrite();
        testRetrofit();
    }

    private void testRetrofit() {
        GovService service = ApplicationRx.getService();

        // do a HTTP:GET, observe result and print it out
        Call<MyTest> call = service.getOneTest();
        call.enqueue(new Callback<MyTest>() {
            @Override
            public void onResponse(Response<MyTest> response, Retrofit retrofit) {
                int statusCode = response.code();
                MyTest t = response.body();
                Log.d(TAG, "c1 code: " + statusCode);
                Log.d(TAG, "c1 body: " + t.toString());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "c1 error: " + t.getMessage());
            }
        });

        // get two tests
        Call<List<MyTest>> c2 = service.getTestList();
        c2.enqueue(new Callback<List<MyTest>>() {
            @Override
            public void onResponse(Response<List<MyTest>> response, Retrofit retrofit) {
                int statusCode = response.code();
                List<MyTest> list = response.body();
                Log.d(TAG, "c2 code: " + statusCode);
                Log.d(TAG, "c2 body: " + list.toString());
                // private ArrayList<ModelSetJson> mListSetJson;
                // AdapterTest adap = new AdapterTest(getActivity() or this, mListSetJson);
                // when mListSetJson changes, then adap.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "c2 error: " + t.getMessage());
            }
        });

        // observable
        Log.d(TAG, "starting up observable...");
        Observable<MyTest> o = service.getOneTestRx();
        //o.observeOn(AndroidSchedulers.mainThread())
        o.subscribe(new Subscriber<MyTest>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "[onCompleted] ");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "[onError] " + e.getMessage());
            }

            @Override
            public void onNext(MyTest m) {
                Log.d(TAG, "[onNext] " + m.toString());
            }
        });

        // save it to database
    }

    private void testSqlBrite() {
        // http://www.vogella.com/tutorials/AndroidSQLite/article.html
        // SqlBrite sqlBrite = SqlBrite.create();
        // SQLiteGovHelper openHelper = new SQLiteGovHelper(this);
        // mDB = sqlBrite.wrapDatabaseHelper(openHelper);
        mDB = ApplicationRx.getDB();

        // now read some data
        final ColumnIndexCache cache = new ColumnIndexCache();
        Observable<SqlBrite.Query> members = mDB.createQuery(mTable, "SELECT * FROM " + mTable);
        members.subscribe(new Action1<SqlBrite.Query>() {
            @Override
            public void call(SqlBrite.Query query) {
                Cursor c = query.run();
                Log.d(TAG, "[subscribe] c.getCount() " + c.getCount());

                // either pass cursor to adapter or get data into a ListView
                mAdapter.swapCursor(c);

                /* TODO clear list, parse data, put into ListView ...
                mGovList = new ArrayList<>();
                while (c.moveToNext()) {
                    String name = c.getString(cache.getColumnIndex(c, SQLiteGovHelper.COLUMN_NAME));
                    String twitterid = c.getString(cache.getColumnIndex(c, SQLiteGovHelper.COLUMN_TWITTERID));
                    //Log.d(TAG, " " + name + ", @" + twitterid);
                    mGovList.add(new ModelGovMember(name, twitterid));
                }

                // set adapter*/
            }
        });

        // delete data
        final int deletedRows = mDB.delete(mTable, "1");
        // mDB.delete(mTable, "1", (String)null);
        Log.d(TAG, "[onResume] deletedRows: " + deletedRows);

        // then insert data
        insertUsersViaTransaction();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d(TAG, "[onResume]");
//        //insertViaTransaction();
//    }

    public void insertUsersViaTransaction() {
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
