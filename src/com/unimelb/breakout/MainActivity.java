package com.unimelb.breakout;

/**
 * COMP90020 Distributed Algorithms
 * Semester 1, 2015
 * Group 4
 * Students: (Name, StudentNumber, Email)
 *          Bumsik Ahn, 621389, bahn@student.unimelb.edu.au
 *          Jiajie Li, 631482, jiajiel@student.unimelb.edu.au
 *          Fengmin Deng, 659332, dengf@student.unimelb.edu.au
 */

import com.android.volley.RequestQueue;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
    public final String TAG = MainActivity.class.getName();    

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

		Log.i(TAG,"game activity onCreate");
	}
	
	protected void onStart() {
		super.onStart();
		Log.i(TAG,"game activity onStart");
	}
	
	protected void onResume() {
		super.onResume();
		Log.i(TAG,"game activity onResume");
	}

    protected void onPause() {
    	super.onPause();
        Log.i(TAG,"game activity onPause");
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	RequestQueue queue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        if (queue != null) {
        	queue.cancelAll(TAG);
        }
        Log.i(TAG, "game activity onStop");
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sp != null) {
            sp.release();
        }
        Log.i(TAG,"game activity onDestroy");
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        Log.i(TAG,"game activity onSaveInstanceState");
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
    	transpentLayer.setVisibility(View.VISIBLE);
        prompt_message.setText(R.string.tip_ready);
        new CountDownTimer(Constants.GAME_COUNTDOWN, Constants.COUNTDOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
            	if (millisUntilFinished > Constants.COUNTDOWN_INTERVAL * 2) {
            		prompt_message.setText("" + (millisUntilFinished / 1000 - 1 ) + "");
            	} else {
                	prompt_message.setText(R.string.go);
            	}
            }
            public void onFinish() {
            	prompt_message.setVisibility(View.INVISIBLE);
                rData.setRunning(true);
                new Thread(worldView).start();
            }
         }.start();
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
        rData.setRivalName(null);
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
