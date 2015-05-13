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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class InitGameAtServerTask extends AsyncTask<RuntimeData, String, Boolean> {
	private static final String TAG = InitGameAtServerTask.class.getName();
    private volatile RuntimeData rData;
    
	private ProgressDialog progressDialog;
    private int steps = 0;
	private Context context;
	
	public InitGameAtServerTask(Context context) {
		this.context = context;
	}
	
	@Override
    protected void onPreExecute() {
		super.onPreExecute();         
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(3);
        progressDialog.setTitle("Initiate New Game");
        progressDialog.setMessage("Connecting server...");
        progressDialog.show();
    }
	
	@Override
	protected Boolean doInBackground(RuntimeData... params) {
		rData = params[0];
		BufferedReader in = null;
		try {
			publishProgress("Connecting server...");
			String url = Constants.SEVER_URL + "?command=start&n=" + rData.getMyName();
			URL serverURL = new URL(url);
			URLConnection urlConnection = serverURL.openConnection();
			urlConnection.setConnectTimeout(Constants.NETWORK_TIMEOUT);
			urlConnection.setReadTimeout(Constants.TRANSFER_TIMEOUT);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			publishProgress("Waiting for response from server...");
            in = new BufferedReader(new InputStreamReader(
            		urlConnection.getInputStream(), "UTF-8"));
            String json = in.readLine();

	        if (json != null) {
	        	publishProgress("Parsing response from server...");
	            Log.i(TAG, json);
				Gson gson = new Gson();
				JsonObject jobj = gson.fromJson(json, JsonObject.class);
				if (jobj.get("response").getAsString().equals("start")) {
					rData.setMapSide(jobj.get("map_side").getAsString());
					rData.setRivalName(jobj.get("rival_name").getAsString());					
					return Boolean.TRUE;
				} else if (jobj.get("response").getAsString().equals("No rival")) {
					progressDialog.dismiss();
					Utils.showError(context, R.string.err_no_rival);
					this.cancel(true);
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
        if (progressDialog != null) {
        	steps = steps + 1;
            progressDialog.setMessage(params[0]);
            progressDialog.setProgress(steps);
            progressDialog.show();
            if(steps >= 3) {	
            	progressDialog.dismiss();
            	steps = 0;
            }
        }
    }  
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (!isCancelled() && result) {
			final LoadFilesTask task = new LoadFilesTask(context);
			if (context.getClass() == MenuActivity.class) {
				task.execute(rData);
			} else {
				Utils.showError(context, R.string.err_init_game);
			}
		} else if (!isCancelled()) {
			Utils.showError(context, R.string.err_init_game);
		}
		if (progressDialog != null)
			progressDialog.dismiss();
	}
}