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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class HelpDisplayActivity extends Activity {
    
//    private static final String TAG = HelpDisplayActivity.class.getName();
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        //Button btn = (Button)findViewById(R.string.lbl_back);
    }
    
    public void onClick(View v) {
        HelpDisplayActivity.this.finish();
    }
}
