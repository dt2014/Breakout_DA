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

    private volatile boolean gameIsOn = true;
    private volatile boolean bricksAreGone;
    private volatile boolean ownershipChanged;
    
    
    public boolean gameIsOn() {
        return gameIsOn;
    }
    public void setGameOn(boolean gameIsOn) {
        this.gameIsOn = gameIsOn;
    }
    public boolean bricksAreGone() {
        return bricksAreGone;
    }
    public void setBricksGone(boolean bricksAreGone) {
        this.bricksAreGone = bricksAreGone;
    }
	public boolean isOwnershipChanged() {
		return ownershipChanged;
	}
	public void setOwnershipChanged(boolean ownershipChanged) {
		this.ownershipChanged = ownershipChanged;
	}
}
