package rx.test;

import android.app.Application;
import android.content.Context;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by agoyal3 on 12/24/15.
 */
public class ApplicationRx extends Application {
    private static GovService mService;

    @Override
    public void onCreate() {
        super.onCreate();
        // initialize singletons so their instances are bound to the application process.
        // http://www.devahead.com/blog/2011/06/extending-the-android-application-class-and-dealing-with-singleton/
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://goanuj.freeshell.org")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mService = retrofit.create(GovService.class);
    }

    public static GovService getService() {
        return mService;
    }

    public static BriteDatabase getDB(Context context) {
        SqlBrite sqlBrite = SqlBrite.create();
        SQLiteGovHelper openHelper = new SQLiteGovHelper(context);
        BriteDatabase mDB = sqlBrite.wrapDatabaseHelper(openHelper);
        return mDB;
    }

}