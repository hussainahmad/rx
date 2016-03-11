package rx.test;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by agoyal3 on 1/19/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final String TAG = getClass().getSimpleName();

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account a, Bundle b, String s, ContentProviderClient cpc, SyncResult sr) {
        doPeriodicTask(a, b);
    }

    void doPeriodicTask(Account a, Bundle b) {
        Log.d(TAG, "doPeriodicTask" + a.toString() + ", " + b.toString());
    }

}
