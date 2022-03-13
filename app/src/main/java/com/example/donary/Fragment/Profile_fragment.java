package com.example.donary.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donary.MainActivity;
import com.example.donary.R;
import com.example.donary.UpdateProfile;
import com.example.donary.UserProfile;
import com.facebook.login.LoginManager;
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


public class Profile_fragment extends Fragment {
    private static final String TAG = "Tab4Fragment";

    Button btnCreateEvent;

    private TextView txt_name, txt_write_something;
    private Button btn_update_profile_info, btnLogout;
    private ImageView img_profilePicture;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Profile_fragment profile_fragment = getFragmentManager().findFragmentById(R.layout.fragment_profile);
        super.onCreate(savedInstanceState);

        img_profilePicture = (ImageView) view.findViewById(R.id.imgProfilePicture);
        txt_name = (TextView) view.findViewById(R.id.txtProfileName);
        txt_write_something = (TextView) view.findViewById(R.id.txtProfileWriteSomething);
        btn_update_profile_info = (Button) view.findViewById(R.id.btnUpdateProfileInfo);
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
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
                String name = userProfile.getUserName();
                System.out.println("datasnapshotb stuff" + dataSnapshot.getValue(UserProfile.class));
                System.out.println("userProfileActivity" + userProfile.getUserName());
                txt_name.setText(name);
                txt_write_something.setText(userProfile.getWriteSomethingAboutYourself());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        btn_update_profile_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateProfile.class));
            }
        });
        return view;
    }

    private void openMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment currentFragment = getFragmentManager().findFragmentByTag("Profile_fragment");
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();
    }
}
