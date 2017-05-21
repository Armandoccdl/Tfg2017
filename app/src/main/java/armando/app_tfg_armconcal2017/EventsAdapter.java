package armando.app_tfg_armconcal2017;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class EventsAdapter extends BaseAdapter {

    private ArrayList<?> events;
    private int R_layout_IdView;
    private Context ctx;

    public EventsAdapter(Context ctx, int R_layout_IdView, ArrayList<?> events) {
        super();
        this.ctx = ctx;
        this.events = events;
        this.R_layout_IdView = R_layout_IdView;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R_layout_IdView, null);
        }
        onEntrance(events.get(position), view);
        return view;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract void onEntrance (Object entrance, View view);
}