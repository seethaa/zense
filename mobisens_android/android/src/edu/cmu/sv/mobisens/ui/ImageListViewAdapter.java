package edu.cmu.sv.mobisens.ui;

import java.util.List;


import edu.cmu.sv.mobisens.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageListViewAdapter extends ArrayAdapter<ImageListViewItem> {
	
	private LayoutInflater inflater = null;
	private Context context = null;
	private int itemLayoutResourceId = R.layout.image_listview_item;
	private int itemImageViewId = R.id.img;
	private int itemTitleViewId = R.id.item_title;
	private int itemDescriptionViewId = R.id.item_description;

	public ImageListViewAdapter(Context context,
			int itemLayoutResourceId, 
			List<ImageListViewItem> objects) {
		super(context, itemLayoutResourceId, R.id.item_title, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this.itemLayoutResourceId = itemLayoutResourceId;
	}
	
	
	
	public View getView(int position, View view, ViewGroup parent) {
		View v = view;
        ImageListViewItem rowData = getItem(position);
        
        if(view == null){
        	//v = this.inflater.inflate(R.layout.image_listview_item, null);
        	this.inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        	v = this.inflater.inflate(itemLayoutResourceId, null);
        }
        
        LinearLayout textContainer = (LinearLayout) v.findViewById(R.id.item_text_container);
        TextView title = (TextView) v.findViewById(this.itemTitleViewId);
        TextView detail = (TextView) v.findViewById(this.itemDescriptionViewId);
        ImageView icon = (ImageView) v.findViewById(this.itemImageViewId);
        
        if(!rowData.isOwnerDraw()){
			title.setText(rowData.getTitle());
			detail.setText(rowData.getDescription());
			icon.setImageResource(rowData.getIconResourceId());
        }else{
        	textContainer.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            detail.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            
        	rowData.draw(v, parent);  // Let the owner draw do whatever they want.
        	
        }
		return v;
	}
}
