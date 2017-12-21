package com.example.zhuxiaodong.liferecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhuxiaodong.liferecorder.DatabaseObjects.NoteItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * detailed note page for user to write and add other things
 */

public class NoteDetailed extends AppCompatActivity {
    private String currentLocation;
    private String key;
    private boolean isNewNote = true;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detailed);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final Intent intent = getIntent();
        final NoteItem note = intent.getParcelableExtra("note");
        final String keyStr = intent.getStringExtra("keyCode");
        if (note != null) {
            isNewNote = false;
            key = keyStr;
        } else {
            key = "";
        }
        final FloatingActionButton saveButton = (FloatingActionButton) findViewById(R.id.fab_note_detailed);
        final TextView locationView = (TextView) this.findViewById(R.id.note_detailed_location_view);
        final EditText titleText = (EditText) this.findViewById(R.id.note_detialed_title_view);
        final EditText contentText = (EditText) this.findViewById(R.id.note_detialed_content_view);

        if (!isNewNote) {
            locationView.setText(note.location);
            titleText.setText(note.title);
            contentText.setText(note.content);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save the note
                saveNote(titleText.getText().toString(),
                        contentText.getText().toString(),
                        locationView.getText().toString());
            }
        });

        currentLocation = (String) locationView.getText();
    }

    private void saveNote(final String title, final String content, final String location) {
        if (isNewNote) {
            DatabaseReference dbRef =
                    database.getReference().child("Users").child(currentUser.getUid()).child("Note");
            dbRef.push().setValue(new NoteItem(title, content, location));
            isNewNote = false;
            Toast.makeText(this, "your note is saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            final DatabaseReference dbRef =
                    database.getReference().child("Users").child(currentUser.getUid()).child("Note");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference ref = dataSnapshot.child(key).getRef();
                    ref.child("title").setValue(title);
                    ref.child("content").setValue(content);
                    ref.child("location").setValue(location);
                    Toast.makeText(getApplicationContext(),
                            "changes saved", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

    }

    /**
     * load the menu options
     * @param menu menu layout
     * @return super class
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //load mymenu to menu
        getMenuInflater().inflate(R.menu.notemenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * determine which option is seleted and the coorsibonding actions
     * @param item item selected
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_note:
                Toast.makeText(this, "you will save this note", Toast.LENGTH_SHORT).show();
                finish();
                //saveNote(titleText.getText(), locationView.getText(), contentText.getText());
                return true;
            case R.id.action_picture:
                Toast.makeText(this, "you will go to the picture page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),
                        PictureDetailed.class).putExtra("key", key));
                return true;
            case R.id.action_set_location:
                if (!currentLocation.equals("")) {
                    Toast.makeText(this, "location is already added", Toast.LENGTH_SHORT).show();
                } else if (isNewNote) {
                    Toast.makeText(this, "please first save the note", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Print location key", "onOptionsItemSelected: " + key);
                    startActivity(new Intent(getApplicationContext(), SettingLocation.class)
                            .putExtra("key", key));
                    finish();
                }
                return true;
            case R.id.action_comment_location:
                if (!currentLocation.equals("")) {
                    Toast.makeText(this, "you will comment on this location",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),
                            CommentingLocation.class).putExtra("location", currentLocation));
                } else {
                    Toast.makeText(this, "please first add the location", Toast.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
