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

public class Constants {
	/**
	 * 
	 */
	public static String SEVER_URL = "http://letsbreakout.comxa.com/api.php";
	public static final int NETWORK_TIMEOUT = 2000;
	public static final int TRANSFER_TIMEOUT = 3000;
	
	
	/**
	 *  ballRadius = screenWidth * BALL_RADIUS_FACTOR
	 */
	public static final float BALL_RADIUS_FACTOR = 0.015f;
	
	/**
	 * The initial position and speed of the balls
	 * ballInitX = screenWidth * BALL_INIT_X_FACTOR
	 * ballInitY = screenHeight * BALL_INIT_Y_FACTOR
	 * ballInitXSpeed = screenWidth * BALL_INIT_X_FACTOR
	 * ballInitYSpeed = screenHeight * BALL_INIT_YSPEED_FACTOR
	 */
	public static final float BALL_INIT_X_FACTOR = 0.5f;
	public static final float BALL_INIT_Y_FACTOR = 0.6f;
	public static final float BALL_INIT_XSPEED_FACTOR = 0.005f;
	public static final float BALL_INIT_YSPEED_FACTOR = 0.005f;
	
	/**
	 * 	barLength = screenWidth * BAR_LENGTH_FACTOR
	 *  barHeight = screenHeight * BAR_HEIGHT_FACTOR
	 */
    public static final float BAR_LENGTH_FACTOR = 0.4f;
    public static final float BAR_HEIGHT_FACTOR = 0.02f;
    
    /**
     * The initial position of barX for both self and rival sides
	 * barInitX = screenWidth * INIT_BAR_X_FACTOR
	 */
    public static final float BAR_INIT_X_FACTOR = (1 - BAR_LENGTH_FACTOR) / 2;
    
    /**
     * The position of barY
     * myBarY = screenHeight * MY_BAR_Y_FACTOR
     * rivalBarY = screenHeight * RIVAL_BAR_Y_FACTOR
     */
    public static final float MY_BAR_Y_FACTOR = 0.95f;
    public static final float RIVAL_BAR_Y_FACTOR = 1 - MY_BAR_Y_FACTOR - BAR_HEIGHT_FACTOR;
    
	/**
	 *  birckLength = screenWidth * BRICK_LENGTH_FACTOR
	 *  brickHeight = screenHeight * BAR_HEIGHT_FACTOR
	 */
    public static final float BRICK_LENGTH_FACTOR = 0.07f;
    public static final float BRICK_HEIGHT_FACTOR = 0.025f;
}
