package com.example.donary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.donary.Fragment.Donate_fragment;
import com.example.donary.Fragment.Event_fragment;
import com.example.donary.Fragment.Message_fragment;
import com.example.donary.Fragment.Profile_fragment;
import com.example.donary.Fragment.Wishlist_fragment;
import com.example.donary.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Homepage extends AppCompatActivity {

    //notification allert
    private int messageCount = 0;
    private static Uri alarmSound;
    //vibration pattern long array
    private final long[] pattern = {100, 300, 300, 300};
    private NotificationManagerCompat mnotificationManager;
    private String sText, sDonater, sSeen;

    //real noti
    String mUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase firebaseDatabase;

    //----------------------------------------------
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;

    //----------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //alert notification
        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mnotificationManager = NotificationManagerCompat.from(this);

        //----------------------------------------------
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListerner);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new Donate_fragment()).commit();
        //----------------------------------------------

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String commenter = intent.getString("commenter");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", commenter);
            editor.apply();

            //put intent to go to specific user profile activity
            openProfileActivity();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    , new Donate_fragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    , new Donate_fragment()).commit();
        }

        checkUserStatus();

        //update Token
        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);

    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            //user is signed in
            mUID = user.getUid();

            //save uid of currently signed in user in shared preferences
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();
        }
        else {
            //user not signed in
            startActivity(new Intent(Homepage.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //getData();

    }

    //----------------------------------------------
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListerner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new Donate_fragment();
                            break;
                        case R.id.nav_message:
                            selectedFragment = new Message_fragment();
                            break;
                        case R.id.nav_wishlist:
                            selectedFragment = new Wishlist_fragment();
                            break;
                        case R.id.nav_event:
                            selectedFragment = new Event_fragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new Profile_fragment();
                            break;
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }
                    return true;
                }
            };
    //----------------------------------------------
/*    //adding fragments to SectionPagerAdapter
    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Event_fragment(), "Event");
        adapter.addFragment(new Message_fragment(), "Donate");
        adapter.addFragment(new Donate_fragment(), "All");
        adapter.addFragment(new Wishlist_fragment(), "Wishlist");
        adapter.addFragment(new Profile_fragment(), "Profile");
        viewPager.setAdapter(adapter);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }*/

    private void openProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

/*
    public void sendOnChannel1(String Text, String donater) {

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.accept)
                .setContentTitle("Donary")
                .setContentText(donater + " " + Text)
                .setTicker("New Notification")
                .setGroup("notificationGroup")
                .setSound(alarmSound)
                .setVibrate(pattern)
                .setNumber(++messageCount)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

*/
/*        Notification summaryNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.accept)
                .setContentTitle("Donary")
                .setContentText(donater + " notification.")
                .setTicker("New Notification")
                .setGroup("notificationGroup")
                .setSound(alarmSound)
                .setVibrate(pattern)
                .setNumber(++messageCount)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();*//*


        mnotificationManager.notify(1, notification);
    }


    public void getData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                    String useridtmp = ds.child("userid").getValue().toString();
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(useridtmp);
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            sText = ds.child("text").getValue().toString();
                            sSeen = ds.child("ispost").getValue().toString();
                            sDonater = dataSnapshot.child("userName").getValue().toString();
                            compare(sText,sDonater,sSeen);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void compare(String text, String donater, String seen){
        if (seen.equals("havent")) {
            sendOnChannel1(text, donater);
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                            reference.child(ds.getKey()).child("ispost").setValue("Seen");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {

        }
    }
*/


}