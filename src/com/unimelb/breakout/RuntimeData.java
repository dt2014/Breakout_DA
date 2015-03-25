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
    private volatile boolean newGame;
    private volatile boolean uploaded;
    private volatile boolean recordShow;
    
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
    
    private volatile int score;
    private volatile int next = -1;
    private volatile int rank = -1;
    private volatile int level;
    private volatile int totalLevels = 3;
    private volatile int lives;
    private volatile String name;
    
    private volatile List<RuntimeData> records;
    
    public RuntimeData() { }
    
    public boolean isRunning() {
        return running;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isNewGame() {
        return newGame;
    }

    public void setNewGame(boolean newGame) {
        this.newGame = newGame;
    }
    
    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public boolean isRecordShow() {
        return recordShow;
    }

    public void setRecordShow(boolean recordShow) {
        this.recordShow = recordShow;
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
   
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<RuntimeData> getRecords() {
        return records;
    }

    public void setRecords(List<RuntimeData> records) {
        this.records = records;
    }

    public int getTotalLevels() {
        return totalLevels;
    }

    public void setTotalLevels(int totalLevels) {
        this.totalLevels = totalLevels;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }
}
