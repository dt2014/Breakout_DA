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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class PlayerActivity extends Activity {
//    private static final String TAG = PlayerActivity.class.getName();
    private volatile RuntimeData rData;
    private volatile EditText playerName;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            rData = (RuntimeData) savedInstanceState.getSerializable("RUNTIME.DATA");
        }
        if (rData == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                rData = (RuntimeData) extras.getSerializable("RUNTIME.DATA");
            }
        }
        setContentView(R.layout.player);
    }

    public void onClickConfirm(View v) {
        playerName = (EditText)findViewById(R.id.player_name);
        String name = playerName.getText().toString().trim();
        
        if (name != null && name.matches("[a-zA-Z ]+")) {
            rData.setMyName(name);
            rData.setMyScore(0);
            SharedPreferences sharedPref = this.
                    getSharedPreferences(MenuActivity.PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("SAVED.MYNAME", rData.getMyName());
            editor.putInt("SAVED.SCORE", rData.getMyScore());
            editor.commit();
            
            Bundle extras = new Bundle();
            extras.putSerializable("RUNTIME.DATA", rData);
            Intent intent = new Intent();
            intent.putExtras(extras);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            AlertDialog.Builder builder = new Builder(this);
            builder.setMessage(R.string.player_name_msg);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        }
    }
}
