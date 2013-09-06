package edu.cmu.sv.mobisens.ui;


import edu.cmu.sv.mobisens.R;
import edu.cmu.sv.mobisens.settings.LocalSettings;
import edu.cmu.sv.mobisens.settings.SystemServiceStatus;
import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SharingOptionsActivity extends Activity {
	private CheckBox chkShare = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharing_options);
		chkShare = (CheckBox) this.findViewById(R.id.chkShare);
		
		chkShare.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				LocalSettings.setSharing(getApplication(), isChecked);
			}
			
		});

	}
	
	protected void onResume(){
		super.onResume();
		
		chkShare.setChecked(LocalSettings.isSharing(getApplication()));
		chkShare.setEnabled(SystemServiceStatus.isGPSEnabled(this) || SystemServiceStatus.isNetworkLocationEnabled(this));
	}
}
