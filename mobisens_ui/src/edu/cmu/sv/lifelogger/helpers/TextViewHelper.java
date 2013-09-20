package edu.cmu.sv.lifelogger.helpers;

import java.util.ArrayList;

import edu.cmu.sv.mobisens_ui.R;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * TextViewHelper is a layout that contains a single textview based on user's input
 * TODO: Take options for layout params, color, etc. ?
 * --not for use now
 *
 */
public class TextViewHelper extends LayoutHelper {
	private ArrayList<String> mResult;
	private Context cxt;
	private String text;
	private LinearLayout mainlayout;

	public TextViewHelper(Context cxt, String text, LinearLayout mainlayout ){
		super(cxt, text, mainlayout);
		this.cxt = cxt;
		this.text = text;
		this.mainlayout = mainlayout;

		LinearLayout newView0 = (LinearLayout) View.inflate(cxt,
				R.layout.centered_textview_layout, null);
		mainlayout.addView(newView0);

		TextView tv1 = (TextView) newView0.findViewById(R.id.centeredTextToShow);
		tv1.setText(text);

	}


	@Override
	public ArrayList<String> getmResult() {
		return null;
	}


}
