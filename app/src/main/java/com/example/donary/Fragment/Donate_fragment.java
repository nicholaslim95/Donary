package com.example.donary.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.donary.AddDonationActivity;
import com.example.donary.R;
import com.example.donary.adapters.AdapterPosts;
import com.example.donary.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Donate_fragment extends Fragment {

        FirebaseAuth firebaseAuth;

        private RecyclerView recyclerView;
        private List<ModelPost> postList;
        private AdapterPosts adapterPosts;

    SwipeRefreshLayout pullToRefresh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate, container, false);


        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.wishlistRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        postList = new ArrayList<>();

        //pull down refresh
        pullToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPosts(); // your get data code
                pullToRefresh.setRefreshing(false);
            }
        });
        loadPosts();

        Button btnAdd = view.findViewById(R.id.btnAddDonation);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddDonationActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


    private void loadPosts() {
        //load posts from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("donates");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.child("status").getValue().equals("Available")){
                        ModelPost modelPost =  ds.getValue(ModelPost.class);
                        postList.add(modelPost);
                        adapterPosts = new AdapterPosts(getContext(),postList);
                        recyclerView.setAdapter(adapterPosts);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
