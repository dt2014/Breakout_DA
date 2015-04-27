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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
	private volatile float initBallX;
	private volatile float initBallY;
	private volatile float initBallXSpeed;
	private volatile float initBallYSpeed;
	private volatile SoundPool sp;
    private volatile int collideId;
	
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
        int height = (int) (MeasureSpec.getSize(heightMeasureSpec) * 0.8);
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
        initBallX = rData.getInitBallX() * gameViewWidth;
        initBallY = rData.getInitBallY() * gameViewHeight;
        initBallXSpeed = rData.getInitBallXSpeed() * gameViewWidth;
        initBallYSpeed = rData.getInitBallYSpeed() * gameViewHeight;
        sp = mainActivity.getSp();
        collideId = mainActivity.getCollideId();
        
        this.surfaceHolder = surfaceHolder;
        paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        initBallAndBar();
        rData.getBricks().validate(gameViewWidth, rData.getMyBar().getY());
        drawGame();
        Log.d(TAG,"surfaceCreated");
	}

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
            editor.putFloat("SAVED.BARX", bar.getX());
            editor.putFloat("SAVED.BARY", bar.getY());
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
    	float initBarX = (1 - Bar.lengthFactor) * gameViewWidth / 2;
        Bar myBar = new Bar(initBarX, gameViewHeight * 0.95F, gameViewWidth, gameViewHeight);
        Ball ball1 = new Ball(initBallX, initBallY, 
        		initBallXSpeed, initBallYSpeed, gameViewWidth, gameViewHeight, myBar);
        rData.setMyBar(myBar);
        rData.setBall1(ball1);
        Bar rivalBar = new Bar(initBarX, gameViewHeight * (0.05F - Bar.heightFactor), gameViewWidth, gameViewHeight);
        Ball ball2 = new Ball(gameViewWidth - initBallX, gameViewHeight - initBallY, 
        		-initBallXSpeed, -initBallYSpeed, gameViewWidth, gameViewHeight, rivalBar);
        rData.setRivalBar(rivalBar);
        rData.setBall2(ball2);
        Log.d(TAG, "initBallAndBar");
    }

}
