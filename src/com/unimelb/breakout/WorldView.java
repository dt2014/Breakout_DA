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
		int timeToUpdate = 10;
		int counter = 0;
		while(rData.isRunning()) {
			try {
			    drawGame();
			    if (++counter == timeToUpdate) {
			    	counter = 0;
			    	update();
			    }
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
        String myName = rData.getMyName();
        String rivalName = rData.getRivalName();
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
	}
    
    private void update() {
    	Ball ball1 = rData.getBall1();
        Ball ball2 = rData.getBall2();
        Bar myBar = rData.getMyBar();
        Bar rivalBar = rData.getRivalBar();
        Bricks bricks = rData.getBricks();
        String myName = rData.getMyName();
        String rivalName = rData.getRivalName();
    	
    	try {
            lock.lock();
//            myBar.updateSpeed();
            writeMyBarPosition(myBar.getBarX(), myName);
            ball1.moveBall();
            ball2.moveBall();
            if (!ball1.getOnPlayInfo().gameIsOn() || !ball2.getOnPlayInfo().gameIsOn()) { //either ball hit the ground, game over
            	rData.setRunning(false);
            	mainActivity.generateGameOverDialog();
            	Log.d(TAG, "ball hit the ground");
            }
            if (ball1.getOnPlayInfo().isOwnershipChanged()) {
            	Log.d(TAG, "inform server to change ball 1 ownership to " + myName);
            	writeChangedOwnership(ball1.getBallId(), rData.getMyName());
            }
            if (ball2.getOnPlayInfo().isOwnershipChanged()) {
            	Log.d(TAG, "inform server to change ball 2 ownership to " + myName);
            	writeChangedOwnership(ball2.getBallId(), myName);
            }
            
            if (ball1.isOwned()) {
            	writeBallPositionAndSpeed(ball1);
            	int brickToDisappear1 = bricks.checkCollision(ball1, gameViewWidth, gameViewHeight);
            	if (brickToDisappear1 != 0 ) {
            		sp.play(collideId, 0.5f, 0.5f, 0, 0, 1);
            		writeHitBrick(brickToDisappear1, myName);
            		readScoresFromServer(myName, rivalName);
            	}
            }
            
            if (ball2.isOwned()) {
            	writeBallPositionAndSpeed(ball2);
            	int brickToDisappear2 = bricks.checkCollision(ball2, gameViewWidth, gameViewHeight);
            	if (brickToDisappear2 != 0 ) {
            		sp.play(collideId, 0.5f, 0.5f, 0, 0, 1);
            		writeHitBrick(brickToDisappear2, myName);
            		readScoresFromServer(myName, rivalName);
            	}
            }
            
            readBallPositionAndSpeed(ball1);
            rData.setBall1(ball1);
            readBallPositionAndSpeed(ball2);
            rData.setBall2(ball2);
            
            readRivalBarForMore(rivalBar);
            
            if (bricks.isClear()) { //all bricks are gone
                mainActivity.showRuntimeData();
                rData.setRunning(false);
                
                //TODO: Inform winning or game over
//                mainActivity.generateGameOverDialog();
            }   
        } finally {
            lock.unlock();
        }
    }
    
    
    /**************************** NETWORK CODE *******************************/   
    
    private void readRivalBarForMore(final Bar rivalBar) {
    	String url = Constants.SEVER_URL + "?command=read&player_name=" + rData.getRivalName();
    	Log.d(TAG, url);
    	JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
    			new Response.Listener<JSONObject>() {
    	    @Override
    	    public void onResponse(JSONObject response) {
    	    	try {
    	    		Log.d(TAG, response.toString());
    	    		String tag = response.getString("response");
    	    		if (tag.equals("bar") || tag.equals("more")) {
    	    			float rivalBarX = Float.valueOf(response.getString("bar_position_x"));
    	    			if (rData.getMapSide().equals("B")) {
    	    				rivalBarX = 1 - Constants.BAR_LENGTH_FACTOR - rivalBarX;
    	    			}
    	    			rivalBar.setBarX(rivalBarX);
    	                rData.setRivalBar(rivalBar);
    	    			if (tag.equals("more")) {
    	    				int brickId = Integer.valueOf(response.getString("brick_id"));
    	    				Bricks bricks = rData.getBricks();
    	    				bricks.setAlive(brickId, false);
    	    				rData.setBricks(bricks);
        	    		}
    	    		} else if (tag.equals("player_lost")) {
    	    			//TODO !!!! inform interruption
    	    			rData.setRunning(false);
    	    		}
    	        } catch (JSONException e) {
    	        	Log.d(TAG, "Something Wrong read bar for more !!!");
    	        }
            }
    	    }, new Response.ErrorListener() {
    	    @Override
    	    public void onErrorResponse(VolleyError error) {
    	    	error.printStackTrace();
    	    }
    	});
    	request.setTag(MainActivity.TAG);
    	VolleySingleton.getInstance(mainActivity.getApplicationContext()).addToRequestQueue(request);
    }
    
    private void writeMyBarPosition(float myBarX, String myName) {
    	if (rData.getMapSide().equals("B")) { //convert to the position in A side in server
    		myBarX = 1 - Constants.BAR_LENGTH_FACTOR - myBarX;
    	}
    	String url = Constants.SEVER_URL + "?command=write&player_name=" + myName + "&bar_position_x=" + myBarX;
    	writeWithNoResponse(url);
    }
    
    private void writeBallPositionAndSpeed(Ball ball) {
    	float ballX = ball.getX();
    	float ballY = ball.getY();
    	float ballXSpeed = ball.getXSpeed();
    	float ballYSpeed = ball.getYSpeed();
    	if (rData.getMapSide().equals("B")) { //convert to the position in A side in server
    		ballX = 1 - ballX;
    		ballY = 1 - ballY;
    		ballXSpeed = -ballXSpeed;
    		ballYSpeed = -ballYSpeed;
    	}
    	String url = Constants.SEVER_URL + "?command=write&ball_id=" + ball.getBallId() +
    		"&ball_position_x=" + ballX +
    		"&ball_position_y=" + ballY +
    		"&ball_speed_x=" + ballXSpeed +
    		"&ball_speed_y=" + ballYSpeed;
    	writeWithNoResponse(url);
    }
    
    private void readBallPositionAndSpeed(final Ball ball) {
    	String url = Constants.SEVER_URL + "?command=read&ball_id=" + ball.getBallId();
    	Log.d(TAG, url);
    	JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
    			new Response.Listener<JSONObject>() {
    	    @Override
    	    public void onResponse(JSONObject response) {
    	    	try {
    	    		Log.d(TAG, response.toString());
    	    		JSONObject position = response.getJSONObject("position");
    	    		float ballX = Float.valueOf(position.getString("ball_position_x"));
    	    		float ballY = Float.valueOf(position.getString("ball_position_y"));
    	    		JSONObject speed = response.getJSONObject("speed");
    	    		float ballXSpeed = Float.valueOf(speed.getString("ball_speed_x"));
    	    		float ballYSpeed = Float.valueOf(speed.getString("ball_speed_y"));
    	    		if (rData.getMapSide().equals("B")) { //convert to the position in B side locally
    	        		ballX = 1 - ballX;
    	        		ballY = 1 - ballY;
    	        		ballXSpeed = -ballXSpeed;
    	        		ballYSpeed = -ballYSpeed;
    	        	}
    	    		ball.setPositionAndSpeed(ballX, ballY, ballXSpeed, ballYSpeed);
    	    		if (response.getString("owner_name").equals(rData.getRivalName())) {
    	    			ball.setOwned(false);
    	    		}
    	        } catch (JSONException e) {
    	        	Log.d(TAG, "Something Wrong read ball " + ball.getBallId() + " details from server response");
    	        }
            }
    	    }, new Response.ErrorListener() {
    	    @Override
    	    public void onErrorResponse(VolleyError error) {
    	    	error.printStackTrace();
    	    }
    	});
    	request.setTag(MainActivity.TAG);
    	VolleySingleton.getInstance(mainActivity.getApplicationContext()).addToRequestQueue(request);
    }
    
    private void readScoresFromServer(String myName, String vivalName){
    	String url = Constants.SEVER_URL + "?command=read&player1_name=" + myName + "&player2_name=" + vivalName;
    	JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
    			new Response.Listener<JSONObject>() {
    	    @Override
    	    public void onResponse(JSONObject response) {
    	    	try {
    	    		Log.d(TAG, response.toString());
    	    		rData.setMyScore(Integer.valueOf(response.getString("player1Score")));
    	    		rData.setRivalScore(Integer.valueOf(response.getString("player2Score")));
    	        } catch (JSONException e) {
    	        	Log.d(TAG, "Something Wrong read scores from server response" + response.toString());
    	        }
            }
    	    }, new Response.ErrorListener() {
    	    @Override
    	    public void onErrorResponse(VolleyError error) {
    	    	error.printStackTrace();
    	    }
    	});
    	request.setTag(MainActivity.TAG);
    	VolleySingleton.getInstance(mainActivity.getApplicationContext()).addToRequestQueue(request);
    }

    private void writeHitBrick(final int brickId, final String myName) {
    	String url = Constants.SEVER_URL + "?command=write&player_name=" + myName + "&brick_id=" + brickId;
    	JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
    			new Response.Listener<JSONObject>() {
    	    @Override
    	    public void onResponse(JSONObject response) {
    	    	try {
    	    		Log.d(TAG, response.toString());
    	            if (response.getString("Info").equals("Brick Eliminate Success")) {
	    				Bricks bricks = rData.getBricks();
	    				bricks.setAlive(brickId, false);
	    				rData.setBricks(bricks);
    	                Log.d(TAG, "brick no. " + brickId + " was eliminated by " + myName);
    	            }
    	        } catch (JSONException e) {
    	        	Log.d(TAG, "Something Wrong write hit brick response" + response.toString());
    	        }
            }
    	    }, new Response.ErrorListener() {
    	    @Override
    	    public void onErrorResponse(VolleyError error) {
    	    	error.printStackTrace();
    	    }
    	});
    	request.setTag(MainActivity.TAG);
    	VolleySingleton.getInstance(mainActivity.getApplicationContext()).addToRequestQueue(request);
    }
    
    private void writeChangedOwnership(int ballId, String myName) {
    	String url = Constants.SEVER_URL + "?command=write&owner_name=" + myName + "&ball_id=" + ballId;
    	writeWithNoResponse(url);
    }
    
    private void writeWithNoResponse(String url) {
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
    	request.setTag(MainActivity.TAG);
    	VolleySingleton.getInstance(mainActivity.getApplicationContext()).addToRequestQueue(request);
    }
    
    /**************************** NETWORK CODE *******************************/
    

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
        Log.d(TAG, "initBallAndBar");
        Log.d(TAG, "Phone:" + rData.getMyName() + "; mapSide: " + rData.getMapSide());
    }

}
