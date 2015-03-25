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

import java.io.File;

import android.content.Context;
import android.content.DialogInterface;

public class DownLoadLevelListener implements DialogInterface.OnClickListener{  
   
	private boolean comfirm;
	private int level; 
	private boolean pressed = false;
	private Context context;
	private static final String TAG="Main";
	
	public DownLoadLevelListener(boolean answer, int level)
	{
		comfirm = answer;
		this.level = level;
	}
	
        @Override  
        public void onClick(DialogInterface dialog, int which) {  
        	if(comfirm)//press yes
        	{	
        	  	String levelURL = "http://128.199.134.230/level.php?levelID=";
        	  	String FILENAME = "level." + level + ".map";
//        		File root = android.os.Environment.getExternalStorageDirectory(); 
        		File file = new File (context.getFilesDir().getPath() + FILENAME);
        		
        		UpdateLevelTask UA = new UpdateLevelTask(null, file);
        		UA.setLevel(level);
        		UA.setContext(context);
        		UA.execute(levelURL);        		
        		pressed = true;     		
        	 }
        	else // press no
        	{
        		pressed = true;
        	}
        }  
        
        public boolean isPressed()
        {
        	return pressed;
        }

		public void setContext(Context context) {
			// TODO Auto-generated method stub
			this.context = context;
		}
		
		public Context getContext()
		{
			return context;
		}
        
}
