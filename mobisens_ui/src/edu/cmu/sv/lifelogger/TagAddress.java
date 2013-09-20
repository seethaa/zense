package edu.cmu.sv.lifelogger;

import java.util.ArrayList;

import edu.cmu.sv.mobisens_ui.R;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

public class TagAddress extends Activity {



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tag_address, menu);
		return true;
	}
	

	 ListView listView;

	 ArrayList< String>arrayList; // list of the strings that should appear in ListView
	 ArrayAdapter arrayAdapter; // a middle man to bind ListView and array list 
	 
	   
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_tag_address );
	        
	        listView = (ListView) findViewById(R.id.lstDemo);
	        
	        
	        //TODO: Take these strings from users. currently hardcoded
	        
	        arrayList = new ArrayList();
	        arrayList.add("Home");
	        arrayList.add("Work");
	        arrayList.add("School");
	        arrayList.add("No Tag/Remove Tag");
	        arrayList.add("Add Tag");
	        
	        
	        
	        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_single_choice,arrayList);
	        listView.setAdapter(arrayAdapter);
	        
	        
	        
	        //  LETS HIGHLIGHT SELECTED ITEMS
	        
	        listView.setOnItemClickListener(new OnItemClickListener() {

	   @Override
	   public void onItemClick(AdapterView arg0, View view, int position,
	     long itemId) {
	    
	    
	    /*  
	     *  when we click on item on list view we can get it catch item here.
	     * so view is the item clicked in list view and position is the position 
	     * of that item in list view which was clicked.
	     * 
	     * Now that we know which item is click we can easily change the color
	     * of text but when we click on next item we we have to deselect the old 
	     * selected item means recolor it back to default , and then hight the 
	     * new selected item by coloring it .
	     * 
	     * So here's the code of doing it.
	     * 
	     * 
	     * */
	    
	    
	    CheckedTextView textView = (CheckedTextView) view;
	    for (int i = 0; i < listView.getCount(); i++) {
	     textView= (CheckedTextView) listView.getChildAt(i);
	     if (textView != null) {
	      textView.setTextColor(Color.BLACK);
	     }
	     
	    }
	    listView.invalidate();
	    textView = (CheckedTextView) view;
	    if (textView != null) {
	     textView.setTextColor(Color.BLUE);
	    }

	   }
	  });
	        
	        
	     
	        
	    }
	
	
	

}
