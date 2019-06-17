package com.example.donary.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donary.ProfileActivity;
import com.example.donary.R;
import com.example.donary.UserProfile;
import com.example.donary.models.ModelPost;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    Context context;
    List<ModelPost> postList;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
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
        String donater = postList.get(i).getDonater();
        String pImage = postList.get(i).getPosImage();
        String ptitle = postList.get(i).getTitle();
        String pCondition = postList.get(i).getCondition();
        String donateid = postList.get(i).getDonateid();
        String pdescription = postList.get(i).getDescription();
        String pTime = postList.get(i).getTime();

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
        myHolder.pTitleTv.setText(ptitle +  " - (" + pCondition+  "% new)");
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
            try {
                Picasso.get().load(pImage).into(myHolder.pImageIv);
            } catch (Exception e) {

            }
        }

        myHolder.moreBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context,"More", Toast.LENGTH_LONG).show();
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
        }
    }
}
