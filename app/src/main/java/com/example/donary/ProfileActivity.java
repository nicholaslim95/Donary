package com.example.donary;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private TextView txt_name, txt_age, txt_email;
    private Button btn_update_profile_info;
    private ImageView img_profilePicture;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        img_profilePicture = (ImageView) findViewById(R.id.imgProfilePicture);
        txt_name = (TextView) findViewById(R.id.txtProfileName);
        txt_age = (TextView) findViewById(R.id.txtProfileAge);
        txt_email = (TextView) findViewById(R.id.txtProfileEmail);
        btn_update_profile_info = (Button) findViewById(R.id.btnUpdateProfileInfo);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(img_profilePicture);
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                System.out.println("datasnapshotb stuff" + dataSnapshot.getValue(UserProfile.class));
                System.out.println("userProfileActivity" + userProfile.getUserName());
                txt_name.setText(userProfile.getUserName());
                txt_age.setText(userProfile.getUserAge());
                txt_email.setText(userProfile.getUserEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        btn_update_profile_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, UpdateProfile.class));
            }
        });
    }
}
