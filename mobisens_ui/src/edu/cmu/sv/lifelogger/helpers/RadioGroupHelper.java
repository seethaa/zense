package edu.cmu.sv.lifelogger.helpers;

import java.util.ArrayList;

import edu.cmu.sv.mobisens_ui.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * RadioGroupPiece is a layout that contains a number of radiobuttons based on n number of choices given by user
 * 
 *-- not for use now.
 */
public class RadioGroupHelper extends LayoutHelper {

	private ArrayList<String> mResult;
	private Context cxt;
	private String text;
	private LinearLayout mainlayout;

	private String choice;
	public RadioGroupHelper(Context cxt, String text, LinearLayout mainlayout, ArrayList<String> choices ){
		super(cxt, text, mainlayout);
		this.cxt = cxt;
		this.text = text;
		this.mainlayout = mainlayout;

		mResult = new ArrayList<String>();

		LinearLayout newView0 = (LinearLayout) View.inflate(cxt, R.layout.vert_radiobutton_layout, null);
		mainlayout.addView(newView0);

		TextView tv = (TextView) newView0.findViewById(R.id.RBtextToShow);
		tv.setText(text);

		RadioGroup radioGroup = new RadioGroup(cxt);
		mainlayout.addView(radioGroup);

		for (String s : choices) {
			RadioButton radioButton = new RadioButton(cxt);
			radioButton.setText(s);
			radioButton.setOnClickListener(radio_listener);
			radioGroup.addView(radioButton);
		}

		radioGroup.invalidate();
	}

	/**
	 * attaches a listener to the each radiobutton that is added
	 */
	OnClickListener radio_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			onRadioButtonClick(v);
		}
	};


	/**
	 *Action method for what to do when a radiobutton is chosen -- adds the choice to mResult to be called when button clicked
	 */
	public void onRadioButtonClick(View v) {
		RadioButton button = (RadioButton) v;
		Toast.makeText(cxt,button.getText() + " was chosen.", Toast.LENGTH_SHORT).show();
		mResult.clear();
		mResult.add(button.getText()+"");
	}

	@Override
	public ArrayList<String> getmResult() {

		return this.mResult;
	}

	



}
