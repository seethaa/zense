package edu.cmu.sv.mobisens.ui;

import java.util.ArrayList;


import edu.cmu.sv.mobisens.MobiSensService;
import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.content.ActivityReader;
import edu.cmu.sv.mobisens.content.AnnotationWidget;
import edu.cmu.sv.mobisens.io.DataShrinker;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.util.Annotation;
import edu.cmu.sv.mobisens.util.GeoIndex;
import edu.cmu.sv.mobisens.util.MachineAnnotation;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

public class ListAnnotationActivity extends ListActivity {
	
	private static final String CLASS_PREFIX = ListAnnotationActivity.class.getName();
	
	public final static String UNREG_REFRESH_RECEIVER = CLASS_PREFIX + ".unreg";
	public final static String REG_REFRESH_RECEIVER = CLASS_PREFIX + ".reg";
	
	
	private static final String TAG = CLASS_PREFIX;
	final static int MSG_REFRESHLOAD_ANNO_COMPLETED = 1;
	

	private ListViewRendererWidget renderWidget = new ListViewRendererWidget();
	private ActivityReader reader = new ActivityReader(0, 0);
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.list_activity);
		
		ActionBar actionBar = getActionBar();
		if(actionBar != null){
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		this.reader.register(this);
		this.renderWidget.register(this);
		this.reader.setRendererType(this.renderWidget.getRenderType());
		
		this.reader.readAsync();
		Toast.makeText(this, "You can select one item to view the location and change the corresponding acvitity label.", Toast.LENGTH_LONG).show();
		Log.i(TAG, "ListView created.");
	}
	

	
	
	protected void onResume(){
		super.onResume();
		// Please done't refetch the list here, none wants the list refresh after moving back.
	}


	protected void onPause(){
		DataShrinker.shrinkData(this);
		super.onPause();
		
	}
	
	protected void onDestroy(){
		
		reader.unregister();
		renderWidget.unregister();
		
		super.onDestroy();
		Log.i(TAG, "ListView destroyed.");
	}


	public void onListItemClick(ListView l, View v, int position, long id){
		//ImageListViewItem item = (ImageListViewItem)this.getListAdapter().getItem(position);
		HeaderListViewItem item = (HeaderListViewItem)this.getListAdapter().getItem(position);
		
		Intent mapAnnoIntent = new Intent(this, MapAnnotationTimeRangeActivity.class);
		mapAnnoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		mapAnnoIntent.putExtra(Annotation.EXTRA_ANNO_STRING, item.getAnnotation().toString());
		mapAnnoIntent.putExtra(MapAnnotationActivity.EXTRA_ANNO_INDEX, position);
		mapAnnoIntent.putExtra(MapAnnotationActivity.EXTRA_ANNO_COLOR, item.getColor());
		
		l.setSelection(position);
		startActivity(mapAnnoIntent);
	}
	
	
}
