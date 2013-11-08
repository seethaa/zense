package edu.cmu.sv.lifelogger.database;

import java.util.ArrayList;

import edu.cmu.sv.mobisens_ui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlaceAdapter extends ArrayAdapter<Place> {
    public Context context;
    public int layoutResourceId;
    public ArrayList<Place> places;

    public PlaceAdapter(Context context, int layoutResourceId, ArrayList<Place> places) {
        super(context, layoutResourceId, places);
        this.layoutResourceId = layoutResourceId;
        this.places = places;
    }

    @Override
    public View getView(int rowIndex, View convertView, ViewGroup parent) {
/*        View row = convertView;
        if(null == row) {
            LayoutInflater layout = (LayoutInflater)getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE
            );
            row = layout.inflate(R.layout.httptestrow, null);
        }
        Place place = places.get(rowIndex);
        if(null != place) {
            TextView name = (TextView) row.findViewById(R.id.htttptestrow_name);
            TextView vicinity = (TextView) row.findViewById(
                    R.id.httptestrow_vicinity);
            if(null != name) {
                name.setText(place.getName());
            }
            if(null != vicinity) {
                vicinity.setText(place.getVicinity());
            }
        }*/
        return null;
    }
}