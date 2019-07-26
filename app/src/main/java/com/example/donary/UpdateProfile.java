package com.example.donary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class UpdateProfile extends AppCompatActivity {

    private EditText newUsername, newWriteSomethingAboutYourself;
    private Button btn_save_profile_info, btn_change_password;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ImageView img_update_profile_pic;
    private FirebaseStorage firebaseStorage;

    private StorageReference storageReference; //Storage reference is the Firebase root directory
    private static int PICK_IMAGE = 123;
    Uri imagePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                img_update_profile_pic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        newUsername = (EditText) findViewById(R.id.txtUpdateProfileName);
        newWriteSomethingAboutYourself = (EditText) findViewById(R.id.txtUpdateProfileWriteSomething);
        img_update_profile_pic = (ImageView) findViewById(R.id.imgUpdateProfilePic);

        btn_save_profile_info = (Button) findViewById(R.id.btnSaveProfileInformation);
        btn_change_password = (Button) findViewById(R.id.btnChangePassword);

        //will populate current profile information to let user see current profile information
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        firebaseStorage =  FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        storageReference.child("Users").child(firebaseAuth.getUid()).child("Profile pic").child(firebaseAuth.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(img_update_profile_pic);
            }
        });

        final DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                newUsername.setText(userProfile.getUserName());
                newWriteSomethingAboutYourself.setText(userProfile.getWriteSomethingAboutYourself());
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
                String writeSomething = newWriteSomethingAboutYourself.getText().toString();
                UserProfile userProfile = new UserProfile(name,  firebaseAuth.getUid());
                userProfile.setWriteSomethingAboutYourself(writeSomething);
                databaseReference.setValue(userProfile);

                if(imagePath != null){
                    StorageReference imageReference = storageReference.child("Users").child(firebaseAuth.getUid()).child("Profile pic").child(firebaseAuth.getUid());
                    UploadTask uploadTask = imageReference.putFile(imagePath);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateProfile.this, "Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(UpdateProfile.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                Toast.makeText(UpdateProfile.this, "Profile updated.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfile.this, ChangePassword.class));
            }
        });

        storageReference = firebaseStorage.getReference();

        storageReference.child("Users").child(firebaseAuth.getUid()).child("Profile pic").child(firebaseAuth.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(img_update_profile_pic);
            }
        });

        img_update_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*"); //application/pdf OR application/*(for documentation), audio/* OR audio/mp3
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE);
            }
        });
    }
}
