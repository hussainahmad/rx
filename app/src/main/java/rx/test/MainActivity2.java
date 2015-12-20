package rx.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity2 extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

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

        // setup retrofit in singleton
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://goanuj.freeshell.org")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        GovService service = retrofit.create(GovService.class);

        Log.d(TAG, "starting up observable...");

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
                });*/

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
                });

        /* BAD code
        Observable<MyTest> o = service.getOneTestRx();
        o.subscribeOn(Schedulers.io());
        o.observeOn(AndroidSchedulers.mainThread());
        o.unsubscribeOn(Schedulers.io());
        o.subscribe(new Observer<MyTest>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "[onCompleted] ");
            }

            @Override
            public void onError(Throwable t) {
                Log.d(TAG, "[onError] ");
                t.printStackTrace();
            }

            @Override
            public void onNext(MyTest m) {
                Log.d(TAG, "[onNext] " + m.toString());
            }
        });*/
    }
}
