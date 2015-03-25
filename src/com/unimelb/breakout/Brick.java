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
import java.util.Random;

public class Brick implements Serializable{
    private static final long serialVersionUID = 3947787005416186023L;
    private volatile float x;
	private volatile float y;
	public static final float lengthFactor = 0.07F;
	public static final float heightFactor = 0.025F;
	
	private boolean alive;
	private boolean special;

    private int hitTimes;
	
	public Brick(float x, float y, boolean special){
		this.x = x;
		this.y = y;
        this.special = special;

        this.alive = true;
        
		Random rn = new Random();
		this.hitTimes = rn.nextInt(3) + 1;
	}
	
	public float getX(){
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

	public boolean isAlive() {
		return alive;
	}

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }
    
	public Object getHitTimes() {
		return hitTimes;
	}
	
	public void setHitTimes(int times) {
		hitTimes = times;
	}
	
	/*
	public void eliminateBrick(){
		hitTimes=hitTimes-1;
		WorldView.score = WorldView.score + 100;
		if(hitTimes == 0)
			alive = false;
	}
	
	//return value for counting how many bricks are still alive.
    @SuppressLint("DrawAllocation")
	public int onDraw(Canvas canvas) {
    	if(alive == true){
    		Paint paint = new Paint();
    		paint.setAntiAlias(true);
    		if (hitTimes == 3)
    			paint.setColor(Color.YELLOW);
    		else if (hitTimes == 2)
    			paint.setColor(Color.CYAN);
    		else 
    			paint.setColor(Color.RED);
    		if(worldView.onScreen) 
    		{
    			RectF r = new RectF(x-length/2,y-height/2,x+length/2,y+height/2);
    			canvas.drawRect(r, paint);
    		}
    		return 1;
    	}
    	return 0;
    }*/
}
