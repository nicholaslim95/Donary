package com.example.donary.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.donary.R;
import com.example.donary.adapters.AdapterMessage;
import com.example.donary.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Message_fragment extends Fragment {
    FirebaseAuth firebaseAuth;

    RecyclerView recyclerView;
    List<ModelChat> message;
    AdapterMessage adapterMessage;

    SwipeRefreshLayout pullToRefresh;

    String myuid;

    //to store user that chat with current user
    List<String> senderlist;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = view.findViewById(R.id.messageRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        senderlist = new ArrayList<String>();

        recyclerView.setLayoutManager(layoutManager);
        message = new ArrayList<>();

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

        Button btnNoti = view.findViewById(R.id.btnNotification);
        btnNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Notification_fragment.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void loadPosts() {
        //load posts from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        final int count = 0;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                message.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String sender = ds.child("sender").getValue().toString();
                    String receiver = ds.child("receiver").getValue().toString();
                    if(receiver.equals(myuid)){
                        if(!senderlist.contains(sender)){
                            senderlist.add(sender);
                            ModelChat modelChat =  ds.getValue(ModelChat.class);
                            message.add(modelChat);
                            adapterMessage = new AdapterMessage(getActivity(), message);
                            recyclerView.setAdapter(adapterMessage);
                        }
                    }else if(sender.equals(myuid)){
                        if(!senderlist.contains(receiver)){
                            senderlist.add(receiver);
                            ModelChat modelChat =  ds.getValue(ModelChat.class);
                            message.add(modelChat);
                            adapterMessage = new AdapterMessage(getActivity(), message);
                            recyclerView.setAdapter(adapterMessage);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


}
