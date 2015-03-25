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
    
    private volatile int viewWidth;
    private volatile int viewHeight;
    private float brickLength;
    private float brickHeight;
    
    private static final Paint normalPaint = new Paint();
    private static final Paint specialPaint = new Paint();

    public Bricks(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        brickLength = Brick.lengthFactor * viewWidth;
        brickHeight = Brick.heightFactor * viewHeight;
        normalPaint.setColor(Color.rgb(160, 82, 45)); //Sienna
        specialPaint.setColor(Color.rgb(0, 255, 127)); //Spring Green
		/*this.number = number;
		bricks = new Brick[number];
		float screenWidth = this.worldView.getWidth();
		float screenHeigth = this.worldView.getHeight();
		float length = screenWidth/11; //length of each brick;
		float width = screenHeigth/60; //width of each brick;
		float columnGap = length/11;
		float rowGap = (float) (1.8*width);
		float x = columnGap + length/2;
		float y = 5*rowGap;
		for(int i = 0; i< number; i++)
		{
			bricks[i]=new Brick(worldView,x,y,length,width);
			if((i+1)%10!=0) //means this row is not full
			{
				x = x + length + columnGap;
			}
			else
			{
				x = columnGap + length/2;
				y = y + rowGap + width;
			}
		}*/
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

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }
    
    public void setViewSize(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    public int getAliveBrickCount() {
        return aliveBrickCount;
    }
    
    // implement brick & ball collision here
	public OnPlayData onDraw(Canvas canvas, Ball ball) { 
	    OnPlayData onPlayData = new OnPlayData();
	    brickLength = Brick.lengthFactor * viewWidth;
        brickHeight = Brick.heightFactor * viewHeight;
	    float radius = Ball.getRadius();
        float ballx = ball.getX();
        float bally = ball.getY();
        boolean noCollision = true;
        for (Brick b : bricks) {
            if (b.isAlive()) {
                float brickx = b.getX() * viewWidth;
                float bricky = b.getY() * viewHeight;
                
                Rect bRect = new Rect((int)brickx, (int)bricky, 
                        (int)(brickx + brickLength), (int)(bricky + brickHeight));
                if (noCollision && bRect.intersect((int)(ballx - radius), 
                        (int)(bally - radius),(int)(ballx + radius), 
                        (int)(bally + radius))) {
                    b.setAlive(false);
                    noCollision = false;
                    aliveBrickCount--;
                    
                    if (b.isSpecial()) {
                        onPlayData.setScore(200);
                        onPlayData.setLives(1);
                    } else {
                        onPlayData.setScore(100);
                    }
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
                } else {
                    if (b.isSpecial()) {
                        canvas.drawRect(brickx, bricky, brickx + brickLength, 
                                bricky + brickHeight, specialPaint);
                    } else {
                        canvas.drawRect(brickx, bricky, brickx + brickLength, 
                                bricky + brickHeight, normalPaint);
                    }
                }
            }
        }
        if (aliveBrickCount == 0) {
            onPlayData.setClear(true);
        }
        return onPlayData;
	}

	public void validate(int gameViewWidth, float barY) {
        aliveBrickCount = 0;
        brickLength = Brick.lengthFactor * gameViewWidth;
        brickHeight = Brick.heightFactor * gameViewWidth;
        for (Brick b : bricks) {
            float brickx = b.getX() * gameViewWidth;
            float bricky = b.getY() * gameViewWidth;
            if (brickx + brickLength < gameViewWidth &&
                    bricky + brickHeight < barY) {
                b.setAlive(true);
                ++aliveBrickCount;
            } else {
                b.setAlive(false);
            }
        }
    }
    
    public void countBricks(int gameViewWidth, float barY) {
        aliveBrickCount = 0;
        brickLength = Brick.lengthFactor * gameViewWidth;
        brickHeight = Brick.heightFactor * gameViewWidth;
        for (Brick b : bricks) {
            float brickx = b.getX() * gameViewWidth;
            float bricky = b.getY() * gameViewWidth;
            if (brickx + brickLength < gameViewWidth &&
                    bricky + brickHeight < barY && b.isAlive()) {
                b.setAlive(true);
                ++aliveBrickCount;
            } else {
                b.setAlive(false);
            }
        }
    }

    public void initBricks(List<Brick> bricks) {
        this.bricks = Collections.synchronizedList(bricks);
    }

	/**After designing a new array of bricks(new maps) then call toJSON,
	*  we turn it to string(A) and save at the server, then when the user ask 
	*  for downloading a new maps, server send the String A to the client, 
	*  then client just call the FromJSON then the string turn to an array of 
	*  bricks again. then we can onDraw the bricks.  
	**/
	/*
	public String ToJSON()
	{ 
		String MessageType="NewMap";
		JSONObject obj=new JSONObject();
		JSONArray bricksJSON=new JSONArray();
		try {
			for(int i = 0; i<bricks.length;i++)
			{
				JSONObject ABrick=new JSONObject();
				ABrick.put("xPosition", Float.toString(bricks[i].getX()/worldView.getWidth()));
				ABrick.put("yPosition", Float.toString(bricks[i].getY()/worldView.getHeight()));
				ABrick.put("HitTimes", bricks[i].getHitTimes());
				bricksJSON.put(ABrick);
			}
			obj.put("Type",MessageType);
			obj.put("Number", bricks.length);	
			obj.put("Length",Float.toString(bricks[0].getLength()));//assume all bricks have same length
			obj.put("Width",Float.toString(bricks[0].getWidth()));//assume all bricks have same width
			obj.put("Data", bricksJSON);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return obj.toString();
	}
	
	public void FromJSON(String jst)
	{
		try
		{
			JSONObject jsonRoot = new JSONObject(jst);
			JSONObject temp;
			JSONArray bricksJSON=new JSONArray();
			if(jsonRoot.get("Type").equals("NewMap"))
			{	
				number = (Integer) jsonRoot.get("Number");
				bricksJSON = jsonRoot.getJSONArray("Data");
				bricks = new Brick[number];
				float length = Float.valueOf((String) jsonRoot.get("Length"));
				float width = Float.valueOf((String) jsonRoot.get("Width"));
				for(int i = 0; i<number; i++)
				{
					bricks[i] = new Brick(this.worldView);
					temp = bricksJSON.getJSONObject(i);
					bricks[i].setX(Float.valueOf(temp.getString("xPosition"))*worldView.getWidth());
					bricks[i].setY(Float.valueOf(temp.getString("yPosition"))*worldView.getHeight());
					bricks[i].setHitTimes(temp.getInt("HitTimes"));
					bricks[i].setLength(length);
					bricks[i].setWidth(width);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
