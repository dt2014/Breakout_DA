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

public class OnPlayData implements Serializable {

    private static final long serialVersionUID = -7169443296866307285L;

    private volatile int score;
    private volatile int lives;
    private volatile boolean isClear; 
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
    public boolean isClear() {
        return isClear;
    }
    public void setClear(boolean clear) {
        this.isClear = clear;
    }
}
