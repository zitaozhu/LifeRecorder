package com.example.zhuxiaodong.liferecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by zhuxiaodong on 2017/11/15.
 */

public class NoteDetailedActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detailed);

        final Intent intent = getIntent();

        final EditText contentView = (EditText) findViewById(R.id.contentView);
        final EditText titleView = (EditText) findViewById(R.id.titleView);
        final TextView titlePromptView = (TextView) findViewById(R.id.titlePromptView);
        final TextView contentPromptView = (TextView) findViewById(R.id.contentPromptView);


    }
}
