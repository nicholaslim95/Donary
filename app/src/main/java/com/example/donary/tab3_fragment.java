package com.example.donary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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


public class tab3_fragment extends Fragment {
    private static final String TAG = "Tab3Fragment";

        FirebaseAuth firebaseAuth;

        RecyclerView recyclerView;
        List<ModelPost> postList;
        AdapterPosts adapterPosts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab3_fragment, container, false);


        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.donatesRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);
        postList = new ArrayList<>();

        loadPosts();

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
                    ModelPost modelPost =  ds.getValue(ModelPost.class);

                    postList.add(modelPost);

                    adapterPosts = new AdapterPosts(getActivity(),postList);
                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


}
