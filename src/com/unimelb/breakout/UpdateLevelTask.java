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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class UpdateLevelTask extends AsyncTask<String, String, String> {
	private TextView levelField;

//	private OutputStream out = null;
//	private InputStream in = null;
//	private Context ctx;
	private File file;
	private ProgressDialog pg;
	private  int steps = 0;
	private int level;
	private boolean finished;
	private Context context;
	private static final String TAG="Main";
	static Object lock = new Object();
	
	public UpdateLevelTask(TextView level,File file) {
		this.levelField = level;
		this.file = file;
	}
	
	
	@Override
    protected void onPreExecute()
    {
        pg = new ProgressDialog(context);
        pg.setCancelable(false);
        pg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pg.setProgress(0);
        pg.setMax(100);
        pg.setTitle("Updating Level " + Integer.toString(level));
        pg.setMessage("Loading...");
        pg.show();
        Log.i(TAG,"7");
        super.onPreExecute();              
    }; 
	
	
	@Override
	protected String doInBackground(String... urls) {
		try {

			String url = urls[0] + Integer.toString(level);
			Log.i(TAG,"url is " + url);
//			StringBuffer sb = new StringBuffer("");
			
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			
			publishProgress("Accessing Server");
			
			HttpResponse response = client.execute(request);
			
			
			publishProgress("Reading Data");
			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
			Log.i(TAG,"2");
			
			
			//char[] buffer = new char[1024];
			String line = in.readLine(); 
			Log.i(TAG,"3");
			publishProgress("Reading Finish");
		
			Log.i(TAG,"4");
			in.close();
			Log.i(TAG,"5");
			
			Log.i(TAG,line);

			return line;
		

		} catch (Exception e) {
			return null;
		}

	}

	@Override
    protected void onProgressUpdate(String... progress) {  
		Log.i(TAG,"8");
		super.onProgressUpdate(progress);
        if (pg != null) {
        	steps=steps+1;
            pg.setMessage(progress[0]);
            pg.setProgress(Math.round(100*((float)steps)/6));
            pg.show();
            if(steps == 6)
            {	
            	pg.dismiss();
            	steps = 0;
            }
        }
    }  
	
	@Override
	protected void onPostExecute(String result) {
		
		super.onPostExecute(result);
		Log.i(TAG,"Actually returns " + result);
		if(levelField!=null)
			this.levelField.setText("Data downloaded: " + result);

	  	//after acuqired the downloaded string(level) the followint code will write it to 
	  	//the phone's external directory. 
		FileOutputStream fos;
		
		
		
		if(result == null)
		{
			result = "Data retrives failed";
		}
		
		else
		{
			try {
				publishProgress("Writing Data To Local File");
					
				fos = new FileOutputStream(file);
				fos.write(result.getBytes());
				fos.close();
			
				publishProgress("Writing Finished");
			
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				;
		        
			}
			publishProgress("Updata Finish");	
		}
		if(pg!=null)
			pg.dismiss();
		finished = true;
		
		 final LoadFilesTask task = new LoadFilesTask(context);
		 if (context.getClass() == MenuActivity.class ) {
		     task.execute(((MenuActivity) context).getrData());
		 } else if(context.getClass() == MainActivity.class ) {
		     task.execute(((MainActivity) context).getrData());
		 }
         
         
		Log.i(TAG,"levle "+Integer.toString(level) + " complete");
	}

	public void setLevel(int Newlevel) {
		// TODO Auto-generated method stub
		level = Newlevel;
	}

	public void setContext(Context context) {
		// TODO Auto-generated method stub
		this.context = context;
	}
	
	public Context getContext()
	{
		return context;
	}

	public ProgressDialog getProgressDialog()
	{
		return pg;
	}
	
	public void dismissDialog()
	{
		if(pg!=null)
			pg.dismiss();
	}


	public boolean isRunning() {
		// TODO Auto-generated method stub
		if( pg!=null&&pg.isShowing())
			return true;
		else
			return false;
	}


	public boolean finished() {
		// TODO Auto-generated method stub
		return finished;
	}
	

}
