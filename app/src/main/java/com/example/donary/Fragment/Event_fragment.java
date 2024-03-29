package com.example.donary.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.donary.CreateEvent;
import com.example.donary.R;
import com.example.donary.adapters.AdapterEventPost;
import com.example.donary.models.ModelEventPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Event_fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";

    private RecyclerView recyclerView;
    private AdapterEventPost adapterEventPost;
    private List<ModelEventPost> eventPostList;

    private List<String> followingList;
    Button btnCreateEvent;

    private FirebaseUser firebaseUser; //originally not here

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnCreateEvent = (Button) view.findViewById(R.id.btnCreateEvent);

        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateEvent.class);
                startActivity(intent);
            }
        });

        recyclerView = view.findViewById(R.id.eventPostRecyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        eventPostList = new ArrayList<>();
        adapterEventPost = new AdapterEventPost(getContext(), eventPostList);
        recyclerView.setAdapter(adapterEventPost);

        readPost();
        return view;
    }

    /*
    private void checkFollowing(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
    */
    private void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Event");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventPostList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelEventPost eventPost = snapshot.getValue(ModelEventPost.class);
                    eventPostList.add(eventPost);
                    adapterEventPost = new AdapterEventPost(getActivity(), eventPostList);
                    recyclerView.setAdapter(adapterEventPost);
                }

                adapterEventPost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
