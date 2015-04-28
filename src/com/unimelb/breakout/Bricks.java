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
import java.util.Collections;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Bricks implements Serializable {
    String TAG = "bricks";
    private static final long serialVersionUID = -6723171429661538211L;
    private volatile List<Brick> bricks;
    private volatile int aliveBrickCount;
    
    private volatile int viewWidth;
    private volatile int viewHeight;
    private volatile float brickLength;
    private volatile float brickHeight;
    private volatile float ballRadius;
    
    private static final Paint normalPaint = new Paint();
    private static final Paint specialPaint = new Paint();

    public Bricks(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        brickLength = viewWidth * Constants.BRICK_LENGTH_FACTOR;
        brickHeight = viewHeight * Constants.BRICK_HEIGHT_FACTOR;
        ballRadius = viewWidth * Constants.BALL_RADIUS_FACTOR;
        normalPaint.setColor(Color.rgb(160, 82, 45)); //Sienna
        specialPaint.setColor(Color.rgb(0, 255, 127)); //Spring Green
	}

    public boolean isFinished() {
        boolean finished = true;
        if (bricks != null) {
            for (Brick b : bricks) {
                if (b.isAlive()) {
                    finished = false;
                    break;
                }
            }
        }
        return finished;
    }
    
    public List<Brick> getBricks() {
        return bricks;
    }
    
    public void setBricks(List<Brick> bricks) {
        this.bricks = bricks;
    }

    public int getAliveBrickCount() {
        return aliveBrickCount;
    }
    
    // implement brick & ball collision here
	public OnPlayData onDraw(Canvas canvas, Ball ball) { 
	    OnPlayData onPlayData = new OnPlayData();
        float ballx = ball.getX();
        float bally = ball.getY();
        boolean noCollision = true;
        for (Brick b : bricks) {
            if (b.isAlive()) {
                float brickx = b.getBrickX() * viewWidth;
                float bricky = b.getBrickY() * viewHeight;
                
                Rect bRect = new Rect((int)brickx, (int)bricky, 
                        (int)(brickx + brickLength), (int)(bricky + brickHeight));
                if (noCollision && bRect.intersect((int)(ballx - ballRadius), 
                        (int)(bally - ballRadius),(int)(ballx + ballRadius), 
                        (int)(bally + ballRadius))) {
                    b.setAlive(false);
                    noCollision = false;
                    aliveBrickCount--;
                    
                    if (b.isSpecial()) {
                        onPlayData.setScore(200);
                        onPlayData.setLives(1);
                    } else {
                        onPlayData.setScore(100);
                    }
                    int collisionWidth = bRect.width();
                    int collisionHeight = bRect.height();
                    if (collisionWidth > collisionHeight) {
                        ball.setYSpeed(-ball.getYSpeed());
                    } else if (collisionWidth < collisionHeight) {
                        ball.setXSpeed(-ball.getXSpeed());
                    } else {
                        ball.setXSpeed(-ball.getXSpeed());
                        ball.setYSpeed(-ball.getYSpeed());
                    }
                } else {
                    if (b.isSpecial()) {
                        canvas.drawRect(brickx, bricky, brickx + brickLength, 
                                bricky + brickHeight, specialPaint);
                    } else {
                        canvas.drawRect(brickx, bricky, brickx + brickLength, 
                                bricky + brickHeight, normalPaint);
                    }
                }
            }
        }
        if (aliveBrickCount == 0) {
            onPlayData.setClear(true);
        }
        return onPlayData;
	}

	public void validate(int gameViewWidth, float barY) {
        aliveBrickCount = 0;
        for (Brick b : bricks) {
            float brickx = b.getBrickX() * gameViewWidth;
            float bricky = b.getBrickY() * gameViewWidth;
            if (brickx + brickLength < gameViewWidth &&
                    bricky + brickHeight < barY) {
                b.setAlive(true);
                ++aliveBrickCount;
            } else {
                b.setAlive(false);
            }
        }
    }
    
    public void countBricks(int gameViewWidth, float barY) {
        aliveBrickCount = 0;
        for (Brick b : bricks) {
            float brickx = b.getBrickX() * gameViewWidth;
            float bricky = b.getBrickY() * gameViewWidth;
            if (brickx + brickLength < gameViewWidth &&
                    bricky + brickHeight < barY && b.isAlive()) {
                b.setAlive(true);
                ++aliveBrickCount;
            } else {
                b.setAlive(false);
            }
        }
    }

    public void initBricks(List<Brick> bricks) {
        this.bricks = Collections.synchronizedList(bricks);
    }

	public void setViewSize(int gameViewWidth, int gameViewHeight) {
		viewWidth = gameViewWidth;
		viewHeight = gameViewHeight;
		brickLength = viewWidth * Constants.BRICK_LENGTH_FACTOR;
        brickHeight = viewHeight * Constants.BRICK_HEIGHT_FACTOR;
        ballRadius = viewWidth * Constants.BALL_RADIUS_FACTOR;
	}
}
