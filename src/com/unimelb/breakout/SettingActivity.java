package com.unimelb.breakout;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;



public class SettingActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Force device to stay in portrait orientation
		requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove banner from the top of the activity
		setContentView(R.layout.activity_setting); //Set the layout to activity_setting
	
	}
	

	
	
	
	
		
	
	
	
	/*
	@Override
	public void onConfigurationChanged(Configuration newConfig) {   
	    super.onConfigurationChanged(newConfig);
	    if (this.getResources().getConfiguration().orientation
	            == Configuration.ORIENTATION_LANDSCAPE){
	    }
	    else if(this.getResources().getConfiguration().orientation
	            ==Configuration.ORIENTATION_PORTRAIT) {
	    }
    } 
    */
}

