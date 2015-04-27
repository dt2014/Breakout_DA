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
import java.util.List;

public class RuntimeData implements Serializable {    
    private static final long serialVersionUID = 8666060483413961394L;
    
    private volatile boolean running;
    
    private volatile int gameViewWidth;
    private volatile int gameViewHeight;

    private volatile Ball ball;
    private volatile float ballx;
    private volatile float bally;
    private volatile float ballXSpeed;
    private volatile float ballYSpeed;
    
    private volatile float initballx; // factor to screen view
    private volatile float initbally; // factor to screen view
    private volatile float initballXSpeed; // factor to screen view
    private volatile float initballYSpeed; // factor to screen view
    
    private volatile Bar bar;
    private volatile float barx;
    private volatile float barLengthFacor;
    private volatile float barXSpeed;
    
    private volatile Bricks bricks;

    private volatile String myName;
    private volatile String rivalName =" ";
    
    private volatile int myScore;
    private volatile int rivalScore;
    
    public RuntimeData() { }
    
    public boolean isRunning() {
        return running;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }
    
    public int getGameViewWidth() {
        return gameViewWidth;
    }

    public void setGameViewWidth(int gameViewWidth) {
        this.gameViewWidth = gameViewWidth;
    }

    public int getGameViewHeight() {
        return gameViewHeight;
    }

    public void setGameViewHeight(int gameViewHeight) {
        this.gameViewHeight = gameViewHeight;
    }
    
    public Ball getBall() {
        return ball;
    }
    
    public void setBall(Ball ball) {
        this.ball = ball;
    }
    
    public Bar getBar() {
        return bar;
    }
    
    public void setBar(Bar bar) {
        this.bar = bar;
    }
 
    public Bricks getBricks() {
        return bricks;
    }

    public void setBricks(Bricks bricks) {
        this.bricks = bricks;
    }
   
    public int getMyScore() {
        return myScore;
    }

    public void setMyScore(int myScore) {
        this.myScore = myScore;
    }

    public int getRivalScore() {
		return rivalScore;
	}

	public void setRivalScore(int rivalScore) {
		this.rivalScore = rivalScore;
	}

	public float getBallx() {
        return ballx;
    }

    public void setBallx(float ballx) {
        this.ballx = ballx;
    }

    public float getInitballx() {
        return initballx;
    }

    public void setInitballx(float initballx) {
        this.initballx = initballx;
    }

    public float getBally() {
        return bally;
    }

    public void setBally(float bally) {
        this.bally = bally;
    }

    public float getBallXSpeed() {
        return ballXSpeed;
    }

    public void setBallXSpeed(float ballXSpeed) {
        this.ballXSpeed = ballXSpeed;
    }

    public float getBallYSpeed() {
        return ballYSpeed;
    }

    public void setBallYSpeed(float ballYSpeed) {
        this.ballYSpeed = ballYSpeed;
    }
    
    public float getInitbally() {
        return initbally;
    }

    public void setInitbally(float initbally) {
        this.initbally = initbally;
    }

    public float getInitballXSpeed() {
        return initballXSpeed;
    }

    public void setInitballXSpeed(float initballXSpeed) {
        this.initballXSpeed = initballXSpeed;
    }

    public float getInitballYSpeed() {
        return initballYSpeed;
    }

    public void setInitballYSpeed(float initballYSpeed) {
        this.initballYSpeed = initballYSpeed;
    }

    public float getBarx() {
        return barx;
    }

    public void setBarx(float barx) {
        this.barx = barx;
    }

    public float getBarLengthFactor() {
        return barLengthFacor;
    }

    public void setBarLengthFactor(float barLengthFactor) {
        this.barLengthFacor = barLengthFactor;
    }

    public float getBarXSpeed() {
        return barXSpeed;
    }

    public void setBarXSpeed(float barXSpeed) {
        this.barXSpeed = barXSpeed;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getRivalName() {
		return rivalName;
	}

	public void setRivalName(String rivalName) {
		this.rivalName = rivalName;
	}

}
