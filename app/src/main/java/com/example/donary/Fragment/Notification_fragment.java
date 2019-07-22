package com.example.donary.Fragment;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.donary.R;
import com.example.donary.adapters.AdapterNotification;
import com.example.donary.models.ModelNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Notification_fragment extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterNotification notificationAdapter;
    List<ModelNotification> notificationList;
    SwipeRefreshLayout pullToRefresh;
    ImageView back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notification);

        back = findViewById(R.id.btnnoti_back);
        recyclerView = findViewById(R.id.notiRecyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Notification_fragment.this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        notificationList = new ArrayList<>();

        notificationAdapter = new AdapterNotification(Notification_fragment.this, notificationList);
        recyclerView.setAdapter(notificationAdapter);
        //back to hoempage
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //back previous fragment
                onBackPressed();
            }
        });

        //pull down refresh
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.notipullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readNotification(); // your get data code
                pullToRefresh.setRefreshing(false);
            }
        });
        readNotification();

    }


    //back to previous fragment
    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void readNotification() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelNotification notification = snapshot.getValue(ModelNotification.class);
                    notificationList.add(notification);
                }

                Collections.reverse(notificationList);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
