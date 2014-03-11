package edu.cmu.sv.mobisens;


import edu.cmu.sv.mobisens.ui.Eula;
import edu.cmu.sv.mobisens.util.About;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MobiSensInformation extends Activity {

	public final static String INFO_TITLE = MobiSensInformation.class.getName() + ".title";
	public final static String EXTRA_TYPE = MobiSensInformation.class.getName() + ".extra_type";
	
	public final static int TYPE_ABOUT = 0;
	public final static int TYPE_EULA = 1;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		
		ActionBar actionBar = getActionBar();
		if(actionBar != null){
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
		}
		
		
		Intent intent = getIntent();
		int type = intent.getIntExtra(EXTRA_TYPE, TYPE_ABOUT);
		switch(type){
		case TYPE_ABOUT:
			showAbout();
			break;
		case TYPE_EULA:
			showEULA();
			break;
		}
		
	}
	
	private void showAbout(){
		PackageManager manager = this.getPackageManager();
    	String mobisensVersionInfo = "About MobiSens";
    	
    	try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			mobisensVersionInfo += " v" + info.versionName + "(" + info.versionCode + ")\r\n\r\n";
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		
    	display(getString(R.string.about_title), mobisensVersionInfo + About.readAbout(this).toString());
	}
	
	protected void showEULA(){
		display(getString(R.string.eula_title), Eula.readEula(this).toString());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	this.onBackPressed();
	        return true;
	    
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void display(String title, String content){
		TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);
		TextView tvContent = (TextView)this.findViewById(R.id.tvContent);
		
		tvTitle.setText(title);
		tvContent.setText(content);
	}
}
