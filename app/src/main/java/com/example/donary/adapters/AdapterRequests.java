package com.example.donary.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
import com.example.donary.notifications.APIService;
import com.example.donary.notifications.Client;
import com.example.donary.notifications.Data;
import com.example.donary.notifications.Response;
import com.example.donary.notifications.Sender;
import com.example.donary.notifications.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;


public class AdapterRequests extends RecyclerView.Adapter<AdapterRequests.MyHolder> {

    Context context;
    List<ModelRequest> requestList;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    APIService apiService;
    boolean notify = false;

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

        //create Aapi service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

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
                notify = true;
                final int[] quantity = new int[1];
                final int[] fixloop = {1}; //fix the bug purpose
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Request")
                        .child(request.getDonateid());

                final DatabaseReference donateRef = FirebaseDatabase.getInstance().getReference("donates");
                donateRef.child(request.getDonateid()).child("quantity").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        quantity[0] = Integer.parseInt(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //once the user choose the requester, will update the firebase donated item status and also request status.
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {

                        donateRef.child(request.getDonateid()).child("quantity").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(fixloop[0] > 0){
                                    //if the item only left 1, after accept the last acceptor, will auto reject others
                                    if (dataSnapshot.getValue().equals("1")) {
                                        for (DataSnapshot ds : dataSnapshot1.getChildren()) {
                                            if (ds.getKey().equals(request.getRequester())) {
                                                reference.child(ds.getKey()).child("status").setValue("Accepted");
                                                donateRef.child(request.getDonateid()).child("quantity").setValue(String.valueOf(quantity[0] - 1));
                                                addNotification(ds.getKey(), ds.child("donateid").getValue().toString(), "accepted your request.");
                                            } else {
                                                reference.child(ds.getKey()).child("status").setValue("Rejected");
                                                addNotification(ds.getKey(), ds.child("donateid").getValue().toString(), "rejected your request.");
                                            }
                                        }
                                    } else if (Integer.parseInt(dataSnapshot.getValue().toString()) >= 2) {
                                        reference.child(request.getRequester()).child("status").setValue("Accepted");
                                        donateRef.child(request.getDonateid()).child("quantity").setValue(String.valueOf(quantity[0] - 1));
                                        addNotification(request.getRequester(), request.getDonateid(), "accepted your request.");
                                    }
                                    fixloop[0]--;
                                }

                                if (dataSnapshot.getValue().equals("0")) {
                                    donateRef.child(request.getDonateid()).child("status").setValue("Donated");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Toast.makeText(context, "Donate Successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        myHolder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Request")
                        .child(request.getDonateid());

                //owner reject for the requester
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getKey().equals(request.getRequester())) {
                                reference.child(ds.getKey()).child("status").setValue("Rejected");
                                addNotification(ds.getKey(), ds.child("donateid").getValue().toString(), "rejected your request.");
                            }
                        }
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
    private void isAccept(final String donateId, final String requesterid, final Button btnAccept, final Button btnReject) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Request").child(donateId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(requesterid).child("status").getValue().equals("Accepted")) {
                    btnAccept.setText("Accepted");
                    btnAccept.setTextColor(Color.rgb(50, 205, 50));
                    btnAccept.setEnabled(false);
                    btnAccept.setVisibility(View.VISIBLE);
                    btnReject.setVisibility(View.GONE);
                } else if (dataSnapshot.child(requesterid).child("status").getValue().equals("Requesting")) {
                    btnAccept.setText("Accept");
                    btnReject.setText("Reject");
                    btnAccept.setTextColor(Color.rgb(0, 0, 0));
                    btnReject.setTextColor(Color.rgb(0, 0, 0));
                    btnAccept.setEnabled(true);
                    btnReject.setEnabled(true);
                    btnAccept.setVisibility(View.VISIBLE);
                    btnReject.setVisibility(View.VISIBLE);
                } else {
                    btnReject.setText("Rejected");
                    btnReject.setEnabled(false);
                    btnReject.setTextColor(Color.rgb(255, 0, 0));
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
    class MyHolder extends RecyclerView.ViewHolder {

        //views from row_post.xml
        ImageView requesterIv;
        TextView requesterNameTv, requestTimeTv, pReasonTv;
        Button acceptBtn, rejectBtn;
        LinearLayout requesterLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            requesterNameTv = itemView.findViewById(R.id.requesterNameTv);
            requestTimeTv = itemView.findViewById(R.id.requesterTimeTv);
            requesterIv = itemView.findViewById(R.id.requesterIv);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);
            requesterLayout = itemView.findViewById(R.id.requesterLayout);
            pReasonTv = itemView.findViewById(R.id.pReasonTv);
        }
    }

    private void addNotification(final String userid, final String postid, final String notiText) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("userid", firebaseUser.getUid());
                    hashMap.put("text", notiText);
                    hashMap.put("postid", postid);

                    reference.child(postid).setValue(hashMap);

                    String msg = notiText;
                    final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserProfile user = dataSnapshot.getValue(UserProfile.class);

                            if (notify) {
                                sendNotification(userid, user.getUserName(), notiText);
                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                //used this to avoid save 2 times same data into firebase
                else {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String existPost = ds.child("postid").getValue().toString();
                        if (!existPost.equals(postid)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("userid", firebaseUser.getUid());
                            hashMap.put("text", notiText);
                            hashMap.put("postid", postid);
                            hashMap.put("ispost", "havent");

                            reference.child(postid).setValue(hashMap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification(final String userid, final String userName, final String notiText) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(userid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), userName + " " + notiText,
                            "New Notification", userid, R.drawable.accept);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(context, "" + response.message(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
