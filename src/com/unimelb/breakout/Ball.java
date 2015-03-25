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

import java.io.Serializable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Ball implements Serializable {
    private static final long serialVersionUID = -1002240139201271969L;

    private static float ballRadius = 10;
   
    private volatile float x;
    private volatile float y;
    private volatile float xSpeed;
    private volatile float ySpeed;
    
    private volatile int screenWidth;
    private volatile int screenHeight;
    
    private volatile Bar bar;
    
    private volatile OnPlayData onPlayData;
    
    private static final Paint paint = new Paint();
    
    public Ball(float x, float y, float xSpeed, float ySpeed, 
            int screenWidth, int screenHeight, Bar bar) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.bar = bar;
        
        ballRadius = (float) (screenWidth/72.0);

        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
    }
    
    /*
    public void resetCoordsAndSpeed() { 
    	this.x = (x/screenWidth)*this.screenWidth;
        this.y = ballRadius;
        setX(screenWidth/2);
        setY(5*screenHeight/10);
        float increase = (float) (1 + getLevel()*0.1);
        setXSpeed(5*increase);
        setYSpeed(10*increase);
    }*/
    
    public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

    public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getXSpeed() {
		return xSpeed;
	}

	public void setXSpeed(float xSpeed) {
		this.xSpeed = xSpeed;
	}

	public float getYSpeed() {
		return ySpeed;
	}

	public void setYSpeed(float ySpeed) {
		this.ySpeed = ySpeed;
	}

	public static float getRadius() {
        return ballRadius;
    }
	
	public OnPlayData getOnPlayInfo() {
        return onPlayData;
    }

    public void onDraw(Canvas canvas) {
        canvas.drawCircle(x, y, ballRadius, paint);
    }

    public void moveBall() {
        onPlayData = new OnPlayData();
        
        x = x + xSpeed;
        y = y + ySpeed;
        
        updatePhysics();
    }
	/*public void updatePhysics() {
		float left,right;
		left = bar.getX()-bar.getLength()/2;
		right = bar.getX()+bar.getLength()/2;
		if(x > screenWidth-ballRadius) {
    		xSpeed=-xSpeed;
    	}
		else if(x < ballRadius) {
    		xSpeed=-xSpeed;
    	}
    	if(y > screenHeight-ballRadius) {
    		ySpeed=-ySpeed;
    	}
    	else if(y < ballRadius) {
    		ySpeed=-ySpeed;
    	}
    	else if((y + ballRadius)>= (bar.getY()-bar.getWidth()/2))
    	{
    		if(x>=left&&x<=right&&((y - ballRadius)<(bar.getY()-bar.getWidth()/2)))
    			ySpeed=-ySpeed;
    	}
	}*/

	public void updatePhysics() {
	    detectBoundary();
	    detectBarCollision();
	}
	
	public void detectBoundary() {
		if(x > screenWidth-ballRadius) {
		    x = screenWidth-ballRadius;
    		xSpeed = -Math.abs(xSpeed);
    	}
		else if(x < ballRadius) {
		    x = ballRadius;
    		xSpeed = Math.abs(xSpeed);
    	}
		
    	if(y < ballRadius) {
    	    y = ballRadius;
    	    ySpeed = -ySpeed;
    	} else if (y > screenHeight-ballRadius){
    	    y = screenHeight-ballRadius;
            xSpeed = 0;
            ySpeed = 0;
            onPlayData.setLives(-1);
    	}
	}

	public void detectBarCollision() {
		if(ySpeed < 0) //improve efficiency
			return;
		else {
			float upperLine = bar.getY();
			float leftLine = bar.getX();
			float rightLine = bar.getX() + bar.getLength();
			
			if (y + ballRadius >= upperLine && y < upperLine) {
			    if(x >= leftLine && x <= rightLine) {
			        y = upperLine - ballRadius;
			        ySpeed = -ySpeed;
                    xSpeed = xSpeed + bar.getBarXSpeed();
	            } else if (x + 0.707 * ballRadius >= leftLine && x < leftLine && xSpeed > 0) //0.707 = sqrt(2)/2
	            {
	                y = upperLine - ballRadius;
	                xSpeed = -xSpeed;
                    ySpeed = -ySpeed;
	            } else if (x - 0.707 * ballRadius <= rightLine && x > rightLine && xSpeed < 0) {
	                y = upperLine - ballRadius;
                    xSpeed=-xSpeed;
                    ySpeed=-ySpeed;
	            }
			}
		}
	}
	/* This method is moved to bricks class.
	public void detectBricksCollision() {
		Bricks wall = bricks;
		Brick[] bricks = wall.getBricks();
		float halfWidth = bricks[0].getWidth()/2;
		float halfLength = bricks[0].getLength()/2;
		float x,y,upperLine,leftLine,rightLine,bottomLine,rpi; //describe the bricks;
		for(int i = 0; i<wall.getNumber(); i++){
			if(bricks[i].getLive())
			{
				x = bricks[i].getX();
				y = bricks[i].getY();
				if((Math.abs(y-this.y)>halfWidth+this.ballRadius)||Math.abs(x-this.x)>halfLength+this.ballRadius)
					continue; //for improving the efficiency 
				upperLine = y - halfWidth;;
				leftLine = x - halfLength;
				rightLine = x + halfLength;
				bottomLine = y + halfWidth;
				rpi = (float) (0.707*ballRadius);
				if((this.x+rpi)>leftLine&&(this.x-rpi)<rightLine)
				{
					if(ySpeed>0&&this.y+ballRadius>=upperLine&&this.y-ballRadius<=upperLine)
					{	
						ySpeed=-ySpeed;
						bricks[i].eliminateBrick();
						continue;
					}
					if(ySpeed<0&&this.y-ballRadius<=bottomLine&&this.y+ballRadius>=bottomLine)
					{
						ySpeed=-ySpeed;
						bricks[i].eliminateBrick();
						continue;
					}
				}
				if(this.y+rpi>=upperLine&&this.y-rpi<=bottomLine)
				{
					if(xSpeed>0&&this.x+ballRadius>=leftLine&&this.x<=leftLine)
					{
						xSpeed=-xSpeed;
						bricks[i].eliminateBrick();
						continue;
					}
					if(xSpeed<0&&this.x-ballRadius>=rightLine&&this.x<=rightLine)
					{
						xSpeed=-xSpeed;
						bricks[i].eliminateBrick();
						continue;
					}
				]
			}
		}
	}*/
}