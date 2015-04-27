package com.unimelb.breakout;

/**
 * COMP90018 Mobile Computing System Programming, Project Breakout Game
 * Semester 2, 2014
 * Group 25
 * Students: (Name, StudentNumber, Email)
 *          Chenchao Ye, 633657, chenchaoy@student.unimelb.edu.au
 *          Fengmin Deng, 659332, dengf@student.unimelb.edu.au
 *          Jiajie Li, 631482, jiajiel@student.unimelb.edu.au
 *          Shuangchao Yin, 612511, shuangchaoy@student.unimelb.edu.au
 */

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();    

    private volatile RuntimeData rData;
	private WorldView worldView;
    private TextView myName;
    private TextView rivalName;
    private TextView myScore;
    private TextView rivalScore;

    private View transpentLayer;
    private volatile int collideId;
    private volatile SoundPool sp;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
            rData = (RuntimeData) savedInstanceState.getSerializable("RUNTIME.DATA");
            Log.d(TAG,"savedInstanceState is mainActivity.onCreate is not null");
        }
        if (rData == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                rData = (RuntimeData) extras.getSerializable("RUNTIME.DATA");
            }
        }
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Force device to stay in portrait orientation
		//requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove banner from the top of the activity
		setContentView(R.layout.activity_main); //Set the layout to activity_main
		
		displayReadyScreen();

		myName = (TextView)findViewById(R.id.val_my_name);
		rivalName = (TextView)findViewById(R.id.val_rival_name);  
		myScore = (TextView)findViewById(R.id.val_my_score);
		rivalScore = (TextView)findViewById(R.id.val_rival_score);
        
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		collideId = sp.load(this, R.raw.collide, 1);

		Log.d(TAG,"game view onCreate");
	}
	
	protected void onStart() {
		super.onStart();
		//Log.i(TAG,"this is onStart");
	}
	
	protected void onResume() {
		super.onResume();
		Log.i(TAG,"game view onResume");
	}

    protected void onPause() {
    	super.onPause();
        Log.i(TAG,"game play onPause");
    }
    
	protected void onStop() {
	    super.onStop();  
	    //Log.i(TAG,"this is onStop");
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sp != null) {
            sp.release();
        }
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        Log.i(TAG,"this is onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("RUNTIME.DATA", rData);
    }

    public void showRuntimeData(){
        this.runOnUiThread(new Runnable() {     
            public void run() {
            	myName.setText(rData.getMyName());
                myScore.setText(String.valueOf(rData.getMyScore()));
                rivalName.setText(rData.getRivalName());
                rivalScore.setText(String.valueOf(rData.getRivalScore()));
            } 
         });
    }
    
    public void displayReadyScreen() {
        worldView = (WorldView)findViewById(R.id.worldView);
        transpentLayer = (View)findViewById(R.id.trans_layer);
        final TextView prompt_message = (TextView)findViewById(R.id.txt_ready);
        prompt_message.setText(R.string.tip_ready);
        transpentLayer.setVisibility(View.VISIBLE);
        transpentLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prompt_message.setText(R.string.go); //TODO!!!
                v.setVisibility(View.INVISIBLE);
                rData.setRunning(true);
                new Thread(worldView).start();
            }
        });
    }
    
    public void generateGameOverDialog() {
        // create alert dialog
        //AlertDialog alertDialog = dgb.create();
        this.runOnUiThread(new Runnable() {   // Use the context here
            public void run() {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                // set title
                dialog.setTitle("Game Over");
                // set dialog message
                dialog.setMessage("Please try again :)");
                dialog.setCancelable(false);
                dialog.setNeutralButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.dismiss(); 
                        clearScore();
                        MainActivity.this.finish();
                        //set the running to false 
                        //in order to exit the while loop below and exit the while loop in 
                        //worldView and finish the activity.
                    }
                });
                dialog.show();
            }
        });
    }
    
    public void clearScore() {
        rData.setMyScore(0);
        rData.setRivalScore(0);
        SharedPreferences sharedPref = getSharedPreferences(MenuActivity.PREF, 
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SAVED.MYSCORE", rData.getMyScore());
        editor.putInt("SAVED.RIVALSCORE", rData.getRivalScore());
        editor.commit();
    }

    public RuntimeData getrData() {
        return rData;
    }
    
    public int getCollideId() {
        return collideId;
    }

    public SoundPool getSp() {
        return sp;
    }
}
