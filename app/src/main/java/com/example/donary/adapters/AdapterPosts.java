package com.example.donary.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donary.AddDonationActivity;
import com.example.donary.ChatActivity;
import com.example.donary.CommentsActivity;
import com.example.donary.FullScreenImageActivity;
import com.example.donary.ProfileActivity;
import com.example.donary.R;
import com.example.donary.UserProfile;
import com.example.donary.ViewRequestsActivity;
import com.example.donary.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;
    List<ModelPost> postList;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    String myUid;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_donates, viewGroup, false);
        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {

        //get data
        final String donater = postList.get(i).getDonater();
        final String pImage = postList.get(i).getPosImage();
        String ptitle = postList.get(i).getTitle();
        String pCondition = postList.get(i).getCondition();
        final String donateid = postList.get(i).getDonateid();
        String pdescription = postList.get(i).getDescription();
        String pTime = postList.get(i).getTime();
        String pStatus = postList.get(i).getStatus();

        final ModelPost post = postList.get(i);

        //convert time to dd/mm/yyyy hh:mm am/pm
        Long currentTime = Long.parseLong(pTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" hh:mm aa dd/MM/yyyy");
        Date date = new Date(currentTime);
        String sTime = simpleDateFormat.format(date);

        //set user default pic
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child("Users").child(donater).child("Profile pic").child(donater).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).placeholder(R.drawable.ic_default_img).into(myHolder.uPicturerIv);
            }
        });

        //display the user name
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(donater);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                myHolder.uNameTv.setText(userProfile.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //set the post data
        myHolder.pTitleTv.setText(ptitle + " - (" + pCondition + "% new) - " + pStatus);
        myHolder.pDescriptionTv.setText(pdescription);
        myHolder.pTimeTv.setText(sTime);

        try {
            Picasso.get().load(pImage).into(myHolder.pImageIv);
        } catch (Exception e) {

        }

        myHolder.moreBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(myHolder.moreBtm, donater, myUid, donateid, pImage);
            }
        });

        myHolder.pImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullScreenIntent = new Intent(context, FullScreenImageActivity.class);
                fullScreenIntent.setData(Uri.parse(pImage));
                context.startActivity(fullScreenIntent);
            }
        });

        myHolder.requestBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Dialog requestDialog = new Dialog(context);
                requestDialog.setContentView(R.layout.request_reson_dialog);
                final EditText reason = (EditText) requestDialog.findViewById(R.id.requestreason);
                Button saveReason = (Button) requestDialog.findViewById(R.id.save);

                //Owner of the donation post will got to the user request page for that donation item.
                if (post.getDonater().equals(firebaseUser.getUid())) {

                    //check whether got requester for that donation
                    if (myHolder.requestBtn.getText().equals("No Requests")) {
                        Toast.makeText(context, "Don't have Requester", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(context, ViewRequestsActivity.class);
                        intent.putExtra("donateid", post.getDonateid());
                        context.startActivity(intent);
                    }

                } else {
                    if (myHolder.requestBtn.getText().equals("Request")) {

                        reason.setEnabled(true);
                        saveReason.setEnabled(true);
                        requestDialog.show();
                        saveReason.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Request")
                                        .child(post.getDonateid()).child(firebaseUser.getUid());

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("donateid", post.getDonateid());
                                hashMap.put("requester", firebaseUser.getUid());
                                hashMap.put("time", String.valueOf(System.currentTimeMillis()));
                                hashMap.put("reason", "Reason: " + reason.getText().toString());
                                hashMap.put("status", "Requesting");

                                reference.setValue(hashMap);
                                requestDialog.cancel();
                                Toast.makeText(context, "Requested", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Request")
                                .child(post.getDonateid()).child(firebaseUser.getUid()).removeValue();
                        Toast.makeText(context, "Request Cancelled", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

        myHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("donateid", post.getDonateid());
                intent.putExtra("commenter", post.getDonater());
                context.startActivity(intent);
            }
        });

        myHolder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*click to go to Profile Activity with uid, this uid is of clicked user
                 * which will be used to show user specific data/posts*/
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("donater", donater);
                context.startActivity(intent);

            }
        });

        //count the number of requests for donation post
        getComments(post.getDonateid(), myHolder.pCommentTv);
        isRequest(post.getDonateid(), myHolder.requestBtn);
        nrRequests(myHolder.pInterestTv, post.getDonateid());
    }

    private void getComments(String donateid, final TextView comments) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(donateid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 1) {
                    comments.setText("0 comment");
                } else {
                    comments.setText(dataSnapshot.getChildrenCount() + " comments");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMoreOptions(ImageButton moreBtm, final String donater, String myUid, final String donateid, final String pImage) {
        //creating popup meny currently having option Delete,
        PopupMenu popupMenu = new PopupMenu(context, moreBtm, Gravity.END);

        //show delete option in only post(s) of currently signed-in user
        if (donater.equals(myUid)) {
            //add item into menu
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
        } else {
            popupMenu.getMenu().add(Menu.NONE, 2, 0, "Message");
        }

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == 0) {
                    //delete is clicked
                    beginDelete(donateid, pImage);
                } else if (id == 1) {
                    //Edit is clicked
                    //start AddDonationActivity with key "editPost" and the id of the post clicked
                    Intent intent = new Intent(context, AddDonationActivity.class);
                    intent.putExtra("key", "editDonation");
                    intent.putExtra("editDonationId", donateid);
                    context.startActivity(intent);

                } else if (id == 2) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("hisUid", donater);
                    context.startActivity(intent);
                }
                return false;
            }
        });

        //show menu;
        popupMenu.show();
    }

    private void beginDelete(String donateid, String pImage) {
        //delete the post and image
        deleteWithImage(donateid, pImage);
    }

    private void deleteWithImage(final String donateid, String pImage) {
        //progress bar
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        //Delete Image using url
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Imaged deleted, and delete the databse now
                        Query fquery = FirebaseDatabase.getInstance().getReference("donates").orderByChild("donateid").equalTo(donateid);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        ds.getRef().removeValue(); //remove values from firebase where donateid matches
                                    }
                                }
                                //deleted
                                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    //PUT reload activity
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failure for delete and show error
                        pd.dismiss();
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //check personal whether got requests for particular post
    private void isRequest(String donateId, final Button button) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Request")
                .child(donateId);

        final DatabaseReference donaterRef = FirebaseDatabase.getInstance().getReference()
                .child("donates").child(donateId).child("donater");

        final DatabaseReference donatedRef = FirebaseDatabase.getInstance().getReference()
                .child("donates").child(donateId).child("status");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot requestSnap) {

                //changes the post request button to "View Requests" button for owner
                donaterRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot donaterSnap) {

                        donatedRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot donatedSnap) {

                                if(donatedSnap.exists() && donaterSnap.exists()){
                                    //check whether the donate item still available
                                    if (donatedSnap.getValue().equals("Available")) {
                                        if (donaterSnap.getValue().equals(firebaseUser.getUid())) {
                                            if (requestSnap.getChildrenCount() >= 1) {
                                                button.setText("View Requests");
                                            } else {
                                                button.setText("No Requests");
                                            }
                                        } else {
                                            //if not owner for that post
                                            if (requestSnap.child(firebaseUser.getUid()).exists()) {
                                                button.setText("Requested");
                                                button.setBackgroundColor(Color.rgb(255, 222, 173));
                                                button.setEnabled(true);
                                            } else {
                                                button.setText("Request");
                                                button.setBackgroundColor(Color.rgb(255, 255, 255));
                                            }
                                        }
                                    } else {
                                        //show to requester the status is accepted
                                        String tmp1 = firebaseUser.getUid();
                                        String tmp2 = requestSnap.child(tmp1).child("status").getValue().toString();
                                        if(tmp2.equals("Accepted")){
                                            button.setText("Accepted");
                                            button.setTextColor(Color.rgb(255, 255, 255));
                                            button.setBackgroundColor(Color.rgb(0, 255, 0));
                                            button.setEnabled(false);
                                        }else {
                                            button.setText("Donated");
                                            button.setTextColor(Color.rgb(255, 255, 255));
                                            button.setBackgroundColor(Color.rgb(255, 0, 0));
                                            button.setEnabled(false);
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(context, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    //COUNT NUMBER OF REQUEST FOR A DONATION POST
    private void nrRequests(final TextView requests, String donateId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Request")
                .child(donateId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 1) {
                    requests.setText("0 request");
                } else {
                    requests.setText(dataSnapshot.getChildrenCount() + " requests");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        //views from row_post.xml
        ImageView pImageIv, uPicturerIv;
        TextView pTitleTv, pInterestTv, pCommentTv, uNameTv, pDescriptionTv, pTimeTv;
        ImageButton moreBtm, commentBtn;
        Button requestBtn;
        LinearLayout profileLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            uPicturerIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pInterestTv = itemView.findViewById(R.id.pInterestTv);
            pCommentTv = itemView.findViewById(R.id.pCommentTv);
            moreBtm = itemView.findViewById(R.id.moreBtn);
            requestBtn = itemView.findViewById(R.id.requestBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);
        }
    }

}
