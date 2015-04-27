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

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity{
    private static final String TAG = MenuActivity.class.getName();
    public static final String PREF = "Breakout_Preferences";
    public static final int MENU_ACTIVITY = 1;
    private volatile RuntimeData rData;
    private volatile TextView welcome;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_menu);

        if (savedInstanceState != null) {
            rData = (RuntimeData) savedInstanceState.getSerializable("RUNTIME.DATA");
        }
        if (rData == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                rData = (RuntimeData) extras.getSerializable("RUNTIME.DATA");
            }
            if (rData == null) {// when playing the game from the start
                rData = new RuntimeData();
                setSavedRuntimeData();
            }
        }
//        downloadTopTenRecords();
        
        welcome = (TextView)findViewById(R.id.welcome);
        if (rData.getMyName() != null) {
            welcome.setText("Welcome " + Utils.nameInitials(rData.getMyName()));
        }
	}
	
	public void clickNewPlayer(View view) {
		callActivityForResult(PlayerActivity.class);
    }
    
    public void clickStartNewGame(View view) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setCancelable(false);
        
        if (rData.getMyName() == null) {
            builder.setMessage(R.string.no_player);
            builder.setPositiveButton(R.string.lbl_goto_player, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callActivityForResult(PlayerActivity.class);
                }
            });
            builder.setNegativeButton(R.string.lbl_cancel, null);
            builder.create().show();
        } else {
            initNewGame();
            loadFilesToGame();
        }
    }
    
    public void clickHelp(View view) {
        Intent intent = new Intent(this, HelpDisplayActivity.class);
        startActivity(intent);
  	}

  	public void clickExit(View view) {
  		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MenuActivity.this);
  		// set title
  		alertDialogBuilder.setTitle("Exit");
  		// set dialog message
  		alertDialogBuilder.setMessage("Are you sure?")
  						  .setCancelable(false)
  					       .setPositiveButton
  					       ("Yes",new DialogInterface.OnClickListener() {
  					    	   public void onClick(DialogInterface dialog,int id) {
  					    		   // if this button is clicked, close
  					    		   // current activity
  					    		   MenuActivity.this.finish();
  					    	   }
  					       	}
  					       )
  					       .setNegativeButton
  					       ("No",new DialogInterface.OnClickListener() {
  					    	   public void onClick(DialogInterface dialog,int id) {
  					    		   // if this button is clicked, just close
  					    		   // the dialog box and do nothing
  					    		   dialog.cancel();
  					    	   }
  					       	}
  					       );
  	 // create alert dialog
  	 AlertDialog alertDialog = alertDialogBuilder.create();
  	 // show it
  	 alertDialog.show();		
  	}
  	
  	 protected void onResume() {
  	     setSavedRuntimeData();
         super.onResume();
         Log.i(TAG,"menu onResume");
     }

    protected void onPause() {
        Log.i(TAG,"menu onPause");
        super.onPause();
    }
    
    private void loadFilesToGame() {
        final LoadFilesTask task = new LoadFilesTask(this);
        task.execute(rData);
        new Thread() {
            @Override
            public void run() {
                try {
                    task.get(5000, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    task.cancel(true);
                    task.showError();
                }
            }
        }.start();
    }
    
    public void callActivityForResult(Class<?> activityClass) {
        Bundle extras = new Bundle();
        extras.putSerializable("RUNTIME.DATA", rData);
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        intent.putExtras(extras);
        startActivityForResult(intent, MENU_ACTIVITY);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode) {
        case MENU_ACTIVITY: 
            if (resultCode == RESULT_OK) {
                Bundle extras = intent.getExtras();
                rData = (RuntimeData) extras.getSerializable("RUNTIME.DATA");
                break;
            }
        }
        if (rData.getMyName() != null) {
            welcome.setText("Welcome " + Utils.nameInitials(rData.getMyName()));
        }
        Log.d(TAG, "menu: onActivityResult");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void setSavedRuntimeData() {
        try {
            SharedPreferences sharedPref = getSharedPreferences(PREF, Context.MODE_PRIVATE);
            String myName = sharedPref.getString("SAVED.MYNAME", null);
            String rivalName = sharedPref.getString("SAVED.RIVALNAME", null);
            int myScore = sharedPref.getInt("SAVED.MYSCORE", 0);
            int rivalScore = sharedPref.getInt("SAVED.RIVALSCORE", 0);
            rData.setMyName(myName);
            rData.setRivalName(rivalName);
            rData.setMyScore(myScore);
            rData.setRivalScore(rivalScore);
            String json = sharedPref.getString("SAVED.BRICKS", null);
            if (json != null) {
                //Log.d(TAG, "json");
                int gameViewWidth = sharedPref.getInt("SAVED.WIDTH", 0);
                int gameViewHeight = sharedPref.getInt("SAVED.HEIGHT", 0);
                float initballx = sharedPref.getFloat("SAVED.INITBALLX", 0.0F);
                float initbally = sharedPref.getFloat("SAVED.INITBALLY", 0.0F);
                float initspeedx = sharedPref.getFloat("SAVED.INITSPEEDX", 0.0F);
                float initspeedy = sharedPref.getFloat("SAVED.INITSPEEDY", 0.0F);
                float barlengthfactor = sharedPref.getFloat("SAVED.BARLENGTHFACTOR", 0.0F);

                float ballx = sharedPref.getFloat("SAVED.BALLX", 0.0F);
                float bally = sharedPref.getFloat("SAVED.BALLY", 0.0F);
                float speedx = sharedPref.getFloat("SAVED.SPEEDX", 0.0F);
                float speedy = sharedPref.getFloat("SAVED.SPEEDY", 0.0F);
                float barx = sharedPref.getFloat("SAVED.BARX", 0.0F);
                float bary = sharedPref.getFloat("SAVED.BARY", 0.0F);
                float barXSpeed = sharedPref.getFloat("SAVED.BARXSPEED", 0.0F);
                List<Brick> brickData = Utils.buildBricks(json);
                Bricks bricks = new Bricks(gameViewWidth, gameViewHeight);
                bricks.initBricks(brickData);
                bricks.setViewSize(gameViewWidth, gameViewHeight);
                rData.setBricks(bricks);
                Bar bar= new Bar(barx, bary, barlengthfactor, gameViewWidth, gameViewHeight);
                rData.setBar(bar);
                Ball ball = new Ball(ballx, bally, speedx, speedy, 
                        gameViewWidth, gameViewHeight, bar);
                rData.setInitballx(initballx);
                rData.setInitbally(initbally);
                rData.setInitballXSpeed(initspeedx);
                rData.setInitballYSpeed(initspeedy);
                rData.setBarLengthFactor(barlengthfactor);
                rData.setBar(bar);
                rData.setBall(ball);
                rData.setBallXSpeed(speedx);
                rData.setBallYSpeed(speedy);
                rData.setBarXSpeed(barXSpeed);
                
            }
        } catch (Exception e) {
            Log.d(TAG, "something wrong!!!!!!!!!!");
            e.printStackTrace();
        }
    }
    
    private void initNewGame() {
    	rData.setMyScore(0);
    	rData.setRivalScore(0);
    }
    
    public RuntimeData getrData() {
        return rData;
    }
}
