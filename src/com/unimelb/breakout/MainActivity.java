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
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();    

    private volatile RuntimeData rData;
	private WorldView worldView;
    private TextView score;
    private TextView lives;
    private TextView level;
    private TextView rank;
    private TextView next;
    private TextView name;
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
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Force device to stay in portrait orientation
		//requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove banner from the top of the activity
		setContentView(R.layout.activity_main); //Set the layout to activity_main
		
		if (rData.getLives() > 0) 
            displayReadyScreen();
        
        score = (TextView)findViewById(R.id.val_score);
        lives = (TextView)findViewById(R.id.val_lives);
        level = (TextView)findViewById(R.id.val_level);
        rank = (TextView)findViewById(R.id.val_rank);
        next = (TextView)findViewById(R.id.val_next);
        name = (TextView)findViewById(R.id.val_player);     
        
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		collideId = sp.load(this, R.raw.collide, 1);
		
		ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);
		pauseButton.setImageResource(R.drawable.pause);
		
//		ImageButton musicButton = (ImageButton) findViewById(R.id.musicButton);
//		musicButton.setImageResource(R.drawable.music_on);
//		Intent intent = new Intent(this,BGM.class);  
//		startService(intent);
		Log.d(TAG,"game view onCreate");
	}
	
	protected void onStart() {
		super.onStart();
		//Log.i(TAG,"this is onStart");
	}
	
	protected void onResume() {
		//Intent intent = new Intent(this,BGM.class);
		//startService(intent);
		super.onResume();
		Log.i(TAG,"game view onResume");
	}

    protected void onPause() {
        worldView.saveRuntimeData();
        SharedPreferences sharedPref = getSharedPreferences(MenuActivity.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SAVED.LEVEL", rData.getLevel());
        editor.putInt("SAVED.SCORE", rData.getScore());
        editor.putInt("SAVED.LIVES", rData.getLives());
        editor.putString("SAVED.NAME", rData.getName());
        editor.putBoolean("SAVED.UPLOADED", rData.isUploaded());
        editor.commit();
        super.onPause();
        Log.i(TAG,"game play onPause");
    }
    
	protected void onStop() {
	    /*Intent intent = new Intent(this,BGM.class);  
	    stopService(intent);*/
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
    
    public void clickPauseOrResume(View view) {
    	worldView.setPause(!worldView.getPause());
    	ImageButton pauseButton =(ImageButton) findViewById(R.id.pauseButton);
    	if (worldView.getPause() == true) { // game is running
    	    pauseButton.setImageResource(R.drawable.resume);
    	    rData.setRunning(false);
    	} else {
    	    pauseButton.setImageResource(R.drawable.pause);
    	    rData.setRunning(true);
            new Thread(worldView).start();
    	}
    	/*if(worldView.pause==false) {
    		pauseButton.setImageResource(R.drawable.pause);
    	    Intent intent = new Intent(this,BGM.class);  
    	    startService(intent);  
    	}
    	else {
    		pauseButton.setImageResource(R.drawable.resume);//the button become resume
    	    Intent intent = new Intent(this,BGM.class);  
    	    stopService(intent); 
    	}*/
    }
    
//    public void clickMusic(View view) {
//        ImageButton musicButton = (ImageButton) findViewById(R.id.musicButton);
//        musicButton.setImageResource(R.drawable.music_off);
//    }
    
    public void showRuntimeData(){
        this.runOnUiThread(new Runnable() {     
            public void run() {
                score.setText(String.valueOf(rData.getScore()));
                lives.setText(String.valueOf(rData.getLives()));
                level.setText(String.valueOf(rData.getLevel()));
                name.setText(Utils.nameInitials(rData.getName()));
                int curRank = rData.getRank();
                if (curRank < 0) {
                    rank.setText(R.string.txt_na);
                } else {
                    rank.setText(String.valueOf(curRank));
                }
                int curNext = rData.getNext();
                if (curNext < 0) {
                    next.setText(R.string.txt_na);
                } else {
                    next.setText(String.valueOf(curNext));
                }
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
    
    public void askForEntry(final int score, final String username){
        this.runOnUiThread(new Runnable() {     
            public void run() {
                AlertDialog.Builder builder = new Builder(MainActivity.this);
                builder.setTitle(R.string.title_score_entry);
                builder.setMessage(R.string.msg_score_entry);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        String scoreURL = "http://128.199.134.230/updateScore.php?score="+score+"&username="+username;
                        final UpdateScoreTask uploadScoreTask = new UpdateScoreTask(MainActivity.this);
                        uploadScoreTask.execute(scoreURL);
                        /*new Thread() {
                            @Override
                            public void run() {
                                try {
                                    uploadScoreTask.get(5000, TimeUnit.MILLISECONDS);
                                } catch (Exception e) {
                                    uploadScoreTask.cancel(true);
                                    MainActivity.this.runOnUiThread(new Runnable(){
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "Error occurred when uploading high score, please try again later.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                             }
                        }.start();*/
                    }
                });  
                builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearScoreAndSetUploaded();
                        MainActivity.this.finish();
                    }
                });
                builder.create().show();
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
                        clearScoreAndSetUploaded();
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
    
    public void clearScoreAndSetUploaded() {
        rData.setScore(0);
        rData.setUploaded(true);
        SharedPreferences sharedPref = getSharedPreferences(MenuActivity.PREF, 
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SAVED.SCORE", rData.getScore());
        editor.putBoolean("SAVED.UPLOADED", rData.isUploaded());
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
