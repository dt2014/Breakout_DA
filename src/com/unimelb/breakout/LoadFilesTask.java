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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

public class LoadFilesTask extends AsyncTask<RuntimeData, String, Boolean> {
    private static final String TAG = LoadFilesTask.class.getName();
    private volatile RuntimeData rData;
    
    private ProgressDialog pg;
    private int steps = 0;
    private Context context;
    
    public LoadFilesTask (Context context) {
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
        pg.setTitle("Load Map File");
        pg.setMessage("loading...");
        pg.show();
             
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
                    List<Brick> brickData = Utils.extraMapData(json, rData);
                    Bricks bricks = new Bricks(rData.getGameViewWidth(), 
                            rData.getGameViewHeight());
                    bricks.initBricks(brickData);
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
    public void onPostExecute(Boolean result) {
        if (! isCancelled() && result) {
//            Toast.makeText(context, "Map file loaded sucessfully!", Toast.LENGTH_SHORT).show();
            if (context.getClass() == MenuActivity.class) {
                ((MenuActivity) context).callActivityForResult(MainActivity.class);
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
                builder.setMessage(R.string.err_load_map);
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
