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

public class Ball implements Serializable {
    private static final long serialVersionUID = -1002240139201271969L;

    private volatile float ballRadius;
   
    private volatile float ballX;
    private volatile float ballY;
    private volatile float ballXSpeed;
    private volatile float ballYSpeed;
    
    private volatile int screenWidth;
    private volatile int screenHeight;
    
    private volatile Bar bar;
    
    private volatile OnPlayData onPlayData;
    
    private static final Paint paint = new Paint();
    private volatile int ballColor;
    
    private volatile int ballId;
    private volatile boolean owned;
    
    public Ball(float ballX, float ballY, float ballXSpeed, float ballYSpeed, int screenWidth, int screenHeight, Bar bar, boolean owned) {
        this.ballX = ballX;
        this.ballY = ballY;
        this.ballXSpeed = ballXSpeed;
        this.ballYSpeed = ballYSpeed;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.bar = bar;
        this.owned = owned;
        
        ballRadius = (float) (screenWidth * Constants.BALL_RADIUS_FACTOR);

        paint.setAntiAlias(true);
        ballColor = owned ? Color.YELLOW : Color.WHITE;
        paint.setColor(ballColor);
    }
    
    public int getBallId() {
		return ballId;
	}

	public void setBallId(int ballId) {
		this.ballId = ballId;
	}

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

	public float getBallRadius() {
        return ballRadius;
    }
	
	public OnPlayData getOnPlayInfo() {
        return onPlayData;
    }

    public void onDraw(Canvas canvas) {
        canvas.drawCircle(ballX, ballY, ballRadius, paint);
    }

    public void moveBall() {
        onPlayData = new OnPlayData();
        
        ballX = ballX + ballXSpeed;
        ballY = ballY + ballYSpeed;
        
        detectBoundary();
	    detectBarCollision();
    }
	
	public void detectBoundary() {
		if(ballX > screenWidth-ballRadius) {
		    ballX = screenWidth-ballRadius;
    		ballXSpeed = -Math.abs(ballXSpeed);
    	}
		else if(ballX < ballRadius) {
		    ballX = ballRadius;
    		ballXSpeed = Math.abs(ballXSpeed);
    	}
		
    	if(ballY < ballRadius) {
    	    ballY = ballRadius;
    	    ballYSpeed = -ballYSpeed;
    	} else if (ballY > screenHeight-ballRadius){
    	    ballY = screenHeight-ballRadius;
            ballXSpeed = 0;
            ballYSpeed = 0;
            onPlayData.setLives(-1);
    	}
	}

	public void detectBarCollision() {
		if(ballYSpeed < 0) //improve efficiency
			return;
		else {
			float upperLine = bar.getBarY();
			float leftLine = bar.getBarX();
			float rightLine = bar.getBarX() + bar.getBarLength();
			
			if (ballY + ballRadius >= upperLine && ballY < upperLine) {
			    if(ballX >= leftLine && ballX <= rightLine) {
			        ballY = upperLine - ballRadius;
			        ballYSpeed = -ballYSpeed;
                    ballXSpeed = ballXSpeed + bar.getBarXSpeed();
	            } else if (ballX + 0.707 * ballRadius >= leftLine && ballX < leftLine && ballXSpeed > 0) //0.707 = sqrt(2)/2
	            {
	                ballY = upperLine - ballRadius;
	                ballXSpeed = -ballXSpeed;
                    ballYSpeed = -ballYSpeed;
	            } else if (ballX - 0.707 * ballRadius <= rightLine && ballX > rightLine && ballXSpeed < 0) {
	                ballY = upperLine - ballRadius;
                    ballXSpeed=-ballXSpeed;
                    ballYSpeed=-ballYSpeed;
	            }
			}
		}
	}
}