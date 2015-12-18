package rx.test;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by agoyal3 on 12/16/15.
 */
public class AdapterGovCursor extends CursorRecyclerViewAdapter<AdapterGovCursor.ViewHolder> {
    private final String TAG = getClass().getSimpleName();
    private Context mCtx;

    //public interface ActionSelection {
    //    public void onActionSelected(int position);
    //}

    public AdapterGovCursor(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName, mTwitterId;

        public ViewHolder(View v) {
            super(v);
            mName = (TextView) v.findViewById(R.id.tv_name);
            mTwitterId = (TextView) v.findViewById(R.id.tv_twitterid);
        }
    }

    @Override
    public AdapterGovCursor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mCtx = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_member, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, Cursor cursor) {
        ModelGovMember m = ModelGovMember.fromCursor(cursor);
        vh.mName.setText(m.getName());
        vh.mTwitterId.setText("@" + m.getTwitterId());
    }

}
