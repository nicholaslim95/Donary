package com.example.donary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class Homepage extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Log.d(TAG, "onCreate: Starting");

        mSectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        //Applying custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String commenter = intent.getString("commenter");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", commenter);
            editor.apply();

            //put intent to go to specific user profile activity
            openProfileActivity();
            /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    ,new Tab3_fragment()).commit();*/
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    ,new tab1_fragment()).commit();
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    //adding fragments to SectionPagerAdapter
    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new tab1_fragment(), "Tab1Fragment");
        adapter.addFragment(new tab2_fragment(), "Tab2Fragment");
        adapter.addFragment(new Tab3_fragment(), "Tab3Fragment");
        viewPager.setAdapter(adapter);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    private void openProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}