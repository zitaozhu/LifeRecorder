package com.example.zhuxiaodong.liferecorder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhuxiaodong.liferecorder.DatabaseObjects.NoteItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * adapter for loading the notes.
 */

class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private static final int ENDINDEX = 200;
    private List<NoteItem> noteList = new ArrayList<>();
    private HashMap<NoteItem, String> map;

    public NoteAdapter(HashMap<NoteItem, String> noteItems) {
        //noteList.addAll(Arrays.asList());
        map = noteItems;
        Log.d("Print", "NoteAdapter: " + noteItems.keySet().toArray().length);

        for (NoteItem item : noteItems.keySet()) {
            noteList.add(item);
            Log.d("Print", "NoteAdapter: " + item.title);
        }

        Log.d("Print", "NoteAdapter: " + noteList.get(0).title);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View noteItem = LayoutInflater.from(parent.getContext()).
                inflate(viewType, parent, false);

        return new ViewHolder(noteItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NoteItem note = noteList.get(position);
        holder.nameView.setText(note.title);
        holder.locationView.setText(note.location);
        holder.locationView.setTextColor(Color.BLUE);
        String content = note.content;
        if (content.length() > ENDINDEX) {
            content = content.substring(ENDINDEX);
        }
        holder.contentView.setText(content);

        holder.locationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();
                Intent locationDetailPageIntent = new Intent(context, LocationDetailed.class);
                locationDetailPageIntent.putExtra("location", note);
                context.startActivity(locationDetailPageIntent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();
                Intent noteDetailedPage = new Intent(context, NoteDetailed.class);
                noteDetailedPage.putExtra("note", note);
                Log.d("key", "onClick: " + map.get(note));
                noteDetailedPage.putExtra("keyCode", map.get(note));
                context.startActivity(noteDetailedPage);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.note_item;
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void addItem(NoteItem note) {
        noteList.add(0, note);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public TextView nameView;
        public TextView locationView;
        public TextView contentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.nameView = (TextView) itemView.findViewById(R.id.note_item_title_view);
            this.locationView = (TextView) itemView.findViewById(R.id.note_item_location_view);
            this.contentView = (TextView) itemView.findViewById(R.id.note_item_content_view);
        }
    }
}