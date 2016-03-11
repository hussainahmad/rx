package rx.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by agoyal3 on 1/19/16.
 */
public class SyncService extends Service {
    private static final String ACTION_BIND_SYNC = "android.content.SyncAdapter";
    private SyncAdapter syncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        syncAdapter = new SyncAdapter(getApplication(), true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (ACTION_BIND_SYNC.equals(intent.getAction())) {
            return syncAdapter.getSyncAdapterBinder();
        }
        return null;
    }
}


