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

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity2 extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private final String mTable = SQLiteGovHelper.TABLE_QUESTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "[onCreate]");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView mRV = (RecyclerView) findViewById(R.id.rv);
        mRV.setLayoutManager(new LinearLayoutManager(this));// setup LayoutManager
        mRV.setItemAnimator(new DefaultItemAnimator());// setup ItemAnimator

        // setup sqlBrite
        SqlBrite sqlBrite = SqlBrite.create();
        SQLiteGovHelper openHelper = new SQLiteGovHelper(this);
        final BriteDatabase mDB = sqlBrite.wrapDatabaseHelper(openHelper);
        final int deletedRows = mDB.delete(mTable, "1");
        Log.d(TAG, "[onCreate] deletedRows: " + deletedRows);

        // get service
        final GovService service = ApplicationRx.getService();

        Log.d(TAG, "starting up observable...");
        service.getQuestionRx("satvocab_000.json")
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
                                long result = mDB.insert(mTable, createUser(questionList.get(i)));
                                //mDB.execute();
                                //long result = mDB.update(mTable, createUser(questionList.get(i)), "i = " + questionList.get(i).getI());
                                Log.d(TAG, "long: " + result + ", " + questionList.get(i).toString());
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
                        Observable<SqlBrite.Query> members = mDB.createQuery(mTable, "SELECT * FROM " + mTable);
                        members.subscribe(new Action1<SqlBrite.Query>() {
                            @Override
                            public void call(SqlBrite.Query query) {
                                Cursor c = query.run();
                                Log.d(TAG, "[onCompleted] c.getCount() " + c.getCount());
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "[onError] " + t.getMessage());
                        t.printStackTrace();
                    }
                });


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
}
