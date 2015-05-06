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


import java.io.InputStream;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class LoadFilesTask extends AsyncTask<RuntimeData, String, Boolean> {
    private static final String TAG = LoadFilesTask.class.getName();
    private volatile RuntimeData rData;
    
    private ProgressDialog progressDialog;
    private int steps = 0;
    private Context context;
    
    public LoadFilesTask (Context context) {
        this.context = context;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();         
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.setTitle("Load Map File");
        progressDialog.setMessage("loading...");
        progressDialog.show();
             
    }; 
    
    @Override
    protected Boolean doInBackground(RuntimeData... params) {
            rData = params[0];
            String fileName = "map.json";
            String json = null;
            InputStream inputStream = null;
            try {
                //Thread.sleep(2000);
                publishProgress("Accessing Game Map");
                
                inputStream = context.getAssets().open(fileName);
                
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                json = new String(buffer, "UTF-8");
                publishProgress("Retriving Data");
                //Thread.sleep(2000);
//                Log.d(TAG,json);
                if (json != null) {
                    List<Brick> brickData = Utils.extraMapData(json);
                    Bricks bricks = new Bricks();
                    bricks.initBricks(brickData);
//                    Log.d(TAG, String.valueOf(bricks.getBricks() == null)); // false
//                    Log.d(TAG, String.valueOf(bricks.getBricks().get(5).isAlive()));
//                    Log.d(TAG, String.valueOf(bricks.getBricks().get(5).getId()));
//                    Log.d(TAG, String.valueOf(bricks.getBricks().get(5).isSpecial()));
                    rData.setBricks(bricks);
                    //Thread.sleep(1000);
                    publishProgress("Retriving Done");
                    return Boolean.TRUE;
                }
            } catch (Exception e) {
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    } 
                } catch (Exception e) {
                }
            }
        return Boolean.FALSE;
    }
    
    @Override
    public void onProgressUpdate(String... params) {
        super.onProgressUpdate(params);
        if (progressDialog != null) {
            steps=steps+1;
            progressDialog.setMessage(params[0]);
            progressDialog.setProgress(Math.round(100*((float)steps)/6));
            progressDialog.show();
            if(steps == 6)
            {   
                progressDialog.dismiss();
                steps = 0;
            }
        }
    }
    
    @Override
    public void onPostExecute(Boolean result) {
        if (! isCancelled() && result) {
//            Toast.makeText(context, "Map file loaded sucessfully!", Toast.LENGTH_SHORT).show();
            if (context.getClass() == MenuActivity.class) {
                ((MenuActivity) context).callActivityForResult(MainActivity.class);
            } else { 
            	Utils.showError(context, R.string.err_load_map);
            }
        } else if (! isCancelled()) {
        	Utils.showError(context, R.string.err_load_map);
        }
        if(progressDialog!=null)
            progressDialog.dismiss();
    }
}
