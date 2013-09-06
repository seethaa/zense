package edu.cmu.sv.mobisens.ui;

import java.util.List;

import edu.cmu.sv.mobisens.R;



import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityListAdapter extends ArrayAdapter<HeaderListViewItem> {

	private LayoutInflater inflater = null;
	private Context context = null;
	
	public ActivityListAdapter(Context context,
			int textViewResourceId, 
			List<HeaderListViewItem> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}
	
	public View getView(int position, View view, ViewGroup parent) {
		
		View v = view;
        HeaderListViewItem rowData = getItem(position);
        
        if(view == null){
        	//v = this.inflater.inflate(R.layout.image_listview_item, null);
        	this.inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        	v = this.inflater.inflate(R.layout.header_listview_item, null);
        }
        
        TextView title = (TextView) v.findViewById(R.id.item_title);
		title.setText(rowData.getTitle());
		TextView subtitle = (TextView) v.findViewById(R.id.item_subtitle);
		if(rowData.getSubTitle().equals("")){
			subtitle.setVisibility(View.GONE);
		}else{
			subtitle.setText(rowData.getSubTitle());
			subtitle.setVisibility(View.VISIBLE);
		}
		
		TextView detail = (TextView) v.findViewById(R.id.item_description);
		detail.setText(rowData.getDescription());                                                     
		LinearLayout colorHeader = (LinearLayout) v.findViewById(R.id.color_header);
		colorHeader.setBackgroundColor(rowData.getColor());
		return v;
	}

}
