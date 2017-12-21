package com.example.zhuxiaodong.liferecorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.zhuxiaodong.liferecorder.DatabaseObjects.NoteItem;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = "Message";
    private final int SIGN_IN_REQUEST_CODE = 123;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private RecyclerView noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.take_some_note);
        }

        noteList = (RecyclerView) findViewById(R.id.main_note_list);
        noteList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );

        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    getString(R.string.welcome) + FirebaseAuth.getInstance().getCurrentUser()
                            .getDisplayName(), Toast.LENGTH_LONG).show();

            //NoteAsyncTask asyncTask = new NoteAsyncTask(this, noteList);
            try {
                loadNotes();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //asyncTask.execute(listOfNotes);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();
                Intent noteDetailPageIntent = new Intent(context, NoteDetailed.class);
                context.startActivity(noteDetailPageIntent);
            }
        });

    }

    /**
     * loads the notes form the firebase
     * @throws InterruptedException
     */
    private void loadNotes() throws InterruptedException {
        if (currentUser == null) {
            Toast.makeText(this, R.string.not_logged_in, Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference dbRef =
                database.getReference().child("Users").child(currentUser.getUid()).child("Note");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {
                    //if theres no notes, load the auto generated one
                    HashMap<NoteItem, String> map = new HashMap<NoteItem, String>();
                    map.put(new NoteItem("Welcome wrtite your first note", "", ""), "");
                    setNoteValue(map);
                }
                HashMap<NoteItem, String> keyMap = new HashMap<>();

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    keyMap.put(messageSnapshot.getValue(NoteItem.class), messageSnapshot.getKey());
//                    NoteItem note = new NoteItem(messageSnapshot
//                            .child(getString(R.string.title)).getValue().toString(),
//                            messageSnapshot.child(getString(R.string.content)).getValue().toString(),
//                            messageSnapshot.child(getString(R.string.location)).getValue().toString());
                }
                setNoteValue(keyMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * run async task to lead to recycler view
     * @param notes  notes going to be loaded
     */
    private void setNoteValue(HashMap<NoteItem, String> notes) {
        for (NoteItem note : notes.keySet()) {
            Log.d("Print", "setNoteValue: " + note.title);
        }
        NoteAsyncTask asyncTask = new NoteAsyncTask(this, noteList);
        asyncTask.execute(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //load mymenu to menu
        getMenuInflater().inflate(R.menu.mymenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * for the selection of toolbar
     * @param item item in the toolbar thats selected
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_note:
                Toast.makeText(this, "you will add a new note", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), NoteDetailed.class));
                return true;
            case R.id.action_sign_out:
                Toast.makeText(this, "you will be signed out", Toast.LENGTH_SHORT).show();
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this,
                                        "You have been signed out.",
                                        Toast.LENGTH_LONG)
                                        .show();

                                // Close activity
                                finish();
                            }
                        });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //sign in sign out

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference dbRef =
                        database.getReference().child("Users").child(currentUser.getUid()).child("Note");
                dbRef.push();
                try {
                    loadNotes();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }

    }
}
