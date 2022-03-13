package com.example.donary;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    /*private TextView txt_name, txt_email;
    private Button btn_update_profile_info, btnLogout;
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
        txt_email = (TextView) findViewById(R.id.txtProfileEmail);
        btn_update_profile_info = (Button) findViewById(R.id.btnUpdateProfileInfo);
        btnLogout = (Button) findViewById(R.id.testBtnLogout);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        final DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());
        StorageReference storageReference = firebaseStorage.getReference();



        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Logout
                FirebaseAuth.getInstance().signOut();
                //Just a test, or else look back on login manager
                LoginManager.getInstance().logOut();
                openMainActivity();
            }
        });

        storageReference.child("Users").child(firebaseAuth.getUid()).child("Profile pic").child(firebaseAuth.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

    private void openMainActivity() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }*/
}
