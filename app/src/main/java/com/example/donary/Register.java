package com.example.donary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private Button btn_register;
    private EditText txtEmail, txtPassword, txt_username, txt_age;
    private FirebaseAuth firebaseAuth;
    private ImageView userProfilePic;

    String email, password, username, age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        txtEmail = (EditText) findViewById(R.id.txtRegisterEmail);
        txtPassword = (EditText) findViewById(R.id.txtRegisterPassword);
        txt_username = (EditText) findViewById(R.id.txtRegisterUsername);
        //Place user name here
        btn_register = (Button) findViewById(R.id.btnRegister);
        txt_age = (EditText) findViewById(R.id.txtRegisterAge);
        userProfilePic = (ImageView) findViewById(R.id.imgRegisterProfile);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    //If validation success, a new user is added into database
                    String user_email = txtEmail.getText().toString().trim();
                    String user_password = txtPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                               sendEmailVerification();
                            }else{
                                Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });


    }

    private Boolean validate(){

        Boolean result = false;
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();
        username = txt_username.getText().toString();
        age = txt_age.getText().toString();

        if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(Register.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }else{
            result = true;
        }
        return result;
    }

    private void sendEmailVerification(){
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        System.out.println("Happens before upload user");
                        sendUserData(); //considered placing in after verification
                        System.out.println("Happens after upload user");
                        Toast.makeText(Register.this, "Successfully registered, verification mail sent", Toast.LENGTH_SHORT).show();
                        //Because .createUserWithEmailAndPassword actually signs in users, so sign out 1st to wait verification
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Register.this, MainActivity.class));
                    }else{
                        Toast.makeText(Register.this, "Verification mail hasn't been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        System.out.println("The uid " + firebaseAuth.getUid());
        System.out.println("Database reference: " + myRef );
        UserProfile userProfile = new UserProfile(age, email, username);
        System.out.println("userProfile.age" + userProfile.getUserAge());
        System.out.println("userProfile.email" + userProfile.getUserEmail());
        System.out.println("userProfile.username" + userProfile.getUserName());
        myRef.setValue(userProfile);
    }
}
