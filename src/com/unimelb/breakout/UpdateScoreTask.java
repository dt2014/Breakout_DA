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

import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateScoreTask extends AsyncTask<String, Integer, String> {
    private ProgressDialog pg;

	OutputStream out = null;
	MainActivity main;

	public UpdateScoreTask(MainActivity main) {
	    this.main = main;
    }
	
	@Override
    protected void onPreExecute() {
        pg = new ProgressDialog(main);
        pg.setCancelable(false);
        pg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pg.setProgress(0);
        pg.setMax(100);
        pg.setMessage("Uploading...");
        pg.show();
        super.onPreExecute();              
    }; 

	@Override
	protected String doInBackground(String... urls) {
		try {
		    /* 27Mar_Daphne: Temporarily comment out code. Change requesting server to accessing local list.
			String url = urls[0];
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
		    */
		    /* 27Mar_Daphne: Temporarily Local High Score List*/
		    RuntimeData highScoreEntry = new RuntimeData();
		    highScoreEntry.setScore(Integer.valueOf(urls[0]).intValue());
		    highScoreEntry.setName(urls[1]);
		    List<RuntimeData> highScoreList = main.getrData().getRecords();
		    if(highScoreList == null) {
		        highScoreList = new ArrayList<RuntimeData>();
		        highScoreList.add(highScoreEntry);
		        RuntimeData fillingEntry = new RuntimeData();
		        fillingEntry.setName("");
		        fillingEntry.setScore(0);
		        for(int i = 0; i < 9; i++) {
		            highScoreList.add(fillingEntry);
		        }
		    } else {
		        for(int i = 0; i < 10; i++) {
	                if(highScoreEntry.getScore() > highScoreList.get(i).getScore()) {
	                    highScoreList.add(i, highScoreEntry);
	                    break;
	                }
	            }
		        Log.i("Record size", String.valueOf(highScoreList.size()));
	            if(highScoreList.size() > 10) {
	                highScoreList.remove(10);
	            }
		    }
		    main.getrData().setRecords(highScoreList);
		    SharedPreferences sharedPref = main.getSharedPreferences(MenuActivity.PREF, Context.MODE_PRIVATE);
	        SharedPreferences.Editor editor = sharedPref.edit();
	        editor.putString("SAVED.RECORDS", Utils.saveRecords(highScoreList));
	        editor.commit();
		    
			pg.dismiss();
			return "ok";

		} catch (Exception e) {
			e.printStackTrace();
			return "Failed!" + e;
		}
	}
	
	@Override
    public void onProgressUpdate(Integer... params) {
	    UpdateScoreTask.this.pg.setProgress(params[0]);
    }

	@Override
	protected void onPostExecute(String result) {
	    if (result.equalsIgnoreCase("ok")) {
	        Log.i("updatescore", "ok");
	        main.getrData().setUploaded(true);
	        main.getrData().setRecordShow(true);
	        main.runOnUiThread(new Runnable() {
	            public void run() {
	                RetrieveHighScoreTask task = new RetrieveHighScoreTask(main.getrData());
	                task.setContext(main);
	                task.execute(main.getrData());
	            }
	        });	        
	    } else {
	        AlertDialog.Builder builder = new Builder(main);
            builder.setMessage("Error occurred when uploading high score. ");
            builder.setCancelable(false);
            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    int score = main.getrData().getScore();
                    String username = main.getrData().getName();
                    String scoreURL = "http://128.199.134.230/updateScore.php?score="+score+"&username="+username;
                    new UpdateScoreTask(main).execute(scoreURL);
                }
            });
            builder.setNegativeButton("Cancel Upload", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    main.getrData().setUploaded(true);
                    main.finish();
                }
            });
            builder.create().show();
	    }
	}
}