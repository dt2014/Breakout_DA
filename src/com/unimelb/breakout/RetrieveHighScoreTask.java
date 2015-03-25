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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class RetrieveHighScoreTask extends AsyncTask<RuntimeData, Void, List<RuntimeData>> {
//	private TextView scoreField;

	OutputStream out = null;
	InputStream in = null;

	private volatile RuntimeData rData;
	private Context context;

	public RetrieveHighScoreTask(RuntimeData rData) {
		this.rData = rData;
	}

    @Override
	protected List<RuntimeData> doInBackground(RuntimeData... params) {
	    rData = params[0];
	    List<RuntimeData> result = null;
        String url = "http://128.199.134.230/retrieveLeaderboard.php";
        StringBuffer sb = new StringBuffer("");
        String json = null;
        
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			BufferedReader in = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
//			String line = "";
//			while ((line = in.readLine()) != null) {
//				sb.append(line);
//				break;
//			}

            json = in.readLine();
			if (json != null) {

//	            Log.d("json onRetrieve from server", json);
                Gson gson = new Gson();
                JsonObject jobj = gson.fromJson(json, JsonObject.class);
                Type listType = new TypeToken<ArrayList<RuntimeData>>() {}.getType();

                 result = Collections.synchronizedList((List<RuntimeData>)gson.fromJson(jobj.get("records"), listType));
                 rData.setRecords(result);
                 //Utils.insert(rData);
             }
			
			in.close();

//			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
//			return "Failed!" + e;
		}
		return result;
	}

	@Override
	protected void onPostExecute(List<RuntimeData> result) {
	    if (!isCancelled() && result != null && rData.isRecordShow()) {
	        if (context.getClass() == MenuActivity.class) {
                ((MenuActivity) context).callActivityForResult(HighScoreListActivity.class);
            } else if (context.getClass() == MainActivity.class) {
                Bundle extras = new Bundle();
                extras.putSerializable("RUNTIME.DATA", rData);
                Intent intent = new Intent();
                intent.setClass(context, HighScoreListActivity.class);
                intent.putExtras(extras);
                context.startActivity(intent);
                ((Activity) context).finish();
            } 
        } else if (isCancelled() || result == null) {
            showError();
        }
		
	}
	
	public void setContext(Context context) {
        // TODO Auto-generated method stub
        this.context = context;
    }
    
    public Context getContext()
    {
        return context;
    }

    public void showError() {
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new Builder(context);
                builder.setMessage("Error occured during retrieving the High Score List. "
                        + "Please try again later.");
                if (context.getClass() == MenuActivity.class && ! rData.isRecordShow()) {
                    builder.setMessage("Error occured during retrieving the High Score List "
                            + "from server. You may still play the game but the data of 'Next'"
                            + "and 'Rank' may not be accurate.");
                }
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
