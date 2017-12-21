package com.example.zhuxiaodong.liferecorder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;

/**
 * detailed picture page that let user to add picture
 * or view the existing one
 */

public class PictureDetailed extends AppCompatActivity {
    private ImageView imageView;
    private static final int CAMERA_REQUEST = 1888;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detailed);
        key = getIntent().getStringExtra("key");

        final Button addPicButton = (Button) findViewById(R.id.add_photo_button);
        imageView = (ImageView) findViewById(R.id.imageView);

        loadImageFromFireBase();

        addPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    /**
     * load the picture from fire base as a string
     * some parts of the code are originally from
     * https://stackoverflow.com/questions/26292969/can-i-store-image-files-in-firebase-using-java-api
     */
    private void loadImageFromFireBase() {
        DatabaseReference dbRef = database.getReference().child("image");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    if (messageSnapshot.getKey().equals(key) && messageSnapshot.getValue() != null) {
                        imageView.setImageBitmap(stringToBitMap
                                (messageSnapshot.getValue(String.class)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    //call the camera to get picture
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            saveToFireBase(photo);
        }
    }

    public Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    //upload the picture to firebase as string
    private void saveToFireBase(Bitmap photo) {
        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
        //see if its already being recycled
        if (photo != null && !photo.isRecycled()) {
            photo.recycle();
            photo = null;
            imageView.setImageBitmap(null);
        }
        byte[] byteArray = bYtE.toByteArray();
        String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

        DatabaseReference dbRef = database.getReference().child("image");
        dbRef.child(key).setValue(imageFile);
        Toast.makeText(getApplicationContext(),
                "Pic is saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
