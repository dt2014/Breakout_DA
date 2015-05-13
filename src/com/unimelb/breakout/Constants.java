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
	public static String SEVER_URL = "http://letsbreakout.comxa.com/apiV4.php";
//	public static String SEVER_URL = "http://192.168.0.4/apiV4.php";
	
	/**
	 * Timing in milliseconds
	 */
	public static final int INITGAME_TASK_TIMEOUT = 10000;
	public static final int NETWORK_TIMEOUT = 6000;
	public static final int TRANSFER_TIMEOUT = 6000;
	public static final int GAME_COUNTDOWN = 5000;
	public static final int COUNTDOWN_INTERVAL = 1000;
	public static final int GAME_THREAD_SLEEP = 60;
	
	/**
	 * Counts for game-thread-sleep rounds for sending network requests to update game dynamics
	 */
//	public static final int UPDATE_BALL = 
	
	public static final float GAMEVIEW_HEIGHT_FACTOR = 0.85f;
	
	/**
	 *  actual ballRadius = screenWidth * BALL_RADIUS_FACTOR
	 */
	public static final float BALL_RADIUS_FACTOR = 0.015f;
	
	/**
	 * The initial position and speed of the balls
	 * actual ballInitX = screenWidth * BALL_INIT_X_FACTOR
	 * actual ballInitY = screenHeight * BALL_INIT_Y_FACTOR
	 * actual ballInitXSpeed = screenWidth * BALL_INIT_X_FACTOR
	 * actual ballInitYSpeed = screenHeight * BALL_INIT_YSPEED_FACTOR
	 */
	public static final float BALL_INIT_X_FACTOR = 0.5f;
	public static final float BALL_INIT_Y_FACTOR = 0.6f;
	public static final float BALL_INIT_XSPEED_FACTOR = 0.005f;
	public static final float BALL_INIT_YSPEED_FACTOR = 0.005f;
	
	/**
	 * 	barLength = screenWidth * BAR_LENGTH_FACTOR
	 *  barHeight = screenHeight * BAR_HEIGHT_FACTOR
	 */
    public static final float BAR_LENGTH_FACTOR = 0.7f;
    public static final float BAR_HEIGHT_FACTOR = 0.02f;
    
    /**
     * The initial position of barX for both self and rival sides
	 * actual barInitX = screenWidth * INIT_BAR_X_FACTOR
	 */
    public static final float BAR_INIT_X_FACTOR = (1 - BAR_LENGTH_FACTOR) / 2;
    
    /**
     * The position of barY
     * actual myBarY = screenHeight * MY_BAR_Y_FACTOR
     * actual rivalBarY = screenHeight * RIVAL_BAR_Y_FACTOR
     */
    public static final float BAR_INIT_Y_FACTOR = 0.95f;
    public static final float OPPOSITE_BAR_Y_FACTOR = 1 - BAR_INIT_Y_FACTOR - BAR_HEIGHT_FACTOR;
    
	/**
	 *  birckLength = screenWidth * BRICK_LENGTH_FACTOR
	 *  brickHeight = screenHeight * BAR_HEIGHT_FACTOR
	 */
    public static final float BRICK_LENGTH_FACTOR = 0.07f;
    public static final float BRICK_HEIGHT_FACTOR = 0.02f;
}
