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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Bar implements Serializable {
    private static final long serialVersionUID = 6816443624906944960L;
    
    public static final float heightFactor = 0.03F; // the portion of the screenHeight
    private volatile float lengthFactor; // the portion of screenWidth
    private volatile float length;
    private volatile float height;
    private volatile float x;
    private volatile float y;
    private volatile float oldTouchX;
    private volatile float barXSpeed;
    
    private int screenWidth;
    private int screenHeight;

    private static final Paint paint = new Paint();
    private volatile boolean isMoved;
    private volatile int count;
    private volatile long prevT;
    
    public Bar(float x, float y, float lengthFactor, int screenWidth, int screenHeight) {
        this.x = x;
        this.y = y;
        this.lengthFactor = lengthFactor;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        length = screenWidth * lengthFactor;
        height = screenHeight * heightFactor;
        oldTouchX = x;
        barXSpeed = 0;
        
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(70, 130, 180)); //steel blue
    }

    public void onDraw(Canvas canvas) {
        canvas.drawRect(x, y, x + length, y + height, paint);
    }
    public void move(float deltaX, long deltaT) {
        count = 0;
        isMoved = true;
        x = x + deltaX;
        if (x < 0) {
            x = 0;
        } else if (x + length > screenWidth) {
            x = screenWidth - length;
        }
        barXSpeed = (deltaX / deltaT * 100);
        float speedValue = Math.abs(barXSpeed);
        if (speedValue > 0 && speedValue <= 20) {
            speedValue = 1;
        } else if (speedValue > 20 && speedValue <= 30) {
            speedValue = 2;
        } else if (speedValue > 30) {
            speedValue = 3;
        }
        if (barXSpeed > 0) {
            barXSpeed = speedValue;
        } else {
            barXSpeed = -speedValue;
        }
    }

    public float getLength() {
        return length;
    }
    
    public float getHeight() {
        return height;
    }
    
    public float getX() {
		return x;
	}

    public float getY() {
		return y;
	}
    
	public void setX(float x) {
		this.x = x;
		if (x < 0) {
            this.x = 0;
        } else if (x + length > screenWidth) {
            this.x = screenWidth - length;
        }
	}
	
	public void setY(float d) {
		this.y = d;
	}
    
    public float getOldTouchX() {
        return oldTouchX;
    }

    public void setOldTouchX(float oldTouchX) {
        this.oldTouchX = oldTouchX;
    }
    
    public float getBarXSpeed() {
        return barXSpeed;
    }

    public void setBarXSpeed(float barXSpeed) {
        this.barXSpeed = barXSpeed;
    }
    
    public long getPrevT() {
        return prevT;
    }

    public void setPrevT(long prevT) {
        this.prevT = prevT;
    }

    public void updateSpeed() {
        if (!isMoved) {
            ++count;
            if (count > 10) {
                barXSpeed = 0;
                count = 0;
            }
        } else {
            isMoved = false;
        }
    }
    
	/*
	public void resetCoords() {
        setX(screenWidth/2); 
        setY(9*screenHeight/10);
	}*/
}
