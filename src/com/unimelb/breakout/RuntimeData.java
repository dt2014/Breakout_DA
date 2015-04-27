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

public class RuntimeData implements Serializable {    
    private static final long serialVersionUID = 8666060483413961394L;
    
    private volatile boolean running;
    
    private volatile int gameViewWidth;
    private volatile int gameViewHeight;
    
    private volatile Ball ball1;
    private volatile float ball1X;
    private volatile float ball1Y;
    private volatile float ball1XSpeed;
    private volatile float ball1YSpeed;

    private volatile Ball ball2;
    private volatile float ball2X;
    private volatile float ball2Y;
    private volatile float ball2XSpeed;
    private volatile float ball2YSpeed;
    
    private volatile float initBallX; // factor to screen view
    private volatile float initBallY; // factor to screen view
    private volatile float initBallXSpeed; // factor to screen view
    private volatile float initBallYSpeed; // factor to screen view
    
    private volatile Bar myBar;
    private volatile float myBarX;
    private volatile float myBarXSpeed;
    
    private volatile Bar rivalBar;
    private volatile float rivalBarX;
    private volatile float rivalBarXSpeed;
    
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
    
    public Ball getBall1() {
        return ball1;
    }
    
    public void setBall1(Ball ball1) {
        this.ball1 = ball1;
    }
    
    public Bar getMyBar() {
        return myBar;
    }
    
    public void setMyBar(Bar myBar) {
        this.myBar = myBar;
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

	public float getBall1X() {
        return ball1X;
    }

    public void setBall1X(float ball1X) {
        this.ball1X = ball1X;
    }

    public float getInitBallX() {
        return initBallX;
    }

    public void setInitBallX(float initBallX) {
        this.initBallX = initBallX;
    }

    public float getBall1Y() {
        return ball1Y;
    }

    public void setBall1Y(float ball1Y) {
        this.ball1Y = ball1Y;
    }

    public float getBall1XSpeed() {
        return ball1XSpeed;
    }

    public void setBall1XSpeed(float ball1XSpeed) {
        this.ball1XSpeed = ball1XSpeed;
    }

    public float getBall1YSpeed() {
        return ball1YSpeed;
    }

    public void setBall1YSpeed(float ball1YSpeed) {
        this.ball1YSpeed = ball1YSpeed;
    }
    
    public Ball getBall2() {
		return ball2;
	}

	public void setBall2(Ball ball2) {
		this.ball2 = ball2;
	}

	public float getBall2X() {
		return ball2X;
	}

	public void setBall2X(float ball2x) {
		ball2X = ball2x;
	}

	public float getBall2Y() {
		return ball2Y;
	}

	public void setBall2Y(float ball2y) {
		ball2Y = ball2y;
	}

	public float getBall2XSpeed() {
		return ball2XSpeed;
	}

	public void setBall2XSpeed(float ball2xSpeed) {
		ball2XSpeed = ball2xSpeed;
	}

	public float getBall2YSpeed() {
		return ball2YSpeed;
	}

	public void setBall2YSpeed(float ball2ySpeed) {
		ball2YSpeed = ball2ySpeed;
	}

	public float getInitBallY() {
        return initBallY;
    }

    public void setInitBallY(float initBallY) {
        this.initBallY = initBallY;
    }

    public float getInitBallXSpeed() {
        return initBallXSpeed;
    }

    public void setInitBallXSpeed(float initBallXSpeed) {
        this.initBallXSpeed = initBallXSpeed;
    }

    public float getInitBallYSpeed() {
        return initBallYSpeed;
    }

    public void setInitBallYSpeed(float initBallYSpeed) {
        this.initBallYSpeed = initBallYSpeed;
    }

    public float getMyBarX() {
        return myBarX;
    }

    public void setMyBarX(float myBarX) {
        this.myBarX = myBarX;
    }

    public float getMyBarXSpeed() {
        return myBarXSpeed;
    }

    public void setMyBarXSpeed(float myBarXSpeed) {
        this.myBarXSpeed = myBarXSpeed;
    }

    public Bar getRivalBar() {
		return rivalBar;
	}

	public void setRivalBar(Bar rivalBar) {
		this.rivalBar = rivalBar;
	}

	public float getRivalBarX() {
		return rivalBarX;
	}

	public void setRivalBarX(float rivalBarX) {
		this.rivalBarX = rivalBarX;
	}

	public float getRivalBarXSpeed() {
		return rivalBarXSpeed;
	}

	public void setRivalBarXSpeed(float rivalBarXSpeed) {
		this.rivalBarXSpeed = rivalBarXSpeed;
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
