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

import com.example.donary.AddWishlistActivity;
import com.example.donary.R;
import com.example.donary.adapters.AdapterWishList;
import com.example.donary.models.ModelWishlist;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Wishlist_fragment extends Fragment {

    FirebaseAuth firebaseAuth;

    private RecyclerView recyclerView;
    private List<ModelWishlist> wishlist;
    private AdapterWishList adapterWishList;

    SwipeRefreshLayout pullToRefresh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.wishlistRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        wishlist = new ArrayList<>();

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

        Button btnAdd = view.findViewById(R.id.btnAddWishlist);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddWishlistActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


    private void loadPosts() {
        //load posts from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wishlist");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wishlist.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.child("status").getValue().equals("Available")){
                        ModelWishlist modelWishlist =  ds.getValue(ModelWishlist.class);
                        wishlist.add(modelWishlist);
                        adapterWishList = new AdapterWishList(getContext(), wishlist);
                        recyclerView.setAdapter(adapterWishList);
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
