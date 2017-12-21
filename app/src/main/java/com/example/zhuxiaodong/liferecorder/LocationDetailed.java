package com.example.zhuxiaodong.liferecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhuxiaodong.liferecorder.DatabaseObjects.Comment;
import com.example.zhuxiaodong.liferecorder.DatabaseObjects.NoteItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * a detaield location page that have user's comments
 * in a recycler view
 */

public class LocationDetailed extends AppCompatActivity {
    private RecyclerView commentListView;
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_detailed);

        final Intent intent = getIntent();
        final NoteItem note = intent.getParcelableExtra("location");

        final TextView locationNameView = (TextView) findViewById(R.id.location_detailed_name_view);
        final Button addCommentButton = (Button) findViewById(R.id.location_detailed_add_comment);

        commentListView = (RecyclerView) findViewById(R.id.location_detailed_comment_list);
        commentListView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        final String locationString = note.location;
        locationNameView.setText(locationString);


        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CommentingLocation.class)
                        .putExtra("location", locationString));
                finish();
            }
        });

        findLocationAndLoadComments(locationString);
    }

    /**
     * find the database reference of the current location
     */
    private void findLocationAndLoadComments(String str) {
        final String address = str;
        final DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference().child("location");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        if(messageSnapshot.child("location").getValue() != null
                                && messageSnapshot.child("location").getValue().equals(address)) {
                            Comment[] temp = new Comment[(int) messageSnapshot.child("comment").getChildrenCount()];
                            int i = 0;
                            for(DataSnapshot snapShot : messageSnapshot.child("comment").getChildren()) {
                                temp[i++] = snapShot.getValue(Comment.class);
                            }
                            LocationAsyncTask asyncTask = new LocationAsyncTask(LocationDetailed.this, commentListView);
                            asyncTask.execute(temp);
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Some error occured", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

}