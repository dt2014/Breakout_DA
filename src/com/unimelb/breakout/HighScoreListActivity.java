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

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HighScoreListActivity extends ListActivity {
    //private static final String TAG = SelectLevelActivity.class.getName();
    private volatile RuntimeData rData;
    
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
        setContentView(R.layout.list);
        ListView recordList = (ListView)findViewById(android.R.id.list);
        RuntimeDataAdapter adapter = new RuntimeDataAdapter(R.layout.record, rData.getRecords());
        recordList.setAdapter(adapter);
    }
    
    public void onClickReturn(View v) {
        this.finish();
    }
    
    private class RuntimeDataAdapter extends ArrayAdapter<RuntimeData> {
        public RuntimeDataAdapter(int view, List<RuntimeData> records) {
            super(HighScoreListActivity.this, view, records);
        }
        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = HighScoreListActivity.this.getLayoutInflater().inflate(R.layout.record, null);
            }
            RuntimeData rData = getItem(position);
            
            TextView rank = (TextView) convertView.findViewById(R.id.recRank);
            rank.setText("#"+ (position + 1));
            TextView name = (TextView) convertView.findViewById(R.id.recName);
            name.setText(rData.getName());
            TextView score = (TextView) convertView.findViewById(R.id.recScore);
            score.setText(String.valueOf(rData.getScore()));
            if (rData.getName().equals(HighScoreListActivity.this.rData.getName())) {
                rank.setTextColor(Color.CYAN);
                score.setTextColor(Color.CYAN);
                name.setTextColor(Color.CYAN);
            }
            return convertView;
        }
    }
}
