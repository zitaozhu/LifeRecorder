package com.example.zhuxiaodong.liferecorder;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.zhuxiaodong.liferecorder.DatabaseObjects.Comment;

/**
 * Asynctask fot loaction page
 */

public class LocationAsyncTask extends AsyncTask<Comment[], Integer, Comment[]> {

    private Context context;
    private RecyclerView recyclerView;

    public LocationAsyncTask(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    protected final Comment[] doInBackground(Comment[]... params) {
        Log.d("dajiba", "doInBackground: " + params[0].length);
        return params[0];
    }


    @Override
    protected void onPostExecute(Comment[] comments) {
        if (comments == null || comments.length == 0) {
            return;
        }

        Log.d("dajiba", "onPostExecute: ");
        final LocationAdapter adapter = new LocationAdapter(comments);

        for (final Comment comment : comments) {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.addItem(comment);
                }
            }, 0);
        }

        adapter.notifyDataSetChanged();
    }
}