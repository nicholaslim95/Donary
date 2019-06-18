package com.example.donary.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donary.AddDonationActivity;
import com.example.donary.Homepage;
import com.example.donary.ProfileActivity;
import com.example.donary.R;
import com.example.donary.Tab3_fragment;
import com.example.donary.UserProfile;
import com.example.donary.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    Context context;
    List<ModelPost> postList;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase;

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

        //convert time to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTime));
        String sTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set user default pic
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child("Users").child(donater).child("Profile pic").child(donater).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).placeholder(R.drawable.ic_default_img).into(myHolder.uPicturerIv);
            }
        });

        //set the post data
        myHolder.pTitleTv.setText(ptitle +  " - (" + pCondition+  "% new) "+pStatus);
        myHolder.pDescriptionTv.setText(pdescription);
        myHolder.pTimeTv.setText(sTime);

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


        //action when no image in post
        if(pImage.equals("")){
            myHolder.pImageIv.setVisibility(View.GONE);
        }
        else {
            //show imageview
            myHolder.pImageIv.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(pImage).into(myHolder.pImageIv);
            } catch (Exception e) {

            }
        }

        myHolder.moreBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(myHolder.moreBtm, donater, myUid, donateid, pImage);

            }
        });
        myHolder.interestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context,"Interest", Toast.LENGTH_LONG).show();
            }
        });
        myHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context,"Comment", Toast.LENGTH_LONG).show();
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

    }

    private void showMoreOptions(ImageButton moreBtm, String donater, String myUid, final String donateid, final String pImage) {
        //creating popup meny currently having option Delete,
        PopupMenu popupMenu = new PopupMenu(context, moreBtm, Gravity.END);

        //show delete option in only post(s) of currently signed-in user
        if(donater.equals(myUid)){
            //add item into menu
            popupMenu.getMenu().add(Menu.NONE,0,0, "Delete");
            popupMenu.getMenu().add(Menu.NONE,1,0, "Edit");
        }

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id==0){
                    //delete is clicked
                    beginDelete(donateid, pImage);
                }
                else if(id==1){
                    //Edit is clicked
                    //start AddDonationActivity with key "editPost" and the id of the post clicked
                    Intent intent = new Intent(context, AddDonationActivity.class);
                    intent.putExtra("key","editDonation");
                    intent.putExtra("editDonationId", donateid);
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
        StorageReference picRef = firebaseStorage.getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Imaged deleted, and delete the databse now

                        Query fquery = FirebaseDatabase.getInstance().getReference("donates").orderByChild("donateid").equalTo(donateid);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue(); //remove values from firebase where donateid matches
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failure for delete and show error
                        pd.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views from row_post.xml
        ImageView pImageIv, uPicturerIv ;
        TextView pTitleTv, pInterestTv, uNameTv, pDescriptionTv, pTimeTv;
        ImageButton moreBtm;
        Button interestBtn, commentBtn;
        LinearLayout profileLayout;

        public MyHolder(@NonNull View itemView){
            super(itemView);

            //init views
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            uPicturerIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pInterestTv = itemView.findViewById(R.id.pInterestTv);
            moreBtm = itemView.findViewById(R.id.moreBtn);
            interestBtn = itemView.findViewById(R.id.interestBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);
        }
    }
}
