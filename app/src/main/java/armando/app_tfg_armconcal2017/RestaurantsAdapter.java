package armando.app_tfg_armconcal2017;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class RestaurantsAdapter extends BaseAdapter {

    private ArrayList<?> restaurants;
    private int R_layout_IdView;
    private Context ctx;

    public RestaurantsAdapter(Context ctx, int R_layout_IdView, ArrayList<?> restaurants) {
        super();
        this.ctx = ctx;
        this.restaurants = restaurants;
        this.R_layout_IdView = R_layout_IdView;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R_layout_IdView, null);
        }
        onEntrance(restaurants.get(position), view);
        return view;
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract void onEntrance (Object entrance, View view);
}