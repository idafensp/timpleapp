package com.tamarawa.timpleplayer;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

public class StringOnTouchListener implements OnTouchListener {

	public static boolean CALCULATE_COORDINATES;
	public static String LABEL;
	
	public StringOnTouchListener(String l)
	{
		CALCULATE_COORDINATES = false;
		LABEL = l;
	}

	@Override
    public boolean onTouch(final View view, final MotionEvent event) 
    {
		
		
		System.out.println("OTL-" + LABEL + "-" + view.getId() + "-" + event.getAction());
//		if(!CALCULATE_COORDINATES)
//    	{
//    		
//    		CALCULATE_COORDINATES = true;
//    		
//    		//System.out.println("onTouch" + event.getAction());
//            if(event.getAction()==2)
//            {
//            	
//            }
//    	}
		return true;
    }
    	
		

}
