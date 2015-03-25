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
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class WorldView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = WorldView.class.getName();
    
	private SurfaceHolder surfaceHolder;
	private volatile RuntimeData rData;
	private volatile Paint paint;
	private final Lock lock = new ReentrantLock();
	private volatile int gameViewWidth;
	private volatile int gameViewHeight;
	private MainActivity mainActivity;
	private volatile float initBallx;
	private volatile float initBally;
	private volatile float initBallXSpeed;
	private volatile float initBallYSpeed;
	private volatile float BarLengthFactor;
	private volatile SoundPool sp;
    private volatile int collideId;
    private volatile boolean pause;
	
	/*public static boolean pause = false;
	public boolean connected = false;*/
	
	public WorldView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
		setFocusable(true);
		Log.i(TAG,"WorldView Constructor");
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > height) {
            width = height;
        } else {
            height = width;
        }
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        );
        //Log.i(TAG,"onMeasure");
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    /* 
	     if(pause==true) 
            return true;
	    */
	    Bar bar = rData.getBar();
	    if (rData.isRunning()) {
	        if(event.getAction() == MotionEvent.ACTION_DOWN) {
	            bar.setOldTouchX(event.getRawX());
	            rData.getBar().setPrevT(System.currentTimeMillis());
	        }
	        if(event.getAction() == MotionEvent.ACTION_MOVE) {
	            float curX = event.getRawX();
	            float dx =  curX - bar.getOldTouchX();
	            long dt = System.currentTimeMillis() - bar.getPrevT();
	            if (Math.abs(dx) > 10) {
	                if (lock.tryLock()) {
                        try {
                            bar.move(dx, dt);
                            bar.setOldTouchX(curX);
                            rData.setBarXSpeed(bar.getBarXSpeed());
                        } finally {
                            lock.unlock();
                        }
                    } 
	            }
	        }
	    }
        return true;
    }

	public void run() {
		while(rData.isRunning()) {
			try {
			    drawGame();
			    if (!rData.isRunning()) {
                    return;
                } else {
                    Thread.sleep(5);
                }
        	} catch (Exception e) {
        	    
        	}
		}
	}
	
	private void drawGame() {
	    mainActivity.showRuntimeData();
        Canvas canvas = null;
        Ball ball = rData.getBall();
        Bar bar = rData.getBar();
        Bricks bricks = rData.getBricks();
        bricks.setViewSize(gameViewWidth, gameViewHeight);
        OnPlayData onPlayData = new OnPlayData();
       
        synchronized(surfaceHolder) {
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                    canvas.drawPaint(paint);
                    onPlayData = bricks.onDraw(canvas, ball);
                    bar.onDraw(canvas);
                    ball.onDraw(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
        
        try {
            lock.lock();
            rData.setBallXSpeed(ball.getXSpeed());
            rData.setBallYSpeed(ball.getYSpeed());
            int score = onPlayData.getScore();
            if (score > 0) {
                sp.play(collideId, 0.5f, 0.5f, 0, 0, 1);
                rData.setScore(rData.getScore() + score);
                Utils.insert(rData);
            }
            rData.setLives(rData.getLives() + onPlayData.getLives());
            int newLive = 0;
            bar.updateSpeed();
            rData.setBarXSpeed(bar.getBarXSpeed());
            ball.moveBall();
            newLive = ball.getOnPlayInfo().getLives();
            rData.setLives(rData.getLives() + newLive);
            if (newLive < 0 || onPlayData.isClear()) { //one life lost or all bricks are gone
                rData.setBallXSpeed(0);
                rData.setBallYSpeed(0);
                rData.setBarXSpeed(0);
                mainActivity.showRuntimeData();
                rData.setRunning(false);
                if (rData.getLives() == 0) { // no more lives
                    if (isInTopTen(rData.getScore())) {
                        // Prompt to ask player if an entry is requested
                        mainActivity.askForEntry(rData.getScore(), rData.getName());
                    } else {
                        mainActivity.generateGameOverDialog();
                    }
                } else if (onPlayData.isClear()) { // bricks are gone
                    if (rData.getLevel() == rData.getTotalLevels()) {
                        // Update the final score with remaining livers
                        int finalScore = rData.getScore() + 1000 * rData.getLives();
                        rData.setScore(finalScore);
                        if (isInTopTen(finalScore)) {
                            mainActivity.askForEntry(rData.getScore(), rData.getName());
                        } else {
                            mainActivity.generateGameOverDialog();
                        }
                    }
                    if (rData.getLevel() < rData.getTotalLevels()) {
                        rData.setLevel(rData.getLevel() + 1);
                        switchLevel(rData.getLevel());
                    }
                } else { // continue playing with the remaining lives
                    initBallAndBar();
                    mainActivity.runOnUiThread(new Runnable() {     
                        public void run() {
                            mainActivity.displayReadyScreen();
                            drawGame();
                        }});
                }
            }   
        } finally {
            lock.unlock();
        }
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
	    mainActivity = (MainActivity) getContext();
	    gameViewWidth = getWidth();
	    gameViewHeight = getHeight();
        rData = mainActivity.getrData();
        rData.setGameViewWidth(gameViewWidth);
        rData.setGameViewHeight(gameViewHeight);
        initBallx = rData.getInitballx() * gameViewWidth;
        initBally = rData.getInitbally() * gameViewHeight;
        initBallXSpeed = rData.getInitballXSpeed() * gameViewWidth;
        initBallYSpeed = rData.getInitballYSpeed() * gameViewHeight;
        BarLengthFactor = rData.getBarLengthFactor();
        sp = mainActivity.getSp();
        collideId = mainActivity.getCollideId();
        if (rData.getLives() > 0) {
            this.surfaceHolder = surfaceHolder;
            paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            if (rData.isNewGame()) {
                Log.d(TAG, "worldView surfaceCreated: isNewGame");
                initBallAndBar();
                rData.getBricks().validate(gameViewWidth, rData.getBar().getY());
                rData.setNewGame(false);
            } else {
                rData.getBricks().countBricks(gameViewWidth, rData.getBar().getY());
            }
            drawGame();
        }
        Log.d(TAG,"surfaceCreated");
        
//		ball = new Ball(this, null, width, height);
//		ball.setXSpeed(speed-5);
//		ball.setYSpeed(speed);
//		bar = new Bar(this, null, width,height);
//		bricks = new Bricks(this);
//		checkIfNoLevel();
		//Here we can assign a string calling FromJSON to the following variable bricks,
		//so that FromJSON should turn string to a object of type Bricks.
		/**Fot testing fromJSON and toJSON
		String map = bricks.ToJSON();
		Log.i(TAG,map);
		Bricks bricksNew = new Bricks(this);
		bricksNew.FromJSON(map);
		String mapNew = bricksNew.ToJSON();
		Log.i(TAG,mapNew);
		bricks = bricksNew;
		**/
	}
/*
	private void checkIfNoLevel() {
		//if no level-1 create one.
		String levelFileName = "Level-1.map";
	    File levelFile = new File("/sdcard/Breakout/Maps/" + levelFileName);
	    if(!levelFile.exists())
	    {
	    	try {
	    	Bricks firstLevel = new Bricks(this,20);
	    	String L1 = firstLevel.ToJSON();
	    	FileOutputStream fos = new FileOutputStream(levelFile);
			fos.write(L1.getBytes());
			fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    //end of create level-1
	}*/

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
	    saveRuntimeData();
        Log.d(TAG, "surface destroyed!");
	}

	public void saveRuntimeData() {
	    rData.setRunning(false);
        SharedPreferences sharedPref = mainActivity.
                getSharedPreferences(MenuActivity.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SAVED.WIDTH", rData.getGameViewWidth());
        editor.putInt("SAVED.HEIGHT", rData.getGameViewHeight());
        editor.putInt("SAVED.SCORE", rData.getScore());
        editor.putInt("SAVED.LIVES", rData.getLives());
        editor.putString("SAVED.NAME", rData.getName());
        editor.putBoolean("SAVED.UPLOADED", rData.isUploaded());
        if (rData.getBricks() == null || rData.getBricks().getAliveBrickCount() == 0) {
            editor.putString("SAVED.BRICKS", null);
        } else {
            Ball ball = rData.getBall();
            Bar bar = rData.getBar();
            editor.putFloat("SAVED.BALLX", ball.getX());
            editor.putFloat("SAVED.BALLY", ball.getY());
            editor.putFloat("SAVED.SPEEDX", ball.getXSpeed());
            editor.putFloat("SAVED.SPEEDY", ball.getYSpeed());
            editor.putFloat("SAVED.BARX", bar.getX());
            editor.putFloat("SAVED.BARY", bar.getY());
            editor.putFloat("SAVED.BARXSPEED", bar.getBarXSpeed());
            String json = Utils.saveBricks(rData.getBricks());
            editor.putString("SAVED.BRICKS", json);
            editor.putFloat("SAVED.INITBALLX", rData.getInitballx());
            editor.putFloat("SAVED.INITBALLY", rData.getInitbally());
            editor.putFloat("SAVED.INITSPEEDX", rData.getInitballXSpeed());
            editor.putFloat("SAVED.INITSPEEDY", rData.getInitballYSpeed());
            editor.putFloat("SAVED.BARLENGTHFACTOR", rData.getBarLengthFactor());
        }
        editor.commit();
	}
    public void switchLevel(int level) {
    	//this.level = level;
//    	String path = Environment.getExternalStorageDirectory().getPath() + "Breakout/Maps/";
        String levelFileName = "level."+ level +".map";
    	String path = getContext().getFilesDir().getPath();
	    File levelFile = new File(path + levelFileName);
	    if(levelFile.exists()) {
//	    	 switchExistLevel(levelFile);
	        mainActivity.runOnUiThread(new Runnable() {     
	            public void run() {
	                final LoadFilesTask task = new LoadFilesTask(mainActivity);
	                task.execute(rData);
	            }});
//	        new Thread() {
//          @Override
//          public void run() {
//              try {
//                  task.get(5000, TimeUnit.MILLISECONDS);
//              } catch (Exception e) {
//                  task.cancel(true);
//                  task.showError();
//              }
//          }
//      }.start();
	    }
	    //else ask the user to download the new maps./
	    else {  
	    	switchNewDownLevel(levelFile);
	    }
    }

    private void switchNewDownLevel(File levelFile) {
        mainActivity.runOnUiThread(new Runnable() {   // Use the context here
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);  
                builder.setTitle("Info");
                builder.setMessage("Do you want to download new level from the server?");
                builder.setCancelable(false);
                DownLoadLevelListener positiveButton = new DownLoadLevelListener(true, rData.getLevel());
                DownLoadLevelListener negativeButton = new DownLoadLevelListener(false, rData.getLevel());
                
                builder.setPositiveButton("Yes", positiveButton); 
                builder.setNegativeButton("No, maybe later", negativeButton);  
                
                positiveButton.setContext(mainActivity);//pass the context to the AsyncTask updataLevelActivity
                builder.show();
            }
        });
    }    

//	private void switchExistLevel(File levelFile) {
    	/*try {
    		BufferedReader br = new BufferedReader(new FileReader(levelFile));
    		String theBricks = br.readLine();
    		bricks.FromJSON(theBricks);
    		//ball.resetCoordsAndSpeed();
    		bar.resetCoords();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}*/
//	}

    private boolean isInTopTen(int scores) {
        List<RuntimeData> highScoreList = rData.getRecords();
        if (highScoreList != null) {
            for (int i = 0; i < highScoreList.size(); i++) {
                if (scores > highScoreList.get(i).getScore()) {
                    return true;
                }
            }
            return false;
        } else
            return true;
    }

    private void initBallAndBar() {
        Bar bar = new Bar((1-BarLengthFactor)*gameViewWidth/2, gameViewHeight*0.9F, 
                BarLengthFactor, gameViewWidth, gameViewHeight);
        rData.setBar(bar);
        rData.setBall(new Ball(initBallx, initBally,
                initBallXSpeed, initBallYSpeed, gameViewWidth, gameViewHeight, bar));
        Log.d(TAG, "initBallAndBar");
    }

    public boolean getPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }
}
