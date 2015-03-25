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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
			String url = urls[0];

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			//return "Your score is: "+" Congragulations! You have reach the highest score!";
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

            //Intent intent = new Intent();
            //intent.setClass(act, ShowLeaderBoardActivity.class);
	        //main.startActivity(intent);
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