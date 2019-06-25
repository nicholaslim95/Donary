package com.example.donary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {

    Button testBtnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        testBtnLogout = (Button) findViewById(R.id.testBtnLogout);

        testBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Logout
                FirebaseAuth.getInstance().signOut();
                //Just a test, or else look back on login manager
                LoginManager.getInstance().logOut();
                openMainActivity();
            }
        });

    }

    private void openMainActivity() {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        startActivity(intent);
    }
}
