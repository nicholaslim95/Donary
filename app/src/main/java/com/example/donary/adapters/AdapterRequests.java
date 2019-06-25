package com.example.donary.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donary.R;
import com.example.donary.UserProfile;
import com.example.donary.ViewRequestsActivity;
import com.example.donary.models.ModelRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class AdapterRequests extends RecyclerView.Adapter<AdapterRequests.MyHolder>{

    Context context;
    List<ModelRequest> requestList;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase;


    public AdapterRequests(Context context, List<ModelRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_requests, viewGroup, false);
        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {

        //get data
        String requester = requestList.get(i).getRequester();
        String requestTime = requestList.get(i).getTime();

        //convert time to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(requestTime));
        String sTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set user default pic
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child("Users").child(requester).child("Profile pic").child(requester).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).placeholder(R.drawable.ic_default_img).into(myHolder.requesterIv);
            }
        });

        //display the user name
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(requester);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                myHolder.requesterNameTv.setText(userProfile.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //set the requester data
        myHolder.requestTimeTv.setText(sTime);



        myHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Request Cancelled", Toast.LENGTH_LONG).show();
            }
        });


    }


    @Override
    public int getItemCount() {
        return requestList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views from row_post.xml
        ImageView requesterIv ;
        TextView requesterNameTv, requestTimeTv;
        Button acceptBtn, rejectBtn;
        LinearLayout requesterLayout;

        public MyHolder(@NonNull View itemView){
            super(itemView);

            //init views
            requesterNameTv = itemView.findViewById(R.id.requesterNameTv);
            requestTimeTv= itemView.findViewById(R.id.requesterTimeTv);
            requesterIv = itemView.findViewById(R.id.requesterIv);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);
            requesterLayout = itemView.findViewById(R.id.requesterLayout);
        }
    }
}
