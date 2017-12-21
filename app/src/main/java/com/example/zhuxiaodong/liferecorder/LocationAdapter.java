package com.example.zhuxiaodong.liferecorder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhuxiaodong.liferecorder.DatabaseObjects.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * adapter for location page's recycler view that loads the comments
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private List<Comment> commentList = new ArrayList<>();

    public LocationAdapter(Comment[] commentList) {

        this.commentList.addAll(Arrays.asList(commentList));
        Log.d("jiba", "LocationAdapter: " + this.commentList.size());
    }

    public void addItem(Comment comment) {
        commentList.add(0, comment);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.comment_item;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View commentItem = LayoutInflater.from(parent.getContext()).
                inflate(viewType, parent, false);

        return new ViewHolder(commentItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Comment comment = commentList.get(position);
        final String name = comment.getName();
        final String content = comment.getComment();
        Log.d("jiba", "onBindViewHolder: " + name + " " + content);
        holder.nameView.setText(name);
        holder.commentView.setText(content);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView;
        public TextView commentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.nameView = (TextView) itemView.findViewById(R.id.comment_item_name_view);
            this.commentView = (TextView) itemView.findViewById(R.id.comment_item_comment_view);
        }
    }
}
