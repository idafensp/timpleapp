package com.tamarawa.timpleplayer;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;

public class Info extends Activity {

	private static final String TAG = "TP-IN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		//avoid orientation change
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		
		 WebView wv = (WebView)findViewById(R.id.infoExtraWebView);
		 
		 Log.d(TAG, "wv="+wv);
		 
		 wv.loadUrl("file:///android_asset/info.html");
		 
		 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}
	


}
