package edu.cmu.sv.mobisens.ui;

import java.util.ArrayList;


import edu.cmu.sv.mobisens.settings.ServiceParameters;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ProfileListListViewItem extends ImageListViewItem {

	private HorizontalGridView gridView;
	private ServiceParameters[] specialProfiles;
	
	public ProfileListListViewItem(ServiceParameters[] specialProfiles) {
		super("", "", -1);
		
		this.specialProfiles = specialProfiles;
		
	}
	
	public boolean isOwnerDraw(){
		return true;
	}
	
	public void draw(View self, View parent){
		/*
		LinearLayout container = (LinearLayout)parent.findViewById(R.id.linear_layout_for_profiles);
				
        //container.setMinimumHeight(12+5+64+2+48+5);  // shit it wastes me so much time....
        
        if(gridView == null){
        	this.gridView = new HorizontalGridView(self.getContext());
        	
            this.gridView.setLayoutParams(new HorizontalGridView.LayoutParams(
            		HorizontalGridView.LayoutParams.MATCH_PARENT,
            		HorizontalGridView.LayoutParams.WRAP_CONTENT));
            
            
            ArrayList<ServiceParameters> profiles = new ArrayList<ServiceParameters>();
    		 for(ServiceParameters profile:specialProfiles){
    			 profiles.add(profile);
    		 }
    		 
    		 GridProfileViewAdapter adapter = new GridProfileViewAdapter(self.getContext(), profiles);
    		 //this.gridView.setAdapter(adapter);
    		 
    		 container.addView(this.gridView);
    		 
        }else{
        	this.gridView.invalidate();
        }
        */
        
	}

}
