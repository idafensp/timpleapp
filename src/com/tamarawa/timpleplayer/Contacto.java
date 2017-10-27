package com.tamarawa.timpleplayer;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;

public class Contacto extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacto);
		//avoid orientation change
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacto, menu);
		return true;
	}
	
	public void openEmail(View v)
	{
		Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "[TIMPLE APP]");
		intent.putExtra(Intent.EXTRA_TEXT, "");
		intent.setData(Uri.parse("mailto:lagavetapps@gmail.com")); // or just "mailto:" for blank
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
		startActivity(intent);
	}
	
	public void openFacebook(View v)
	{
		Intent intent = getOpenFacebookIntent(Contacto.this);
		startActivity(intent);
	}
	
	public static Intent getOpenFacebookIntent(Context context) {

//	   try {
//	    context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
//	    return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/917535461591145"));
//	   } catch (Exception e) {
	    return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pages/Timple-Player-App/917535461591145"));
//	   }
	}
	
	public void openTwitter(View v)
	{
		Intent intent = null;
		try {
		    // get the Twitter app if possible
		    this.getPackageManager().getPackageInfo("com.twitter.android", 0);
		    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=2711994925"));
		    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		} catch (Exception e) {
		    // no Twitter app, revert to browser
		    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/timpleapp"));
		}
		this.startActivity(intent);
	}

}
