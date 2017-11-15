package com.example.zhuxiaodong.liferecorder;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

/**
 * Created by zhuxiaodong on 2017/11/14.
 */

public class NoteAsyncTask extends AsyncTask<String, String, String>{

    private Context context;
    private RecyclerView recyclerView;

    public NoteAsyncTask(Context context, RecyclerView listLayout) {
        this.context = context;
        this.recyclerView = listLayout;
    }

    @Override
    protected String doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
