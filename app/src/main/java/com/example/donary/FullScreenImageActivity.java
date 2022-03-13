package com.example.donary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullScreenImageActivity extends AppCompatActivity {

    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        back = findViewById(R.id.back);
        ImageView fullScreenIv = (ImageView) findViewById(R.id.fullScreenIv);
        Intent callingActivityIntent = getIntent();
        if(callingActivityIntent != null){
            Uri imageUri = callingActivityIntent.getData();
            if(imageUri != null && fullScreenIv != null){
                Glide.with(this).load(imageUri).into(fullScreenIv);
            }
        }

        //back to previous
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //back previous fragment
                onBackPressed();
            }
        });
    }
}
