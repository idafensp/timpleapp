package com.tamarawa.timpleplayer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;

public class Start extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		//avoid orientation change
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	public void startPlayer(View v)
	{
    	Intent intent = new Intent(this, Player.class);
    	intent.putExtra("DEBUG_MODE", true);
    	startActivity(intent);  	 
	}

	
	public void startTutorial(View v)
	{
    	Intent intent = new Intent(this, Tutorial.class);
    	startActivity(intent);  	 
	}
	
	public void startContacto(View v)
	{
    	Intent intent = new Intent(this, Contacto.class);
    	startActivity(intent);  	 
	}
	
	public void startAjustes(View v)
	{
    	Intent intent = new Intent(this, Settings.class);
    	startActivity(intent);  	 
	}

}
