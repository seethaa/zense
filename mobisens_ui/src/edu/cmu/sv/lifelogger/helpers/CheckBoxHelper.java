package edu.cmu.sv.lifelogger.helpers;

import java.util.ArrayList;

import edu.cmu.sv.mobisens_ui.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * CheckBoxesPiece is a layout that contains a number of checkboxes based on n number of choices given by user
 *
 *--- not for use now.
 */
public class CheckBoxHelper extends LayoutHelper {
	private ArrayList<Integer> inner_ids;
	private ArrayList<String> mResult;

	public CheckBoxHelper(Context cxt, String text, LinearLayout mainlayout, ArrayList<String> choices ){
		super(cxt, text, mainlayout);

		LinearLayout newView0 = (LinearLayout) View.inflate(cxt, R.layout.vert_checkboxes_layout, null);
		mainlayout.addView(newView0);

		TextView tv = (TextView) newView0.findViewById(R.id.RBtextToShow);
		tv.setText(text);

		LinearLayout ll = (LinearLayout) newView0.findViewById(R.id.checkboxes_list);

		mResult = new ArrayList<String>();

		for (String s : choices) {
			CheckBox cb = new CheckBox(cxt);
			cb.setText(s);
			cb.setOnClickListener(checkbox_listener);
			ll.addView(cb);
		}

	}

	/**
	 * attaches a listener to the each checkbox that is added
	 */
	OnClickListener checkbox_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			onBoxCheck(v);
		}
	};

	/**
	 *Action method for what to do when a box is checked -- adds the choice to mResult to be called when button is clicked
	 * @param v
	 */
	public void onBoxCheck(View v) {
		CheckBox cbchoice = (CheckBox) v;

		if (cbchoice.isChecked()){
			//			Toast.makeText(cxt,cbchoice.getText() + " was chosen.", Toast.LENGTH_SHORT).show();

			if (!mResult.contains(cbchoice.getText())){
				mResult.add(cbchoice.getText().toString());

			}
		}
		else if (!cbchoice.isChecked()){
			mResult.remove(cbchoice.getText().toString());
		}
	}

	@Override
	public ArrayList<String> getmResult() {
		//		mResult = mResult.substring(4);
		return this.mResult;
	}






}
