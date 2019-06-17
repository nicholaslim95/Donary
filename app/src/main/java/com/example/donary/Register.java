package com.example.donary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Register extends AppCompatActivity {

    private Button btn_register;
    private EditText txtEmail, txtPassword, txt_username, txt_age;
    private FirebaseAuth firebaseAuth;
    private ImageView userProfilePic;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference; //Storage reference is the Firebase root directory
    private static int PICK_IMAGE = 123;
    Uri imagePath;
    String email, password, username, age;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                userProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        //StorageReference myRef1 = storageReference.child(firebaseAuth.getUid()); //This will save file under Firebase UID as a child data

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

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*"); //application/pdf OR application/*(for documentation), audio/* OR audio/mp3
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE);
            }
        });



    }

    private Boolean validate(){

        Boolean result = false;
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();
        username = txt_username.getText().toString();
        age = txt_age.getText().toString();

        if(username.isEmpty() || email.isEmpty() || password.isEmpty() || imagePath == null){
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
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());
        StorageReference imageReference = storageReference.child("Users").child(firebaseAuth.getUid()).child("Profile pic").child(firebaseAuth.getUid());
        UploadTask uploadTask = imageReference.putFile(imagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Register.this, "Upload successful", Toast.LENGTH_SHORT).show();
            }
        });
        UserProfile userProfile = new UserProfile(age, email, username);
        myRef.setValue(userProfile);
    }
}
