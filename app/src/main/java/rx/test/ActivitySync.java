package rx.test;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by agoyal3 on 1/19/16.
 */
public class ActivitySync extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private static String STAG;
    public static final String AUTHORITY = "rx.test.provider";
    public static final String ACCOUNT_TYPE = "rx.test";
    public static final String ACCOUNT = "default_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "[onCreate]");
        super.onCreate(savedInstanceState);
        STAG = TAG;
        setContentView(R.layout.activity_main);
        // setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // setup fields
        TextView tv = (TextView) findViewById(R.id.tv_hello);
        RecyclerView mRV = (RecyclerView) findViewById(R.id.rv);
        mRV.setLayoutManager(new LinearLayoutManager(this));// setup LayoutManager
        mRV.setItemAnimator(new DefaultItemAnimator());// setup ItemAnimator

        // perform a periodic task
        SyncAdapter sa = new SyncAdapter(this, true);
        Account a = createSyncAccount(this);
        Bundle b = Bundle.EMPTY;
        //ContentProviderClient cp = new ContentProviderClient();
        sa.onPerformSync(a, b, "foo", null, null);
    }

    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        // Add the account and account type, no password or user data
        // If successful, return the Account object, otherwise report an error.
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            // If you don't set android:syncable="true" in your <provider> element in the manifest,
            // then call context.setIsSyncable(account, AUTHORITY, 1) here.
            Log.d(STAG, "[createSyncAccount] success: account added");
        } else {
            // The account exists or some other error occurred. Log this, report it or handle it internally.
            Log.d(STAG, "[createSyncAccount] error: account exists, account is null, or another error occurs");
        }
        return newAccount;
    }
}
