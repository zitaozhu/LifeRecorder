package com.example.zhuxiaodong.liferecorder;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.zhuxiaodong.liferecorder.DatabaseObjects.NoteItem;

import java.util.HashMap;

/**
 * Created by zhuxiaodong on 2017/12/5.
 */

public class NoteAsyncTask extends AsyncTask<HashMap<NoteItem, String>, Integer, HashMap<NoteItem, String> > {

    private Context context;
    private RecyclerView recyclerView;

    public NoteAsyncTask(Context context, RecyclerView listLayout) {
        this.context = context;
        this.recyclerView = listLayout;
    }

    @Override
    protected HashMap doInBackground(HashMap<NoteItem, String>... params) {
        for(NoteItem note : params[0].keySet()) {
            Log.d("Initial Print", "doInBackground: " + note.title);
        }
        return params[0];
    }

    @Override
    protected void onPostExecute(HashMap<NoteItem, String> noteItems) {
        if(noteItems == null || noteItems.size() == 0) {
            Log.d("Error", "onPostExecute: empty array");
            return ;
        }

        final NoteAdapter adapter = new NoteAdapter(noteItems);

        for(final NoteItem note : noteItems.keySet()) {
            Log.d("Print", "onPostExecute: " + note.title);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false));

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.addItem(note);
                }
            }, 0);
        }

        adapter.notifyDataSetChanged();
    }
}