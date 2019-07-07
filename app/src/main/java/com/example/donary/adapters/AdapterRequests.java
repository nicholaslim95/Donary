package com.example.donary.adapters;

import android.content.Context;
import android.graphics.Color;
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
import java.util.List;
import java.util.Locale;


public class AdapterRequests extends RecyclerView.Adapter<AdapterRequests.MyHolder>{

    Context context;
    List<ModelRequest> requestList;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


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

        String reason = requestList.get(i).getReason();
        //get data
        String requester = requestList.get(i).getRequester();
        String requestTime = requestList.get(i).getTime();
        final ModelRequest request = requestList.get(i);

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
        myHolder.pReasonTv.setText(reason);



        myHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Request")
                        .child(request.getDonateid());

                final DatabaseReference donateRef = FirebaseDatabase.getInstance().getReference("donates");

                //once the user choose the requester, will update the firebase donated item status and also request status.
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            if (ds.getKey().equals(request.getRequester())){
                                reference.child(ds.getKey()).child("status").setValue("Accepted");
                            }
                            else{
                                reference.child(ds.getKey()).child("status").setValue("Rejected");
                            }
                        }
                        donateRef.child(request.getDonateid()).child("status").setValue("Donated");
                        Toast.makeText(context,"Donate Successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        isAccept(request.getDonateid(), request.getRequester(), myHolder.acceptBtn, myHolder.rejectBtn);
    }


    @Override
    public int getItemCount() {
        return requestList.size();
    }

    //Hide btn accept or reject based on the status in Request in firebase
    private void isAccept(final String donateId, final String requesterid, final Button btnAccept, final Button btnReject){

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Request").child(donateId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(requesterid).child("status").getValue().equals("Accepted")){
                    btnAccept.setText("Accepted");
                    btnAccept.setTextColor(Color.rgb(50,205,50));
                    btnAccept.setEnabled(false);
                    btnAccept.setVisibility(View.VISIBLE);
                    btnReject.setVisibility(View.GONE);
                }
                else if(dataSnapshot.child(requesterid).child("status").getValue().equals("Requesting")){
                    btnAccept.setText("Accept");
                    btnReject.setText("Reject");
                    btnAccept.setTextColor(Color.rgb(0,0,0));
                    btnReject.setTextColor(Color.rgb(0,0,0));
                    btnAccept.setEnabled(true);
                    btnReject.setEnabled(true);
                    btnAccept.setVisibility(View.VISIBLE);
                    btnReject.setVisibility(View.VISIBLE);
                }
                else {
                    btnReject.setText("Rejected");
                    btnReject.setEnabled(false);
                    btnReject.setTextColor(Color.rgb(255,0,0));
                    btnAccept.setVisibility(View.GONE);
                    btnReject.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views from row_post.xml
        ImageView requesterIv ;
        TextView requesterNameTv, requestTimeTv, pReasonTv;
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
            pReasonTv = itemView.findViewById(R.id.pReasonTv);
        }
    }
}
