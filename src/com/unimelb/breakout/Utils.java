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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public final class Utils {

    private Utils() {}

    public static final List<Brick> extraMapData(String json) {
        Gson gson = new Gson();
        JsonObject jobj = gson.fromJson(json, JsonObject.class);
        
        Type bricksType = new TypeToken<ArrayList<Brick>>() {}.getType();
        return gson.fromJson(jobj.get("bricks"), bricksType);
    }
    
    public static final List<Brick> buildBricks(String json) {
        Gson gson = new Gson();
        JsonObject jobj = gson.fromJson(json, JsonObject.class);
        Type listType = new TypeToken<ArrayList<Brick>>() {}.getType();
        return gson.fromJson(jobj.get("bricks"), listType);
    }
    
    public static final String saveBricks(Bricks brickData) {
        Gson gson = new Gson();
        String json = gson.toJson(brickData);
        return json;
    }
    
    public static final String nameInitials(String name) {
        StringBuilder nameInitials = new StringBuilder("");
        nameInitials.append(name.charAt(0) + ".");
        for(int i = 1; i < name.length(); ++i) {
            if (name.charAt(i) == ' ') {
                nameInitials.append(name.charAt(i + 1) + ".");
            }
        }
        return nameInitials.toString().toUpperCase(Locale.ENGLISH);
    }
    
    public static void showError(final Context context, final int errMsg) {
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new Builder(context);
                builder.setMessage(errMsg);
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
    
    public static void deActivateFromServer(Context context, String command, String name) {
    	String url = Constants.SEVER_URL + "?command=" + command + "&n=" + name;
    	Log.d("deActivateFromServer", url);
    	StringRequest stopGameRequest = new StringRequest(Request.Method.GET, url,
    			new Response.Listener<String>() {
    	    @Override
    	    public void onResponse(String response) {
    	    }
    	}, new Response.ErrorListener() {
    	    @Override
    	    public void onErrorResponse(VolleyError error) {
    	    	error.printStackTrace();
    	    }
    	});
    	VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stopGameRequest);
    }
}
