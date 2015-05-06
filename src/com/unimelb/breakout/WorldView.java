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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

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
//	private volatile float ballInitX;
//	private volatile float ballInitY;
//	private volatile float ballInitXSpeed;
//	private volatile float ballInitYSpeed;
	private volatile SoundPool sp;
    private volatile int collideId;
	
	public WorldView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
		setFocusable(true);
		Log.i(TAG,"WorldView Constructor");
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (MeasureSpec.getSize(heightMeasureSpec) * Constants.GAMEVIEW_HEIGHT_FACTOR);
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        );
        //Log.i(TAG,"onMeasure");
    }
	
	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
	    mainActivity = (MainActivity) getContext();
	    gameViewWidth = getWidth();
	    gameViewHeight = getHeight();
        rData = mainActivity.getrData();
        rData.setGameViewWidth(gameViewWidth);
        rData.setGameViewHeight(gameViewHeight);
        sp = mainActivity.getSp();
        collideId = mainActivity.getCollideId();
        
        this.surfaceHolder = surfaceHolder;
        paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        initBallAndBar();
//        rData.getBricks().validate(gameViewWidth, rData.getMyBar().getBarY());
        drawGame();
        Log.d(TAG,"surfaceCreated");
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
		
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
	    rData.setRunning(false);
	    Utils.deActivateFromServer(mainActivity, rData.getMyName());
        Log.d(TAG, "surface destroyed!");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    Bar myBar = rData.getMyBar();
	    if (rData.isRunning()) {
	        if(event.getAction() == MotionEvent.ACTION_DOWN) {
	            myBar.setOldTouchX(event.getRawX() / gameViewWidth);
	            myBar.setPrevT(System.currentTimeMillis());
	            rData.setMyBar(myBar);
//	            Log.d(TAG, rData.getMyName() + " in bar action down");
	        }
	        if(event.getAction() == MotionEvent.ACTION_MOVE) {
	            float curX = event.getRawX() / gameViewWidth;
	            float dx =  curX - myBar.getOldTouchX();
	            long dt = System.currentTimeMillis() - myBar.getPrevT();
	            if (Math.abs(dx) > 0.01) {
	                if (lock.tryLock()) {
                        try {
                            myBar.move(dx, dt);
                            myBar.setOldTouchX(curX);
                            rData.setMyBar(myBar);
//                            Log.d(TAG, rData.getMyName() + " in bar action move");
//                            rData.setMyBarXSpeed(myBar.getBarXSpeed());
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
                    Thread.sleep(Constants.GAME_THREAD_SLEEP);
                }
        	} catch (Exception e) {
        	    
        	}
		}
	}

    private void drawGame() {
//    	Log.d(TAG, rData.getMyName() + " in drawGame");
	    mainActivity.showRuntimeData();
        Canvas canvas = null;
        Ball ball1 = rData.getBall1();
        Ball ball2 = rData.getBall2();
        Bar myBar = rData.getMyBar();
        Bar rivalBar = rData.getRivalBar();
        Bricks bricks = rData.getBricks();
//        OnPlayData onPlayData = new OnPlayData();
       
        synchronized(surfaceHolder) {
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                    canvas.drawPaint(paint);
                    bricks.onDraw(canvas, gameViewWidth, gameViewHeight);
                    myBar.onDraw(canvas, gameViewWidth, gameViewHeight);
                    rivalBar.onDraw(canvas, gameViewWidth, gameViewHeight);
                    ball1.onDraw(canvas, gameViewWidth, gameViewHeight);
                    ball2.onDraw(canvas, gameViewWidth, gameViewHeight);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
        
        try {
            lock.lock();
//            rData.setBall1XSpeed(ball1.getXSpeed());
//            rData.setBall1YSpeed(ball1.getYSpeed());
            //TODO!!!!!!!!!!!!
//            int score = onPlayData.getScore();
//            if (score > 0) {
//                sp.play(collideId, 0.5f, 0.5f, 0, 0, 1);
//                rData.setMyScore(rData.getMyScore() + score);
//            }
//            myBar.updateSpeed();
//            rData.setMyBarXSpeed(myBar.getBarXSpeed());
            ball1.moveBall();
            ball2.moveBall();
            if (!ball1.getOnPlayInfo().gameIsOn() || !ball2.getOnPlayInfo().gameIsOn()) { //either ball hit the ground, game over
            	rData.setRunning(false);
            	Log.d(TAG, "ball hit the ground");
            }
            if (ball1.getOnPlayInfo().isOwnershipChanged()) {
            	Log.d(TAG, "inform server to change ball 1 ownership to " + rData.getMyName());
            	writeChangedOwnership(ball1.getBallId(), rData.getMyName());
            }
            if (ball2.getOnPlayInfo().isOwnershipChanged()) {
            	Log.d(TAG, "inform server to change ball 2 ownership to " + rData.getMyName());
            	writeChangedOwnership(ball2.getBallId(), rData.getMyName());
            }
            
            
            
//            if (ball1.isOwned())
            rData.setBall1(ball1);
            rData.setBall2(ball2);
            if (bricks.isClear()) { //all bricks are gone
//                rData.setBall1XSpeed(0);
//                rData.setBall1YSpeed(0);
//                rData.setMyBarXSpeed(0);
                mainActivity.showRuntimeData();
                rData.setRunning(false);
                
                //TODO: Inform winning or game over
//                mainActivity.generateGameOverDialog();
            }   
        } finally {
            lock.unlock();
        }
	}
    
    private void writeChangedOwnership(int ballId, String myName) {
    	String url = Constants.SEVER_URL + "?command=write&owner_name=" + myName + "&ball_id=" + ballId;
    	Log.d(TAG, url);
    	StringRequest request = new StringRequest(Request.Method.GET, url,
    			new Response.Listener<String>() {
    	    @Override
    	    public void onResponse(String response) {
    	    }
    	}, new Response.ErrorListener() {
    	    @Override
    	    public void onErrorResponse(VolleyError error) {
    	    	error.printStackTrace();
    	    }
    	});
    	VolleySingleton.getInstance(mainActivity.getApplicationContext()).addToRequestQueue(request);
    }

    private void initBallAndBar() {
    	Bar myBar = new Bar(Constants.BAR_INIT_X_FACTOR, Constants.BAR_INIT_Y_FACTOR);
        Bar rivalBar = new Bar(Constants.BAR_INIT_X_FACTOR, Constants.OPPOSITE_BAR_Y_FACTOR);
    	rData.setMyBar(myBar);
        rData.setRivalBar(rivalBar);
        
        if (rData.getMapSide().equals("A")) { //I own ball1
        	Ball ball1 = new Ball(1, 
            		Constants.BALL_INIT_X_FACTOR,
            		Constants.BALL_INIT_Y_FACTOR, 
            		Constants.BALL_INIT_XSPEED_FACTOR,
            		Constants.BALL_INIT_YSPEED_FACTOR,
            		myBar, true);
            rData.setBall1(ball1);
            
            Ball ball2 = new Ball(2,
            		Constants.BALL_INIT_X_FACTOR,
            		1 - Constants.BALL_INIT_Y_FACTOR,
            		- Constants.BALL_INIT_XSPEED_FACTOR,
            		- Constants.BALL_INIT_YSPEED_FACTOR,
            		myBar, false);
            rData.setBall2(ball2);
        } else { // I own ball2
        	Ball ball1 = new Ball(1,
            		Constants.BALL_INIT_X_FACTOR,
            		1 - Constants.BALL_INIT_Y_FACTOR,
            		- Constants.BALL_INIT_XSPEED_FACTOR,
            		- Constants.BALL_INIT_YSPEED_FACTOR,
            		myBar, false);
            rData.setBall1(ball1);
            
            Ball ball2 = new Ball(2, 
            		Constants.BALL_INIT_X_FACTOR,
            		Constants.BALL_INIT_Y_FACTOR, 
            		Constants.BALL_INIT_XSPEED_FACTOR,
            		Constants.BALL_INIT_YSPEED_FACTOR,
            		myBar, true);
            rData.setBall2(ball2);
        }
        
//        ballInitX = Constants.BALL_INIT_X_FACTOR * gameViewWidth;
//    	ballInitY = Constants.BALL_INIT_Y_FACTOR * gameViewHeight;
//    	ballInitXSpeed = Constants.BALL_INIT_XSPEED_FACTOR * gameViewWidth;
//    	ballInitYSpeed = Constants.BALL_INIT_YSPEED_FACTOR * gameViewHeight;
//        rData.setBall1X(ballInitX);
//    	rData.setBall1Y(ballInitY);
//    	rData.setBall1XSpeed(ballInitXSpeed);
//    	rData.setBall1YSpeed(ballInitYSpeed);
//    	rData.setBall1Owned(isSideA);
//        rData.setBall2X(gameViewWidth - ballInitX);
//    	rData.setBall2Y(gameViewHeight - ballInitY);
//    	rData.setBall2XSpeed(-ballInitXSpeed);
//    	rData.setBall2YSpeed(-ballInitYSpeed);
//    	rData.setBall2Owned(!isSideA);
        
        Log.d(TAG, "initBallAndBar");
        Log.d(TAG, "Phone:" + rData.getMyName() + "; mapSide: " + rData.getMapSide());
    }

}
