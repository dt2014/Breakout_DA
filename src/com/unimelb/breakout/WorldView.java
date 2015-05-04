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
	private volatile float ballInitX;
	private volatile float ballInitY;
	private volatile float ballInitXSpeed;
	private volatile float ballInitYSpeed;
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
        int height = (int) (MeasureSpec.getSize(heightMeasureSpec) * 0.85);
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        );
        //Log.i(TAG,"onMeasure");
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    Bar bar = rData.getMyBar();
	    if (rData.isRunning()) {
	        if(event.getAction() == MotionEvent.ACTION_DOWN) {
	            bar.setOldTouchX(event.getRawX());
	            rData.getMyBar().setPrevT(System.currentTimeMillis());
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
                            rData.setMyBarXSpeed(bar.getBarXSpeed());
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
        Ball ball1 = rData.getBall1();
        Ball ball2 = rData.getBall2();
        Bar myBar = rData.getMyBar();
        Bar rivalBar = rData.getRivalBar();
        Bricks bricks = rData.getBricks();
        bricks.setViewSize(gameViewWidth, gameViewHeight);
        OnPlayData onPlayData = new OnPlayData();
       
        synchronized(surfaceHolder) {
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                    canvas.drawPaint(paint);
                    onPlayData = bricks.onDraw(canvas, ball1);
                    myBar.onDraw(canvas);
                    ball1.onDraw(canvas);
                    rivalBar.onDraw(canvas);
                    ball2.onDraw(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
        
        try {
            lock.lock();
            rData.setBall1XSpeed(ball1.getXSpeed());
            rData.setBall1YSpeed(ball1.getYSpeed());
            int score = onPlayData.getScore();
            if (score > 0) {
                sp.play(collideId, 0.5f, 0.5f, 0, 0, 1);
                rData.setMyScore(rData.getMyScore() + score);
            }
            myBar.updateSpeed();
            rData.setMyBarXSpeed(myBar.getBarXSpeed());
            ball1.moveBall();
            if (onPlayData.isClear()) { //all bricks are gone
                rData.setBall1XSpeed(0);
                rData.setBall1YSpeed(0);
                rData.setMyBarXSpeed(0);
                mainActivity.showRuntimeData();
                rData.setRunning(false);
                
                //TODO: Inform winning or game over
                mainActivity.generateGameOverDialog();
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
        ballInitX = rData.getInitBallX() * gameViewWidth;
        ballInitY = rData.getInitBallY() * gameViewHeight;
        ballInitXSpeed = rData.getInitBallXSpeed() * gameViewWidth;
        ballInitYSpeed = rData.getInitBallYSpeed() * gameViewHeight;
        sp = mainActivity.getSp();
        collideId = mainActivity.getCollideId();
        
        this.surfaceHolder = surfaceHolder;
        paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        initBallAndBar();
        rData.getBricks().validate(gameViewWidth, rData.getMyBar().getBarY());
        drawGame();
        Log.d(TAG,"surfaceCreated");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
	    saveRuntimeData();
	    String url = Constants.SEVER_URL + "?command=stop&player_name=" + rData.getMyName();
    	Log.i(TAG, url);
    	StringRequest stopGameRequest = new StringRequest(Request.Method.GET, url,
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
    	stopGameRequest.setTag(TAG);
    	VolleySingleton.getInstance(mainActivity.getApplicationContext()).addToRequestQueue(stopGameRequest);
        Log.d(TAG, "surface destroyed!");
	}

	public void saveRuntimeData() {
	    rData.setRunning(false);
        SharedPreferences sharedPref = mainActivity.
                getSharedPreferences(MenuActivity.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SAVED.WIDTH", rData.getGameViewWidth());
        editor.putInt("SAVED.HEIGHT", rData.getGameViewHeight());
        editor.putString("SAVED.MyNAME", rData.getMyName());
        editor.putString("SAVED.RIVALNAME", rData.getRivalName());
        editor.putInt("SAVED.MYSCORE", rData.getMyScore());
        editor.putInt("SAVED.RIVALSCORE", rData.getRivalScore());
        if (rData.getBricks() == null || rData.getBricks().getAliveBrickCount() == 0) {
            editor.putString("SAVED.BRICKS", null);
        } else {
            Ball ball = rData.getBall1();
            Bar bar = rData.getMyBar();
            editor.putFloat("SAVED.BALLX", ball.getX());
            editor.putFloat("SAVED.BALLY", ball.getY());
            editor.putFloat("SAVED.SPEEDX", ball.getXSpeed());
            editor.putFloat("SAVED.SPEEDY", ball.getYSpeed());
            editor.putFloat("SAVED.BARX", bar.getBarX());
            editor.putFloat("SAVED.BARY", bar.getBarY());
            editor.putFloat("SAVED.BARXSPEED", bar.getBarXSpeed());
            String json = Utils.saveBricks(rData.getBricks());
            editor.putString("SAVED.BRICKS", json);
            editor.putFloat("SAVED.INITBALLX", rData.getInitBallX());
            editor.putFloat("SAVED.INITBALLY", rData.getInitBallY());
            editor.putFloat("SAVED.INITSPEEDX", rData.getInitBallXSpeed());
            editor.putFloat("SAVED.INITSPEEDY", rData.getInitBallYSpeed());
        }
        editor.commit();
	}

    private void initBallAndBar() {
    	float barInitX = Constants.BAR_INIT_X_FACTOR * gameViewWidth;
    	float myBarY = Constants.MY_BAR_Y_FACTOR * gameViewHeight;
    	float rivalBarY = Constants.RIVAL_BAR_Y_FACTOR * gameViewHeight;
    	ballInitX = Constants.BALL_INIT_X_FACTOR * gameViewWidth;
    	ballInitY = Constants.BALL_INIT_Y_FACTOR * gameViewHeight;
    	ballInitXSpeed = Constants.BALL_INIT_XSPEED_FACTOR * gameViewWidth;
    	ballInitYSpeed = Constants.BALL_INIT_YSPEED_FACTOR * gameViewHeight;
    	
        Bar myBar = new Bar(barInitX, myBarY, gameViewWidth, gameViewHeight);
        rData.setMyBar(myBar);
        Ball ball1 = new Ball(ballInitX, ballInitY, ballInitXSpeed, ballInitYSpeed, gameViewWidth, gameViewHeight, myBar, true);
        rData.setBall1(ball1);
        
        Bar rivalBar = new Bar(barInitX, rivalBarY, gameViewWidth, gameViewHeight);
        Ball ball2 = new Ball(gameViewWidth - ballInitX, gameViewHeight - ballInitY, -ballInitXSpeed, -ballInitYSpeed, gameViewWidth, gameViewHeight, myBar, false);
        rData.setRivalBar(rivalBar);
        rData.setBall2(ball2);
        Log.d(TAG, "initBallAndBar");
    }

}
