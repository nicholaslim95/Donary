package com.example.donary;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfile extends AppCompatActivity {

    private EditText newUsername, newUserEmail, newUserAge;
    private Button btn_save_profile_info, btn_change_password;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        newUsername = (EditText) findViewById(R.id.txtUpdateProfileName);
        newUserEmail = (EditText) findViewById(R.id.txtUpdateProfileEmail);
        newUserAge = (EditText) findViewById(R.id.txtUpdateProfileAge);

        btn_save_profile_info = (Button) findViewById(R.id.btnSaveProfileInformation);
        btn_change_password = (Button) findViewById(R.id.btnChangePassword);

        //will populate current profile information to let user see current profile information
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                newUsername.setText(userProfile.getUserName());
                newUserAge.setText(userProfile.getUserAge());
                newUserEmail.setText(userProfile.getUserEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        btn_save_profile_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = newUsername.getText().toString();
                String age =  newUserAge.getText().toString();
                String email = newUserEmail.getText().toString();

                UserProfile userProfile = new UserProfile(age, email, name);

                databaseReference.setValue(userProfile);
                Toast.makeText(UpdateProfile.this, "Profile updated.", Toast.LENGTH_SHORT).show();
            }
        });

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfile.this, ChangePassword.class));
            }
        });
    }
}
