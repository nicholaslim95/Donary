package com.example.donary;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EventPostFullScreenImage extends AppCompatActivity {

    private ImageView eventPostFullScreenBack, eventPostFullScreen;
    private String eventid;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_post_full_screen_image);

        eventPostFullScreenBack = findViewById(R.id.eventPostImageBack);
        eventPostFullScreen = findViewById(R.id.imgEventPostFullScreen);
        Intent intent = getIntent();
        eventid = intent.getStringExtra("eventid");

        StorageReference eventImageStorageReference = firebaseStorage.getReference();

        eventImageStorageReference.child("Event").child(eventid).child("Event image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().into(eventPostFullScreen);
                //Glide.with(EventPostFullScreenImage.this).load(uri).into(eventPostFullScreen);
            }
        });

        eventPostFullScreenBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
