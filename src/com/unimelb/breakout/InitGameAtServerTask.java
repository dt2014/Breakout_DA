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

import java.lang.reflect.Type;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

public class InitGameAtServerTask extends AsyncTask<RuntimeData, String, Boolean> {
	private static final String TAG = InitGameAtServerTask.class.getName();
    private volatile RuntimeData rData;
    
	private ProgressDialog pg;
    private int steps = 0;
	private Context context;
	
	public InitGameAtServerTask(Context context) {
		this.context = context;
	}
	
	
	@Override
    protected void onPreExecute() {
		super.onPreExecute();         
        pg = new ProgressDialog(context);
        pg.setCancelable(false);
        pg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pg.setProgress(0);
        pg.setMax(100);
        pg.setTitle("Initiate New Game");
        pg.setMessage("Connecting server...");
        pg.show();
    }
	
	
	@Override
	protected Boolean doInBackground(RuntimeData... params) {
		rData = params[0];
		BufferedReader in = null;
		try {
//			URL serverURL = new URL(Constants.SEVER_URL);
//			URLConnection urlConnection = serverURL.openConnection();
//			urlConnection.setConnectTimeout(Constants.NETWORK_TIMEOUT);
//			urlConnection.setReadTimeout(Constants.TRANSFER_TIMEOUT);
//			urlConnection.setRequestProperty("user-agent", "Android");
//			urlConnection.setDoOutput(true);
//			urlConnection.setDoInput(true);
//            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
//            String body = "command=start&player_name=" + rData.getMyName();
//            out.print(body);
//            out.flush();
//            out.close();
//            in = new BufferedReader(new InputStreamReader(
//            		urlConnection.getInputStream(), "UTF-8"));
//            String json = in.readLine();
			
			String json = "{\"response\":\"start\", \"map_side\":\"A\", \"rival_name\":\"Haha\"}";
	        if (json != null) {
				Gson gson = new Gson();
				JsonObject jobj = gson.fromJson(json, JsonObject.class);
				if (jobj.get("response").getAsString().equals("start")) {
					rData.setMapSide(jobj.get("map_side").getAsString());
					rData.setRivalName(jobj.get("rival_name").getAsString());
					return Boolean.TRUE;
				}
	        }
		}  catch (Exception e) {
			
		} finally {
			try {
				if (in != null) {
					in.close();
				} 
			
			} catch (Exception e) {
			}		
		}
		return Boolean.FALSE;
	}

	@Override
    protected void onProgressUpdate(String... params) {
		super.onProgressUpdate(params);
        if (pg != null) {
        	steps=steps+1;
            pg.setMessage(params[0]);
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
	protected void onPostExecute(Boolean result) {
		if (! isCancelled() && result) {
		final LoadFilesTask task = new LoadFilesTask(context);
          if (context.getClass() == MenuActivity.class) {
        	  task.execute(rData);
          } else { 
          	showError();
          }
      } else if (! isCancelled()) {
          showError();
      }
      if(pg!=null)
          pg.dismiss();
	}
	 
	public void showError() {
	        ((Activity) context).runOnUiThread(new Runnable() {
	            public void run() {
	                AlertDialog.Builder builder = new Builder(context);
	                builder.setMessage(R.string.err_init_game);
	                builder.setCancelable(true);
	                builder.setPositiveButton(R.string.lbl_back, new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.cancel();
	                        if (context.getClass().isInstance(MainActivity.class)) {
	                            ((Activity) context).finish();
	                        }
	                    }
	                });
	                builder.create().show();
	            }
	        });
	    }
}
