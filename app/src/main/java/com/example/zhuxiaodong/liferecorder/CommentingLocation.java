package com.example.zhuxiaodong.liferecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhuxiaodong.liferecorder.DatabaseObjects.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * user can left some comment to the current location
 */

public class CommentingLocation extends AppCompatActivity {
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commenting_location);

        final Intent intent = getIntent();
        final String location = intent.getStringExtra("location");

        final Button saveButton = (Button) findViewById(R.id.make_comment_save_button);
        final TextView locationName = (TextView) findViewById(R.id.make_comment_location_name);
        final EditText contentText = (EditText) findViewById(R.id.commenting_location_content);
        locationName.setText(location);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment(location, contentText.getText().toString());

            }
        });
    }

    /**
     * adds the comment to the firebase
     * @param location location that user commenting on
     * @param content user's comment
     */
    private void addComment(String location, final String content) {
        final String address = location;
        final DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference().child("location");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        if (messageSnapshot.child("location").getValue() != null
                                && messageSnapshot.child("location").getValue().equals(address)) {
                            DatabaseReference tempRef =
                                    messageSnapshot.child("comment").getRef().push();
                            tempRef.setValue(new Comment(currentUser.getUid(), content));
                            Toast.makeText(getApplicationContext(),
                                    "Comment saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Some error occured", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getApplicationContext(),
                        "Comment saved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }
}

