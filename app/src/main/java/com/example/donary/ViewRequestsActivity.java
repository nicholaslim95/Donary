package com.example.donary;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.donary.adapters.AdapterRequests;
import com.example.donary.models.ModelRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelRequest> requestList;
    AdapterRequests AdapterRequests;
    View view;
    SwipeRefreshLayout pullToRefresh;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);


        firebaseAuth = FirebaseAuth.getInstance();

        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.requestRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ViewRequestsActivity.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);
        requestList = new ArrayList<>();

        //pull down refresh
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRequests(); // your get data code
                pullToRefresh.setRefreshing(false);
            }
        });
        loadRequests();

        //back to hoempage
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //back previous fragment
                onBackPressed();
            }
        });

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

    private void loadRequests() {
        //load requests from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request").child("-Li3-aVwnz9DI5ENUT3K");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requestList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelRequest modelRequest =  ds.getValue(ModelRequest.class);

                    requestList.add(modelRequest);

                    AdapterRequests = new AdapterRequests(ViewRequestsActivity.this,requestList);
                    recyclerView.setAdapter(AdapterRequests);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewRequestsActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
