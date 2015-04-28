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

public class Brick implements Serializable{
    private static final long serialVersionUID = 3947787005416186023L;
    
    private volatile int brickId;
    
    private volatile float brickX;
	private volatile float brickY;
	
	private boolean alive;
	private boolean special;
	
	public Brick(int brickId, float brickX, float brickY, boolean special){
		this.brickId = brickId;
		this.brickX = brickX;
		this.brickY = brickY;
        this.special = special;
        this.alive = true;
	}
	
	public int getId() {
		return brickId;
	}

	public float getBrickX(){
		return brickX;
	}
	
	public float getBrickY() {
		return brickY;
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
}
