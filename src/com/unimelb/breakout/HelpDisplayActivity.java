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
