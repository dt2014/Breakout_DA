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

public class Bar implements Serializable {
    private static final long serialVersionUID = 6816443624906944960L;

    private volatile float barLength;
    private volatile float barHeight;
    private volatile float barX;
    private volatile float barY;
    private volatile float oldTouchX;
    private volatile float barXSpeed;
    
    private int screenWidth;
    private int screenHeight;

    private static final Paint paint = new Paint();
    private volatile boolean isMoved;
    private volatile int count;
    private volatile long prevT;
    
    public Bar(float barX, float barY, int screenWidth, int screenHeight) {
        this.barX = barX;
        this.barY = barY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        barLength = screenWidth * Constants.BAR_LENGTH_FACTOR;
        barHeight = screenHeight * Constants.BAR_HEIGHT_FACTOR;
        oldTouchX = barX;
        barXSpeed = 0;
        
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(70, 130, 180)); //steel blue
    }

    public void onDraw(Canvas canvas) {
        canvas.drawRect(barX, barY, barX + barLength, barY + barHeight, paint);
    }
    public void move(float deltaX, long deltaT) {
        count = 0;
        isMoved = true;
        barX = barX + deltaX;
        if (barX < 0) {
            barX = 0;
        } else if (barX + barLength > screenWidth) {
            barX = screenWidth - barLength;
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

    public float getBarLength() {
        return barLength;
    }
    
    public float getBarHeight() {
        return barHeight;
    }
    
    public float getBarX() {
		return barX;
	}

    public float getBarY() {
		return barY;
	}
    
	public void setBarX(float barX) {
		this.barX = barX;
		if (barX < 0) {
            this.barX = 0;
        } else if (barX + barLength > screenWidth) {
            this.barX = screenWidth - barLength;
        }
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
