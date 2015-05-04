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

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
	private static final String TAG = VolleySingleton.class.getName();
	private static VolleySingleton volleyInstance;
    private static Context myContext;
    private RequestQueue requestQueue;
 
    private VolleySingleton(Context context) {
    	myContext = context;
        requestQueue = getRequestQueue();
    }
    
    public static synchronized VolleySingleton getInstance(Context context) {
        if (volleyInstance == null) {
        	volleyInstance = new VolleySingleton(context);
        }
        return volleyInstance;
    }
 
    public RequestQueue getRequestQueue() {
    	if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
    		requestQueue = Volley.newRequestQueue(myContext.getApplicationContext());
        }
        return requestQueue;
    }
    
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
