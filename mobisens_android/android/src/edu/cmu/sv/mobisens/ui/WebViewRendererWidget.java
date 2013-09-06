package edu.cmu.sv.mobisens.ui;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.ContextWrapper;
import android.content.Intent;

import edu.cmu.sv.mobisens.util.Annotation;



public class WebViewRendererWidget extends RendererWidget {
	
	private WebViewActivity renderer;

	@Override
	public String getRenderType() {
		return this.getClass().getName();
	}
	
	protected void beforeRegistered(ContextWrapper contextWrapper){
		if(!(contextWrapper instanceof WebViewActivity)){
			throw new IllegalArgumentException("context must be an instance of ListActivity.");
		}
		
		this.renderer = (WebViewActivity)contextWrapper;
	}
	
	
	public void unregister(){
		this.renderer = null;
		
		super.unregister();
	}
	
	protected void onClear(Intent intent){
		super.onClear(intent);
	}
	
	protected void onRenderItem(Intent intent){
		
  
	}

	@Override
	protected boolean onUpdateItem(Intent intent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String[] getActions() {
		// TODO Auto-generated method stub
		return new String[0];
	}

}
