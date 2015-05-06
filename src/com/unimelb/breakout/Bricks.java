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
    
//    private volatile int screenWidth;
//    private volatile int screenHeight;
//    private volatile float brickLength;
//    private volatile float brickHeight;
//    private volatile float ballRadius;
    
    private static final Paint normalPaint = new Paint();
    private static final Paint specialPaint = new Paint();

    public Bricks() {
//        this.screenWidth = viewWidth;
//        this.screenHeight = viewHeight;
//        brickLength = viewWidth * Constants.BRICK_LENGTH_FACTOR;
//        brickHeight = viewHeight * Constants.BRICK_HEIGHT_FACTOR;
//        ballRadius = viewWidth * Constants.BALL_RADIUS_FACTOR;
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
    
    public void setAlive(int brickId, boolean alive) {
    	this.bricks.get(brickId - 1).setAlive(alive); 
    }
    
    /*
     * Check brick & ball collision here
     * Return the brickId if collision occurs, otherwise return 0
     */
	public int checkCollision(Ball ball, int screenWidth, int screenHeight) {
        float ballx = ball.getX();
        float bally = ball.getY();
        for (Brick brick : bricks) {
            if (brick.isAlive()) {
                float brickx = brick.getBrickX();
                float bricky = brick.getBrickY();
                
                Rect bRect = new Rect((int) (brickx * screenWidth),
                		(int) (bricky * screenHeight), 
                        (int) ((brickx + Constants.BRICK_LENGTH_FACTOR) * screenWidth),
                        (int) ((bricky + Constants.BRICK_HEIGHT_FACTOR) * screenHeight));
                if (bRect.intersect((int) ((ballx - Constants.BALL_RADIUS_FACTOR) * screenWidth), 
                        (int) ((bally - Constants.BALL_RADIUS_FACTOR) * screenHeight),
                        (int) ((ballx + Constants.BALL_RADIUS_FACTOR) * screenWidth), 
                        (int) ((bally + Constants.BALL_RADIUS_FACTOR) * screenHeight))) {
//                    brick.setAlive(false);
                    
                    //TODO!!!!!!!!!!!!!!!!!
//                    if (brick.isSpecial()) {
//                        onPlayData.setScore(200);
//                    } else {
//                        onPlayData.setScore(100);
//                    }
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
                    return brick.getId();
                }
            }
        }
//        if (aliveBrickCount == 0) {
//            onPlayData.setClear(true);
//        }
        return 0;
	}
	
	public void onDraw(Canvas canvas, int screenWidth, int screenHeight) {
		for (Brick brick : bricks) {
			if (brick.isAlive()) {
				if (brick.isSpecial()) {
                    canvas.drawRect(brick.getBrickX() * screenWidth,
                    		brick.getBrickY() * screenHeight,
                    		(brick.getBrickX() + Constants.BRICK_LENGTH_FACTOR) * screenWidth, 
                            (brick.getBrickY()  + Constants.BRICK_HEIGHT_FACTOR) * screenHeight,
                            specialPaint);
                } else {
                	canvas.drawRect(brick.getBrickX() * screenWidth,
                			brick.getBrickY()  * screenHeight,
                    		(brick.getBrickX() + Constants.BRICK_LENGTH_FACTOR) * screenWidth, 
                            (brick.getBrickY()  + Constants.BRICK_HEIGHT_FACTOR) * screenHeight,
                            normalPaint);
                }
			}
			
		}
	}
	
	public boolean isClear() {
		boolean isClear = false;
		aliveBrickCount = bricks.size();
		for (Brick b : bricks) {
			if(!b.isAlive()) {
				--aliveBrickCount;
			}
        }
		if (aliveBrickCount == 0) isClear = true;
		return isClear;
	}
//	public void validate(int gameViewWidth, float barY) {
//        aliveBrickCount = 0;
//        for (Brick b : bricks) {
//            float brickx = b.getBrickX() * gameViewWidth;
//            float bricky = b.getBrickY() * gameViewWidth;
//            if (brickx + brickLength < gameViewWidth &&
//                    bricky + brickHeight < barY) {
//                b.setAlive(true);
//                ++aliveBrickCount;
//            } else {
//                b.setAlive(false);
//            }
//        }
//    }
    
//    public void countBricks(int gameViewWidth, float barY) {
//        aliveBrickCount = 0;
//        for (Brick b : bricks) {
//            float brickx = b.getBrickX() * gameViewWidth;
//            float bricky = b.getBrickY() * gameViewWidth;
//            if (brickx + brickLength < gameViewWidth &&
//                    bricky + brickHeight < barY && b.isAlive()) {
//                b.setAlive(true);
//                ++aliveBrickCount;
//            } else {
//                b.setAlive(false);
//            }
//        }
//    }

    public void initBricks(List<Brick> bricks) {
        this.bricks = Collections.synchronizedList(bricks);
        for (Brick b : this.bricks) {
        	b.setAlive(true);
        }
    }
}
