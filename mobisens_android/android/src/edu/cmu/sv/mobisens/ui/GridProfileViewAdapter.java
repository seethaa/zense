package edu.cmu.sv.mobisens.ui;

import java.util.List;


import edu.cmu.sv.mobisens.MobiSensLauncher;
import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.content.ProfileWidget;
import edu.cmu.sv.mobisens.settings.ServiceParameters;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridProfileViewAdapter extends ArrayAdapter<ServiceParameters> {
	private LayoutInflater inflater = null;
	private Context context = null;

	public GridProfileViewAdapter(Context context,
			List<ServiceParameters> objects) {
		super(context, R.id.profile_title, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}
	
	public View getView(int position, View view, ViewGroup parent) {
		
		View v = view;
        ServiceParameters profile = getItem(position);
        
        if(view == null){
        	//v = this.inflater.inflate(R.layout.image_listview_item, null);
        	this.inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        	v = this.inflater.inflate(R.layout.profile_list_view_item, null);
        	v.setTag(position);
        }
        
        ImageView image = (ImageView) v.findViewById(R.id.profile_image);
        image.setImageBitmap(profile.getProfileImage());
		TextView caption = (TextView) v.findViewById(R.id.profile_title);
		caption.setText(profile.getName());
		//caption.setWidth(image.getWidth());
		
		//caption.measure(0, 0);
		//Log.i("View", String.valueOf(caption.getMeasuredHeight()));
		//caption.setMaxHeight(48);  // This is a hack, but I can't find better ways to fix the height.
		
		
		return v;
	}
	
	
}
