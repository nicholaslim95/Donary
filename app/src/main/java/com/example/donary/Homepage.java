package com.example.donary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.donary.Fragment.Event_fragment;
import com.example.donary.Fragment.Donate_fragment;
import com.example.donary.Fragment.Message_fragment;
import com.example.donary.Fragment.Profile_fragment;
import com.example.donary.Fragment.Wishlist_fragment;


public class Homepage extends AppCompatActivity {

    //----------------------------------------------
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    //----------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //----------------------------------------------
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListerner);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new Donate_fragment()).commit();
        //----------------------------------------------
      /*  Log.d(TAG, "onCreate: Starting");

        mSectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        //Applying custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String commenter = intent.getString("commenter");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", commenter);
            editor.apply();

            //put intent to go to specific user profile activity
            openProfileActivity();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    ,new Donate_fragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    ,new Donate_fragment()).commit();
        }
/*
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);*/
    }

    //----------------------------------------------
    private  BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListerner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){
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

                    if(selectedFragment != null){
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

    private void openProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}