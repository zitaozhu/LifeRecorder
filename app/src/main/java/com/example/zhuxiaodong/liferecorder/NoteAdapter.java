package com.example.zhuxiaodong.liferecorder;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuxiaodong on 2017/11/15.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{

    private List<NoteObject> noteObjectList = new ArrayList<>();

    public NoteAdapter(List<NoteObject> noteObjectList) {
        this.noteObjectList = noteObjectList;
    }

    public void addItem(NoteObject note) {
        noteObjectList.add(0, note);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public View itemView;
        public EditText contentView;
        public EditText titleView;
        public TextView titlePromptView;
        public TextView contentPromptView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.contentView = (EditText) itemView.findViewById(R.id.contentView);
            this.contentPromptView = (TextView) itemView.findViewById(R.id.contentPromptView);
            this.titleView = (EditText) itemView.findViewById(R.id.titleView);
            this.titlePromptView = (TextView) itemView.findViewById(R.id.titlePromptView);
        }
    }
}
