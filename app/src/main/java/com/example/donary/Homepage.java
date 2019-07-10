package com.example.donary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.donary.Fragment.Tab3_fragment;
import com.example.donary.Fragment.tab1_fragment;
import com.example.donary.Fragment.tab2_fragment;
import com.example.donary.Fragment.tab4_fragment;
import com.example.donary.Fragment.tab5_fragment;


public class Homepage extends AppCompatActivity {

/*
    private static final String TAG = "Homepage";

    private SectionsPageAdapter mSectionPageAdapter;

    private ViewPager mViewPager;

    //Implementing action bar from action_menu.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.item_profile:
                openProfileActivity();
                return true;
            case R.id.item_settings:
                openSettingsActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/

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
                new tab1_fragment()).commit();
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
                    ,new Tab3_fragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    ,new tab1_fragment()).commit();
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
                            selectedFragment = new tab1_fragment();
                            break;
                        case R.id.nav_donate:
                            selectedFragment = new tab2_fragment();
                            break;
                        case R.id.nav_wishlist:
                            selectedFragment = new Tab3_fragment();
                            break;
                        case R.id.nav_event:
                            selectedFragment = new tab4_fragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new tab5_fragment();
                            //Intent intent = new Intent(Homepage.this, ProfileActivity.class);
                            //startActivity(intent);
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
        adapter.addFragment(new tab1_fragment(), "Event");
        adapter.addFragment(new tab2_fragment(), "Donate");
        adapter.addFragment(new Tab3_fragment(), "All");
        adapter.addFragment(new tab4_fragment(), "Wishlist");
        adapter.addFragment(new tab5_fragment(), "Profile");
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