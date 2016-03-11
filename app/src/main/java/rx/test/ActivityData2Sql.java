package rx.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ActivityData2Sql extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private final String mTable = SQLiteGovHelper.TABLE_QUESTION;
    private BriteDatabase mDB;
    private Button mButtonGet, mButtonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "[onCreate]");
        setContentView(R.layout.activity_main);
        mButtonGet = (Button) findViewById(R.id.btn_get);
        mButtonDelete = (Button) findViewById(R.id.btn_delete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView mRV = (RecyclerView) findViewById(R.id.rv);
        mRV.setLayoutManager(new LinearLayoutManager(this));// setup LayoutManager
        mRV.setItemAnimator(new DefaultItemAnimator());// setup ItemAnimator

        // get db
        mDB = ApplicationRx.getDB(this);
        //final int deletedRows = mDB.delete(mTable, "1");
        //Log.d(TAG, "[onCreate] deletedRows: " + deletedRows);
        final GovService service = ApplicationRx.getService();

        // check if questions exist on disk first
        checkDisk(service, "satvocab_000.json");

        mButtonGet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "[onclick] get");
                checkDisk(service, "satvocab_001.json");
            }
        });

        mButtonDelete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "[onclick] delete");
                mDB.delete(mTable, "i>0");
            }
        });
    }

    private int getLow(String fileName) {
        return Integer.valueOf(fileName.substring(9, 12)) * 10;
    }

    private void checkDisk(final GovService service, final String fileName) {
        final int low = getLow(fileName);
        final int high = low + 10;
        Log.d(TAG, "[checkDisk] low: " + low + ", high: " + high);

        Observable<SqlBrite.Query> members = mDB.createQuery(mTable,
                "SELECT * FROM " + mTable + " WHERE i>" + low + " AND i<=" + high);

        Log.d(TAG, "[checkDisk] subscribe");
        Subscription s = members.subscribe(new Action1<SqlBrite.Query>() {
            @Override
            public void call(SqlBrite.Query query) {
                Cursor c = query.run();
                Log.d(TAG, "[SqlBrite:call] c.getCount() " + c.getCount());
                if (c.getCount() < 10) {
                    Log.d(TAG, " No data in database, goto network.");
                    c.close();
                    getDataFromNetwork(service, fileName);
                } else {
                    Log.d(TAG, " Yes data in database.");
                    while (c.moveToNext()) {
                        Question q = Question.fromCursor(c);
                        //Log.d(TAG, " count: " + q.toString());
                    }
                    c.close();
                }
            }
        });
        Log.d(TAG, "[checkDisk] unsubscribe");
        s.unsubscribe();
    }

    // get data from network and store in database
    private void getDataFromNetwork(GovService service, final String fileName) {
        Log.d(TAG, "starting up observable...");
        service.getQuestionRx(fileName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Question>>() {
                    @Override
                    public void onNext(List<Question> questionList) {
                        Log.d(TAG, "[onNext] transaction.begin()");
                        BriteDatabase.Transaction transaction = mDB.newTransaction();
                        try {
                            // start at 1 to avoid issues
                            for (int i = 1; i < questionList.size(); ++i) {
                                final Question q = questionList.get(i);
                                //mDB.delete(mTable, "i=" + q.getI()); // safeguard in case row exists
                                mDB.execute(createSQL(q));
                                // Log.d(TAG, "exec: " + q.toString());
                            }
                            transaction.markSuccessful();
                        } finally {
                            transaction.end();
                            Log.d(TAG, "[onNext] transaction.end()");
                        }
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "[onCompleted] ");
                        // TODO: filter by low, high
                        final int low = getLow(fileName);
                        final int high = low + 10;

                        Observable<SqlBrite.Query> members = mDB.createQuery(mTable,
                                "SELECT * FROM " + mTable + " WHERE i>" + low + " AND i<=" + high);
                        Subscription s1 = members.subscribe(new Action1<SqlBrite.Query>() {
                            @Override
                            public void call(SqlBrite.Query query) {
                                Cursor c = query.run();
                                while (c.moveToNext()) {
                                    Question q = Question.fromCursor(c);
                                    //Log.d(TAG, " " + q.toString());
                                }
                                c.close();
                                Log.d(TAG, "[onCompleted] limited query c.getCount() " + c.getCount());
                            }
                        });
                        s1.unsubscribe();

                        // check total # of records
                        Observable<SqlBrite.Query> query = mDB.createQuery(mTable, "SELECT * FROM " + mTable);
                        Subscription s2 = query.subscribe(new Action1<SqlBrite.Query>() {
                            @Override
                            public void call(SqlBrite.Query query) {
                                Cursor c = query.run();
                                Log.d(TAG, "[onCompleted] full query c.getCount() " + c.getCount());
                                c.close();

                            }
                        });
                        s2.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "[onError] " + t.getMessage());
                        t.printStackTrace();
                    }
                });
    }

    private String createSQL(Question q) {
        StringBuilder sb = new StringBuilder("insert or replace into t_q (i,q,a1,a2,a3,a4,a5) ");
        sb.append("values (");
        sb.append(q.getI()).append(",\"");
        sb.append(q.getQ()).append("\",\"");
        sb.append(q.getA1()).append("\",\"");
        sb.append(q.getA2()).append("\",\"");
        sb.append(q.getA3()).append("\",\"");
        sb.append(q.getA4()).append("\",\"");
        sb.append(q.getA5()).append("\")");
        return sb.toString();
    }

    public ContentValues createUser(Question q) {
        ContentValues cv = new ContentValues();
        cv.put(SQLiteGovHelper.COL_I, q.getI());
        cv.put(SQLiteGovHelper.COL_Q, q.getQ());
        cv.put(SQLiteGovHelper.COL_A1, q.getA1());
        cv.put(SQLiteGovHelper.COL_A2, q.getA2());
        cv.put(SQLiteGovHelper.COL_A3, q.getA3());
        cv.put(SQLiteGovHelper.COL_A4, q.getA4());
        cv.put(SQLiteGovHelper.COL_A5, q.getA5());
        return cv;
    }

            /* call service methods to get data
        service.getOneTestRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<MyTest>() {
                    @Override
                    public void onNext(MyTest m) {
                        Log.d(TAG, "[onNext] " + m.toString());
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "[onCompleted] ");
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "[onError] ");
                        t.printStackTrace();
                    }
                });

        service.getTestListRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<List<MyTest>>() {
                    @Override
                    public void onNext(List<MyTest> lm) {
                        Log.d(TAG, "[onNext] ");

                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "[onCompleted] ");
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "[onError] ");
                        t.printStackTrace();
                    }
                });*/
}
