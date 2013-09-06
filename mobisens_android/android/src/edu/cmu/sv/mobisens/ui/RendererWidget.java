package edu.cmu.sv.mobisens.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import edu.cmu.sv.lifelogger.api.ActivityRecognitionRequestScheduler;
import edu.cmu.sv.mobisens.content.ActivityReader;
import edu.cmu.sv.mobisens.content.AnnotationWidget;
import edu.cmu.sv.mobisens.content.Widget;
import edu.cmu.sv.mobisens.io.DataShrinker;
import edu.cmu.sv.mobisens.io.MobiSensDataHolder;
import edu.cmu.sv.mobisens.io.MobiSensLog;
import edu.cmu.sv.mobisens.util.Annotation;

public abstract class RendererWidget extends Widget {
	
	private static final String CLASS_PREFIX = RendererWidget.class.getName();
	public static final String ACTION_UPDATE_ANNO = CLASS_PREFIX + ".action_update_item";
	public static final String ACTION_RENDER_ITEM = CLASS_PREFIX + ".action_render_item";
	
	public static final String ACTION_RENDER_ITEMS = CLASS_PREFIX + ".action_render_items";
	public static final String ACTION_ALL_RENDER_DONE = CLASS_PREFIX + ".action_read_done";
	public static final String ACTION_CLEAR = CLASS_PREFIX + ".action_clear";
	
	public static final String ACTION_DATA_MODIFIED_OUTSIDE = CLASS_PREFIX + ".action_data_modified_outside";
	
	public static final String EXTRA_RENDERER_TYPE = CLASS_PREFIX + ".extra_renderer_type";
	public static final String EXTRA_APPEND_IF_FAILED = CLASS_PREFIX + ".extra_append_if_failed";
	public static final String ALL_RENDERER_TYPES = RendererWidget.class.getName();
	
	private ArrayList<AsyncTask<?,?,?>> unfinishRenderingTasks = new ArrayList<AsyncTask<?,?,?>>();
	
	public void register(ContextWrapper contextWrapper){
		IntentFilter filter = new IntentFilter(RendererWidget.ACTION_RENDER_ITEMS);
		filter.addAction(RendererWidget.ACTION_ALL_RENDER_DONE);
		filter.addAction(RendererWidget.ACTION_CLEAR);
		filter.addAction(RendererWidget.ACTION_UPDATE_ANNO);
		filter.addAction(AnnotationWidget.ACTION_APPEND_ANNO_DONE);
		filter.addAction(RendererWidget.ACTION_DATA_MODIFIED_OUTSIDE);
		
		this.setFilter(filter);
		
		super.register(contextWrapper);
		
	}
	
	public void unregister(){
		for(AsyncTask<?,?,?> renderTask:this.unfinishRenderingTasks){
			renderTask.cancel(true); // Prevent memory leak and update disposed UI.
		}
		
		super.unregister();
	}
	
	
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		final String rendererType = intent.getStringExtra(EXTRA_RENDERER_TYPE);
		if(rendererType.equals(RendererWidget.ALL_RENDERER_TYPES) == false && 
				rendererType.equals(this.getClass().getName()) == false)
			return;
		
		if(action.equals(RendererWidget.ACTION_ALL_RENDER_DONE)){
			this.onDone(intent);
			
		}
		
		if(action.equals(RendererWidget.ACTION_CLEAR)){
			this.onClear(intent);
			
		}
		
		if(action.equals(RendererWidget.ACTION_RENDER_ITEMS)){
			
			final Activity renderedActivity = (Activity) ((this.getContext() instanceof Activity) ? this.getContext() : null);
			
			//String colors = intent.getStringExtra(ActivityReader.EXTRA_RENDER_COLOR);

			//String[] colorArray = (colors == null ? new String[0] : colors.split("\t"));
			MobiSensLog.log(CLASS_PREFIX + ": start rendering...");
			
			AsyncTask<Long, String, Void> uiUpdateTask = new AsyncTask<Long, String, Void>(){
				private MobiSensDataHolder db;
				
				@Override
				protected void onPreExecute(){
					if(renderedActivity != null){
						renderedActivity.setProgressBarIndeterminateVisibility(true);
					}
					onPrepareRendering();
					
					db = new MobiSensDataHolder(getContext());
					try{
						db.open();
					}catch(Exception ex){
						MobiSensLog.log(ex);
					}
				}
				
				@Override
				protected Void doInBackground(Long... params) {
					// TODO Auto-generated method stub
					
					// Shrink before each read.
					db.batchRemove(DataShrinker.RESERVE_TIME);
					
					String[] annos = db.search(0, params[0]);
					
					ArrayList<String> partialAnnoSet = new ArrayList<String>();
					for(int i = 0; i < annos.length; i++){
						partialAnnoSet.add(annos[i]);
						
						if((i > 0 && i % getProgressiveRenderItemCount() == 0) ||
								(i == annos.length - 1 && partialAnnoSet.size() > 0)){
							publishProgress(partialAnnoSet.toArray(new String[0])); // Update the crap progressively
							partialAnnoSet.clear();
							try {
								Thread.sleep(getProgressiveRenderSleepInterval());
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}
					
					
					return null;
				}
				
				@Override
			    protected void onProgressUpdate(String... annos) {
					// When you are using for loop to update the list view
					// the control WILL NOT return back to UI until all items have been added.
					// So if you have a large set of list items, the user won't see them until
					// all of them were rendered.
					// Holy shit!
					
					for(int i = 0; i < annos.length; i++){
						String annoString = annos[i];
						Intent renderIntent = new Intent(RendererWidget.ACTION_RENDER_ITEM);
						renderIntent.putExtra(Annotation.EXTRA_ANNO_STRING, annoString);
						//renderIntent.putExtra(ActivityReader.EXTRA_RENDER_COLOR, color);

						renderIntent.putExtra(RendererWidget.EXTRA_RENDERER_TYPE, rendererType);
						onRenderItem(renderIntent);
					}
			    }
				
				@Override
			    protected void onPostExecute(Void unused) {
					Intent doneIntent = new Intent(RendererWidget.ACTION_ALL_RENDER_DONE);
					doneIntent.putExtra(RendererWidget.EXTRA_RENDERER_TYPE, rendererType);
					
					onDone(doneIntent);
					if(renderedActivity != null){
						renderedActivity.setProgressBarIndeterminateVisibility(false);
					}
					
					MobiSensLog.log(CLASS_PREFIX + ": Holy shit, rendering finished!");
					unfinishRenderingTasks.remove(this);
					
					db.close();
			    }

				
			};
			
			unfinishRenderingTasks.add(uiUpdateTask);
			uiUpdateTask.execute(new Long[]{System.currentTimeMillis()});
			
		}
		
		if(action.equals(RendererWidget.ACTION_UPDATE_ANNO)){
			this.onUpdateItem(intent);
		}
		
		if(AnnotationWidget.ACTION_APPEND_ANNO_DONE.equals(action)){
			
			boolean merged = intent.getBooleanExtra(AnnotationWidget.EXTRA_ANNO_MERGED, false);
			// We need this to change the UI instantly
			// after merging a new activity segment.
			if(merged){
				this.onUpdateItem(intent);
			} else {  // Update failed, this needs append to UI.
				
				this.onRenderItem(intent);
			}
		}
		
		if(RendererWidget.ACTION_DATA_MODIFIED_OUTSIDE.equals(action)){
			onDataChanged();
		}
	}
	
	/*
	 * Request to refresh the whole activity list.
	 */
	protected void onDataChanged(){
		
	}
	
	protected void onDone(Intent intent){
		
	}
	
	protected void onClear(Intent intent){
		
	}
	
	protected void onPrepareRendering(){
		
	}
	
	protected int getProgressiveRenderItemCount(){
		return 10;
	}
	
	protected long getProgressiveRenderSleepInterval(){
		return 100;
	}
	
	protected abstract void onRenderItem(Intent intent);
	
	
	protected abstract boolean onUpdateItem(Intent intent);
	
	public abstract String getRenderType();
}
