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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SelectLevelActivity extends ListActivity {
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
        
        ListView levelList = (ListView)findViewById(android.R.id.list);
        int availablelevel = 0;
        for (int i = 1; i <= 3; ++i) {
            String fileName = "level." + i + ".map";
            File levelFile = new File(this.getFilesDir().getPath() + fileName);
            if (levelFile.exists()) {
                availablelevel++;
            }
        }
        
        List<String> levels = new ArrayList<String>(availablelevel);
        for (int i = 0; i < availablelevel; ++i) {
            levels.add(this.getString(R.string.lbl_level) + " " + (i + 1));
        }
        RuntimeDataAdapter adapter = new RuntimeDataAdapter(R.layout.level, levels);
        levelList.setAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {
        rData.setLevel(position + 1);
        rData.setLives(3);
        rData.setScore(0);
        rData.setNext(-1);
        rData.setRank(-1);
        rData.setUploaded(false);
        final LoadFilesTask task = new LoadFilesTask(this);
        task.execute(rData);
        new Thread() {
            @Override
            public void run() {
                try {
                    task.get(5000, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    task.cancel(true);
                    task.showError();
                }
            }
        }.start();
    }
    
    public void onClickReturn(View v) {
        this.finish();
    }
    
    private class RuntimeDataAdapter extends ArrayAdapter<String> {
        public RuntimeDataAdapter(int view, List<String> levels) {
            super(SelectLevelActivity.this, view, levels);
        }
        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = SelectLevelActivity.this.getLayoutInflater().inflate(R.layout.level, null);
            }
            String text = getItem(position);
            TextView level = (TextView) convertView.findViewById(R.id.level);
            level.setText(text);
            return convertView;
        }
    }
}
