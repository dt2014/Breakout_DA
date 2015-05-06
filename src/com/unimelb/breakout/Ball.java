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

import java.io.Serializable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/*
 * All measurements in this class are relative values to the game view 
 * width (for x) and height (for y) except for the method to draw the
 * ball on canvas.
 */
public class Ball implements Serializable {
    private static final long serialVersionUID = -1002240139201271969L;
   
    private volatile float ballX;
    private volatile float ballY;
    private volatile float ballXSpeed;
    private volatile float ballYSpeed;
    
    private volatile Bar myBar;
    
    private volatile OnPlayData onPlayData;
    
    private static final Paint paintOwned = new Paint();
    private static final Paint paintOther = new Paint();
    
    private volatile int ballId;
    private volatile boolean owned;
    
    public Ball(int ballId, float ballX, float ballY, float ballXSpeed, float ballYSpeed, Bar myBar, boolean owned) {
    	this.ballId = ballId;
    	this.ballX = ballX;
    	this.ballY = ballY;
    	this.ballXSpeed = ballXSpeed;
    	this.ballYSpeed = ballYSpeed;
    	this.myBar = myBar;
    	this.owned = owned;
    	
    	paintOwned.setAntiAlias(true);
    	paintOwned.setColor(Color.YELLOW);
    	paintOther.setAntiAlias(true);
    	paintOther.setColor(Color.GRAY);
    	
//        mapSide = rData.getMapSide();
//    	this.ballId = ballId;
//    	if (ballId == 1) {
//    		ballX = rData.getBall1X();
//    		ballY = rData.getBall1Y();
//    		ballXSpeed = rData.getBall1XSpeed();
//    		ballYSpeed = rData.getBall1YSpeed();
//    		owned = rData.isBall1Owned();
//    	} else { //ballId == 2
//    		ballX = rData.getBall2X();
//    		ballY = rData.getBall2Y();
//    		ballXSpeed = rData.getBall2XSpeed();
//    		ballYSpeed = rData.getBall2YSpeed();
//    		owned = rData.isBall2Owned();
//    	}
//        bar = rData.getMyBar();
//        ballRadius = (float) (screenWidth * Constants.BALL_RADIUS_FACTOR);
    }
    
    public int getBallId() {
		return ballId;
	}

//	public void setBallId(int ballId) {
//		this.ballId = ballId;
//	}

	public boolean isOwned() {
		return owned;
	}

	public void setOwned(boolean owned) {
		this.owned = owned;
	}

	public float getX() {
		return ballX;
	}

	public void setX(float x) {
		this.ballX = x;
	}

    public float getY() {
		return ballY;
	}

	public void setY(float y) {
		this.ballY = y;
	}

	public float getXSpeed() {
		return ballXSpeed;
	}

	public void setXSpeed(float xSpeed) {
		this.ballXSpeed = xSpeed;
	}

	public float getYSpeed() {
		return ballYSpeed;
	}

	public void setYSpeed(float ySpeed) {
		this.ballYSpeed = ySpeed;
	}
	
	public OnPlayData getOnPlayInfo() {
        return onPlayData;
    }

    public void onDraw(Canvas canvas, int screenWidth, int screenHeight) {
    	Paint paint = isOwned() ? paintOwned : paintOther;
    	canvas.drawCircle(ballX * screenWidth, ballY * screenHeight, Constants.BALL_RADIUS_FACTOR * screenWidth, paint);
    }

    public void moveBall() {
        onPlayData = new OnPlayData();
        
        ballX = ballX + ballXSpeed;
        ballY = ballY + ballYSpeed;
        
        detectBoundary();
	    detectBarCollision();
    }
	
	public void detectBoundary() {
		if (ballX > 1 - Constants.BALL_RADIUS_FACTOR) { //bounce off right edge 
		    ballX = 1 - Constants.BALL_RADIUS_FACTOR;
    		ballXSpeed = -Math.abs(ballXSpeed);
    	} else if (ballX < Constants.BALL_RADIUS_FACTOR) {//bounce off left edge
		    ballX = Constants.BALL_RADIUS_FACTOR;
    		ballXSpeed = Math.abs(ballXSpeed);
    	}
		
    	if (ballY < Constants.BALL_RADIUS_FACTOR) {//bounce off up edge
    	    ballY = Constants.BALL_RADIUS_FACTOR;
    	    ballYSpeed = -ballYSpeed;
    	} else if (ballY > 1 - Constants.BALL_RADIUS_FACTOR){ // reach bottom edge
    	    ballY = 1 - Constants.BALL_RADIUS_FACTOR;
            ballXSpeed = 0;
            ballYSpeed = 0;
            onPlayData.setGameOn(false);
    	}
	}

	public void detectBarCollision() {
		if (ballYSpeed < 0) //improve efficiency
			return;
		else {
			float upperLine = myBar.getBarY();
			float leftLine = myBar.getBarX();
			float rightLine = myBar.getBarX() + Constants.BAR_LENGTH_FACTOR;
			
			if (ballY + Constants.BALL_RADIUS_FACTOR >= upperLine && ballY < upperLine) { //ball hits bar
			    if(ballX >= leftLine && ballX <= rightLine) { //hitting the top of the bar
			        ballY = upperLine - Constants.BALL_RADIUS_FACTOR;
			        ballYSpeed = -ballYSpeed;
                    ballXSpeed = ballXSpeed + myBar.getBarXSpeed();
	            } else if (ballX + 0.707 * Constants.BALL_RADIUS_FACTOR >= leftLine && ballX < leftLine && ballXSpeed > 0) { //0.707 = sqrt(2)/2
	                ballY = upperLine - Constants.BALL_RADIUS_FACTOR; //hitting bar left corner
	                ballXSpeed = -ballXSpeed;
                    ballYSpeed = -ballYSpeed;
	            } else if (ballX - 0.707 * Constants.BALL_RADIUS_FACTOR <= rightLine && ballX > rightLine && ballXSpeed < 0) {
	                ballY = upperLine - Constants.BALL_RADIUS_FACTOR; //hitting bar right corner
                    ballXSpeed=-ballXSpeed;
                    ballYSpeed=-ballYSpeed;
	            }
			    this.setOwned(true); // change the owner ship of the ball
			    onPlayData.setOwnershipChanged(true);
			}
		}
	}
}