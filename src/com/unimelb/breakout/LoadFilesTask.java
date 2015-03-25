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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class LoadFilesTask extends AsyncTask<RuntimeData, String, Boolean> {
    private static final String TAG = LoadFilesTask.class.getName();
    private volatile RuntimeData rData;
    
    private ProgressDialog pg;
    private  int steps = 0;
    private boolean finished;
    private Context context;
    private int updateCounter = 0;
    
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
        pg.setTitle("Load Local Level File");
        pg.setMessage("loading...");
        pg.show();
             
    }; 
    
    @Override
    protected Boolean doInBackground(RuntimeData... params) {
            rData = params[0];
            StringBuilder bs = new StringBuilder();
            bs.append("level.").append(rData.getLevel()).append(".map");
            Log.d(TAG, bs.toString());
            String fileName = bs.toString();
            String json = null;
            InputStream inputStream = null;
            try {
                //Thread.sleep(2000);
//                inputStream = context.getAssets().open(fileName);
                File levelFile = new File(context.getFilesDir().getPath() + fileName);
                Log.d(TAG, "LoadLevelTask: level file " + fileName + " " + String.valueOf(levelFile.exists()));
                publishProgress("Accessing Level File");
                inputStream = new FileInputStream(levelFile);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                json = new String(buffer, "UTF-8");
                publishProgress("Retriving Data");
                //Thread.sleep(2000);
                Log.d(TAG,json);
                if (json != null) {
                    List<Brick> brickData = Utils.extraLevelData(json, rData);
                    Bricks bricks = new Bricks(rData.getGameViewWidth(), 
                            rData.getGameViewHeight());
                    bricks.initBricks(brickData);
                    rData.setBricks(bricks);
                    //Thread.sleep(1000);
                    rData.setNewGame(true);
                    
//                    savePreferenceData();
                    
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
//            Toast.makeText(context, "Level file loaded sucessfully!", Toast.LENGTH_SHORT).show();
            if (context.getClass() == MenuActivity.class) {
                ((MenuActivity) context).callActivityForResult(MainActivity.class);
            } else if (context.getClass() == MainActivity.class) {
//                ((MainActivity) context).runOnUiThread(new Runnable() {     
//                    public void run() {
//                        ((MainActivity) context).displayReadyScreen();
//                    }});
                Bundle extras = new Bundle();
                extras.putSerializable("RUNTIME.DATA", rData);
                ((MainActivity) context).onCreate(extras);
                Log.d(TAG,"LoadFilesTask: onPostExecute");
//                Bundle extras = new Bundle();
//                extras.putSerializable("RUNTIME.DATA", rData);
//                Intent intent = new Intent();
//                intent.setClass(context, MainActivity.class);
//                intent.putExtras(extras);
//                context.startActivity(intent);
//                ((Activity) context).finish();
            } else if (context.getClass() == SelectLevelActivity.class) {
                  Bundle extras = new Bundle();
                  extras.putSerializable("RUNTIME.DATA", rData);
                  Intent intent = new Intent();
                  intent.setClass(context, MainActivity.class);
                  intent.putExtras(extras);
                  context.startActivity(intent);
                  ((Activity) context).finish();
            }
        } else if (! isCancelled()) {
            showError();
        }
        if(pg!=null)
            pg.dismiss();
    }
    
//    private void savePreferenceData() {
//        SharedPreferences sharedPref = context.
//                getSharedPreferences(MenuActivity.PREF, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putInt("SAVED.LEVEL", rData.getLevel());
//        editor.putInt("SAVED.SCORE", rData.getScore());
//        editor.putInt("SAVED.LIVES", rData.getLives());
//        editor.putString("SAVED.NAME", rData.getName());
//        editor.putBoolean("SAVED.UPLOADED", rData.isUploaded());
//        if (rData.getBricks() == null || rData.getBricks().getAliveBrickCount() == 0) {
//            editor.putString("SAVED.BRICKS", null);
//        } else {
//            Ball ball = rData.getBall();
//            Bar bar = rData.getBar();
//            editor.putFloat("SAVED.BALLX", ball.getX());
//            editor.putFloat("SAVED.BALLY", ball.getY());
//            editor.putFloat("SAVED.SPEEDX", ball.getXSpeed());
//            editor.putFloat("SAVED.SPEEDY", ball.getYSpeed());
//            editor.putFloat("SAVED.BARX", bar.getX());
//            editor.putFloat("SAVED.BARY", bar.getY());
//            editor.putFloat("SAVED.BARXSPEED", bar.getBarXSpeed());
//            String json = Utils.saveBricks(rData.getBricks());
//            editor.putString("SAVED.BRICKS", json);
//            editor.putFloat("SAVED.INITBALLX", rData.getInitballx());
//            editor.putFloat("SAVED.INITBALLY", rData.getInitbally());
//            editor.putFloat("SAVED.INITSPEEDX", rData.getInitballXSpeed());
//            editor.putFloat("SAVED.INITSPEEDY", rData.getInitballYSpeed());
//            editor.putFloat("SAVED.BARLENGTHFACTOR", rData.getBarLengthFactor());
//        }
//        editor.commit();
//    }
    
    public void showError() {
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new Builder(context);
                builder.setMessage(R.string.err_load_level);
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
