package edu.cmu.sv.mobisens.content;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ContextListener implements OnClickListener{

	private Context context;
	private Intent intent;

	public ContextListener(Context context, Intent intent){
		this.context = context;
		this.intent = intent;
	}
	
	public Context getContext(){
		return context;
	}
	
	public Intent getIntent(){
		return intent;
	}
	
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
