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
import android.annotation.SuppressLint;
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
	private volatile Paint paint;
	private final Lock lock = new ReentrantLock();
	private volatile RuntimeData rData;
	private String myName;
	private String rivalName;
	private String mapSide;
	private volatile Bricks bricks;
	private volatile int gameViewWidth;
	private volatile int gameViewHeight;
	private MainActivity mainActivity;
	private volatile Ball ball1;
	private volatile Ball ball2;
	private volatile Bar myBar;
	private volatile Bar rivalBar;
	private volatile int pendingBrickId;
	private volatile SoundPool sp;
    private volatile int collideId;
    private volatile boolean deActivatedFromServer = false;
	
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
	    rData = mainActivity.getrData();
	    bricks = rData.getBricks();
        myName = rData.getMyName();
        rivalName = rData.getRivalName();
        mapSide = rData.getMapSide();
	    gameViewWidth = getWidth();
	    gameViewHeight = getHeight();
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
	    if (!deActivatedFromServer) {
		    Utils.deActivateFromServer(mainActivity, "stop", myName);
	    }
        Log.d(TAG, "surface destroyed!");
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (rData.isRunning()) {
	        if(event.getAction() == MotionEvent.ACTION_DOWN) {
	            myBar.setOldTouchX(event.getRawX() / gameViewWidth);
	            myBar.setPrevT(System.currentTimeMillis());
	            rData.setMyBar(myBar);
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
//          myBar.updateSpeed();
    		ball1.moveBall();
            ball2.moveBall();
            
            //either ball hit the ground, game over
            if (!ball1.getOnPlayInfo().gameIsOn() || !ball2.getOnPlayInfo().gameIsOn()) { 
            	rData.setRunning(false);
            	Utils.deActivateFromServer(mainActivity, "lose", myName);
            	deActivatedFromServer = true;
            	mainActivity.generateGameOverDialog("Please try again :)");
            	Log.d(TAG, "ball hit the ground");
            }
            //optional ownership parameter "b" means "ball_id"
            String ownershipPara = "";
            if (ball1.getOnPlayInfo().isOwnershipChanged()) {
//            	Log.d(TAG, "inform server to change ball 1 ownership to " + myName);
            	ownershipPara = "&b=1";
            }
            if (ball2.getOnPlayInfo().isOwnershipChanged()) {
//            	Log.d(TAG, "inform server to change ball 2 ownership to " + myName);
            	ownershipPara = "&b=2";
            }
            //compulsory url parameter "x" means "bar_position_x="
            String myBarPara = "&x=" + barXToServer(myBar.getBarX());
            //optional ball1, ball2 and brick parameters
            //"x" is ballX, "y" is ballY, "w" is ballXSpeed, "z" is ballYSpeed, "k" is brickId
            String ball1Para = "";
            String ball2Para = "";
            String brickPara = "";
            if (ball1.isOwned()) {
            	ball1Para = "&x1=" + ballXYToServer(ball1.getX()) + 
            			"&y1=" + ballXYToServer(ball1.getY()) +
            			"&w1=" + ballSpeedToServer(ball1.getXSpeed()) + 
            			"&z1=" + ballSpeedToServer(ball1.getYSpeed());
            	int brickToDisappear1 = bricks.checkCollision(ball1, gameViewWidth, gameViewHeight);
            	if (brickToDisappear1 != 0 ) {
            		pendingBrickId = brickToDisappear1;
            		sp.play(collideId, 0.5f, 0.5f, 0, 0, 1);
            		brickPara = "&k=" + brickToDisappear1;
            	}
            }
            
            if (ball2.isOwned()) {
            	ball2Para = "&x2=" + ballXYToServer(ball2.getX()) + 
            			"&y2=" + ballXYToServer(ball2.getY()) +
            			"&w2=" + ballSpeedToServer(ball2.getXSpeed()) + 
            			"&z2=" + ballSpeedToServer(ball2.getYSpeed());
            	int brickToDisappear2 = bricks.checkCollision(ball2, gameViewWidth, gameViewHeight);
            	if (brickToDisappear2 != 0 ) {
            		pendingBrickId = brickToDisappear2;
            		sp.play(collideId, 0.5f, 0.5f, 0, 0, 1);
            		brickPara = "&k=" + brickToDisappear2;
            	}
            }
            
            String url = Constants.SEVER_URL + 
            		"?command=play&n=" + myName + 
            		myBarPara + 
            		ball1Para + 
            		ball2Para + 
            		brickPara + 
            		ownershipPara; 
            readFromServer(url);
            
            if (bricks.isClear()) { //all bricks are gone
                mainActivity.showRuntimeData();
                rData.setRunning(false);
                int myScore = rData.getMyScore();
                int rivalScore = rData.getRivalScore();
                if (myScore == rivalScore) {
                	mainActivity.generateGameOverDialog("You draw :)");
                } else if (myScore > rivalScore) {
                	mainActivity.generateGameOverDialog("Congratulations! You win!");
                } else {
                	mainActivity.generateGameOverDialog("You lost :( Try again!");
                }
            }  
        } finally {
        	lock.unlock();
        }
	}
    
    private float barXToServer(float myBarX) {
    	float barX = myBarX;
    	if (mapSide.equals("B")) { //convert to the position in A side in server
    		barX = 1 - Constants.BAR_LENGTH_FACTOR - myBarX;
    	}
    	return barX;
    }
    
    private float ballXYToServer(float ballXY) {
    	float xy = ballXY;
    	if (mapSide.equals("B")) { //convert to the position in A side in server
    		xy = 1 - ballXY;
    	}
    	return xy;
    }
    
    private float ballSpeedToServer(float ballSpeed) {
    	float speed = ballSpeed;
    	if (mapSide.equals("B")) { //convert to the position in A side in server
    		speed = -ballSpeed;
    	}
    	return speed;
    }
    
    /**************************** NETWORK CODE *******************************/   
    
    private void readFromServer(String url) {
    	Log.d(TAG, url);
    	JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
    			new Response.Listener<JSONObject>() {
    	    @Override
    	    public void onResponse(JSONObject response) {
    	    	try {
    	    		Log.d(TAG, response.toString());
    	    		//save scores
    	    		JSONObject scores = response.getJSONObject("s");
    	    		if (mapSide.equals("A")) {
    	    			rData.setMyScore(Integer.valueOf(scores.getString("A")));
        	    		rData.setRivalScore(Integer.valueOf(scores.getString("B")));
    	    		} else {
    	    			rData.setMyScore(Integer.valueOf(scores.getString("B")));
        	    		rData.setRivalScore(Integer.valueOf(scores.getString("A")));
    	    		}
    	    		
    	    		//read bar info
    	    		JSONObject barInfo = response.getJSONObject("b"); // "b" is bar
    	    		String tag = barInfo.getString("r"); //"r" is response
    	    		if (tag.equals("l")) { //"l" is player lost
    	    			rData.setRunning(false);
    	    			mainActivity.generateGameOverDialog("Your rival is away. Try again later.");
    	    		} else if (tag.equals("w")) {
    	    			mainActivity.generateGameOverDialog("You win! Congratulations!");
    	    		} else { // read rival bar position first then handle other information
    	    			float rivalBarX = Float.valueOf(barInfo.getString("x")); //"x" is barX
    	    			if (mapSide.equals("B")) {
    	    				rivalBarX = 1 - Constants.BAR_LENGTH_FACTOR - rivalBarX;
    	    			}
    	    			rivalBar.setBarX(rivalBarX);
    	    			//handle brick gone
    	    			if (tag.equals("m") || tag.equals("k")) { //"m" - one brick gone; "k" - one brick gone & ball ownership changed
    	    				int brickId = Integer.valueOf(barInfo.getString("k")); //here "k" is for brickId
    	    				bricks.setAlive(brickId, false);
//    	    				Log.d(TAG, "more???");
        	    		}
    	    			//handle ball ownership change
    	    			if (tag.equals("o") || tag.equals("k")) {
        	    			String ball1Owner = barInfo.getString("1");//"1" means ball1
        	    			if (ball1Owner.equals(rivalName)) {
        	    				ball1.setOwned(false);
        	    			}
        	    			String ball2Owner = barInfo.getString("2");//"2" means ball2
        	    			if (ball2Owner.equals(rivalName)) {
        	    				ball2.setOwned(false);
        	    			}
        	    		}
    	    		}
    	    		
    	    		//check if brick elimination is ok
    	    		if (response.has("e")){//"e" is "eliminate"
    	    			if (response.getString("e").equals("y")) {
    	    				bricks.setAlive(pendingBrickId, false);
//        	                Log.d(TAG, "brick no. " + pendingBrickId + " was eliminated by " + myName);
    	    			}
    	    		}
    	    		
    	    		// finally read ball info
    	    		if (response.has("l")){ //"l" is for ball
    	    			JSONObject ballInfo = response.getJSONObject("l");
    	    			if (ballInfo.has("1")) { //"1" is for ball1
    	    				JSONObject ball1Info = ballInfo.getJSONObject("1");
    	    				float ball1X = Float.valueOf(ball1Info.getString("x"));
    	    				float ball1Y = Float.valueOf(ball1Info.getString("y"));
    	    				float ball1XSpeed = Float.valueOf(ball1Info.getString("w"));
    	    				float ball1YSpeed = Float.valueOf(ball1Info.getString("z"));
    	    				if (mapSide.equals("A")) {
    	    					ball1.setPositionAndSpeed(ball1X, ball1Y, ball1XSpeed, ball1YSpeed);
    	    				} else {
    	    					ball1.setPositionAndSpeed(1 - ball1X, 1 - ball1Y, -ball1XSpeed, -ball1YSpeed);
    	    				}
    	    			}
    	    			if (ballInfo.has("2")) {//"2" is for ball2
    	    				JSONObject ball2Info = ballInfo.getJSONObject("2");
    	    				float ball2X = Float.valueOf(ball2Info.getString("x"));
    	    				float ball2Y = Float.valueOf(ball2Info.getString("y"));
    	    				float ball2XSpeed = Float.valueOf(ball2Info.getString("w"));
    	    				float ball2YSpeed = Float.valueOf(ball2Info.getString("z"));
    	    				if (mapSide.equals("A")) {
    	    					ball2.setPositionAndSpeed(ball2X, ball2Y, ball2XSpeed, ball2YSpeed);
    	    				} else {
    	    					ball2.setPositionAndSpeed(1 - ball2X, 1 - ball2Y, -ball2XSpeed, -ball2YSpeed);
    	    				}
    	    			}
    	    		}
    	        } catch (JSONException e) {
    	        	Log.d(TAG, "Something Wrong!!!");
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

    /**************************** NETWORK CODE *******************************/
    

    private void initBallAndBar() {
    	myBar = new Bar(Constants.BAR_INIT_X_FACTOR, Constants.BAR_INIT_Y_FACTOR);
        rivalBar = new Bar(Constants.BAR_INIT_X_FACTOR, Constants.OPPOSITE_BAR_Y_FACTOR);
        
        if (mapSide.equals("A")) { //I own ball1
        	ball1 = new Ball(1, 
            		Constants.BALL_INIT_X_FACTOR,
            		Constants.BALL_INIT_Y_FACTOR, 
            		Constants.BALL_INIT_XSPEED_FACTOR,
            		Constants.BALL_INIT_YSPEED_FACTOR,
            		myBar, true);
            
            ball2 = new Ball(2,
            		Constants.BALL_INIT_X_FACTOR,
            		1 - Constants.BALL_INIT_Y_FACTOR,
            		- Constants.BALL_INIT_XSPEED_FACTOR,
            		- Constants.BALL_INIT_YSPEED_FACTOR,
            		myBar, false);
        } else { // I own ball2
        	ball1 = new Ball(1,
            		Constants.BALL_INIT_X_FACTOR,
            		1 - Constants.BALL_INIT_Y_FACTOR,
            		- Constants.BALL_INIT_XSPEED_FACTOR,
            		- Constants.BALL_INIT_YSPEED_FACTOR,
            		myBar, false);
            
            ball2 = new Ball(2, 
            		Constants.BALL_INIT_X_FACTOR,
            		Constants.BALL_INIT_Y_FACTOR, 
            		Constants.BALL_INIT_XSPEED_FACTOR,
            		Constants.BALL_INIT_YSPEED_FACTOR,
            		myBar, true);
        }        
        Log.d(TAG, "initBallAndBar");
        Log.d(TAG, "Phone:" + rData.getMyName() + "; mapSide: " + rData.getMapSide());
    }
}
