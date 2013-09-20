package edu.cmu.sv.lifelogger.helpers;

import java.util.ArrayList;
import java.util.Random;

import edu.cmu.sv.mobisens_ui.R;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * SingleEditTextPiece is a layout that contains a single edittext along with a textview 
 * --not for use now
 *
 */
public class SingleEditTextHelper extends LayoutHelper {

	private ArrayList<String> mResult;
	private int myId;
	private EditText ed;

	public SingleEditTextHelper(Context cxt, String text, LinearLayout mainlayout){
		super(cxt, text, mainlayout);

		mResult = new ArrayList<String>();

		RelativeLayout newView0 = (RelativeLayout) View.inflate(cxt,
				R.layout.single_edittext_layout, null);
		mainlayout.addView(newView0);

		Random randomGenerator = new Random();
		myId= randomGenerator.nextInt(100);

		ed = (EditText) newView0.findViewById(R.id.editableText);
		ed.setId(myId);
		//		ed.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		//				LayoutParams.WRAP_CONTENT));

		TextView tv = (TextView) newView0.findViewById(R.id.textToShow);
		tv.setText(text);
	}


	@Override
	public ArrayList<String> getmResult() {

		String a = ed.getText().toString();
		Toast.makeText(cxt, a + " was written.", Toast.LENGTH_SHORT).show();
		System.out.println(a+ " was written.");
		if (!a.equalsIgnoreCase("")){
			mResult.clear();
			mResult.add(a);
		}

		return this.mResult;
	}



}
