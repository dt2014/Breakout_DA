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
 * bar on canvas.
 */
public class Bar implements Serializable {
    private static final long serialVersionUID = 6816443624906944960L;

    private volatile float barX;
    private volatile float barY;
    private volatile float oldTouchX;
    private volatile float barXSpeed;

    private static final Paint paint = new Paint();
    private volatile boolean isMoved;
    private volatile int count;
    private volatile long prevT;
    
    public Bar(float barX, float barY) {
        this.barX = barX;
        this.barY = barY;
        oldTouchX = barX;
        barXSpeed = 0;
        
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(70, 130, 180)); //steel blue
    }

    public void onDraw(Canvas canvas, int screenWidth, int screenHeight) {
    	canvas.drawRect(barX * screenWidth,
    			barY * screenHeight,
    			(barX + Constants.BAR_LENGTH_FACTOR) * screenWidth,
    			(barY + Constants.BAR_HEIGHT_FACTOR) * screenHeight,
    			paint);
    }
    
    public void move(float deltaX, long deltaT) {
        count = 0;
        isMoved = true;
        barX = barX + deltaX;
        if (barX < 0) {
            barX = 0;
        } else if (barX + Constants.BAR_LENGTH_FACTOR > 1) {
            barX = 1 - Constants.BAR_LENGTH_FACTOR;
        }
        barXSpeed = (deltaX / deltaT * 1000);
        float speedValue = Math.abs(barXSpeed);
        if (speedValue > 0 && speedValue <= 20) {
            speedValue = 0.001f;
        } else if (speedValue > 20 && speedValue <= 30) {
            speedValue = 0.002f;
        } else if (speedValue > 30) {
            speedValue = 0.003f;
        }
        if (barXSpeed > 0) {
            barXSpeed = speedValue;
        } else {
            barXSpeed = -speedValue;
        }
    }

    public float getBarX() {
		return barX;
	}

    public void setBarX(float barX) {
		this.barX = barX;
		if (barX < 0) {
            this.barX = 0;
        } else if (barX + Constants.BAR_LENGTH_FACTOR > 1) {
            this.barX = 1 - Constants.BAR_LENGTH_FACTOR;
        }
	}
	
    public float getBarY() {
		return barY;
	}
    
    public void setBarY(float barY) {
		this.barY = barY;
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
}
