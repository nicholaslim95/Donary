package com.example.donary.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donary.Event;
import com.example.donary.EventPost;
import com.example.donary.R;
import com.example.donary.UserProfile;
import com.example.donary.models.ModelEventPost;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterEventPost extends RecyclerView.Adapter<AdapterEventPost.ViewHolder> {

    public Context mContext;
    public List<ModelEventPost> mPost;

    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    public AdapterEventPost(Context mContext, List<ModelEventPost> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_event_post, viewGroup, false);
        return new AdapterEventPost.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        //This is where we ge data and assign to viewholder
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ModelEventPost modelEventPost = mPost.get(i);

        final String eventName = mPost.get(i).getEventName();
        final String eventDescription = mPost.get(i).getEventDetail();

        //SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");

        final Date eventStartDate = mPost.get(i).getEventStartDate();
        eventStartDate.setYear(eventStartDate.getYear() - 1900); //After SDK 16, need to minus years after 1900
        final Date eventEndDate = mPost.get(i).getEventEndDate();
        eventEndDate.setYear(eventEndDate.getYear() - 1900);
        //final String startDate = dateFormat.format(eventStartDate);
        //final String endDate = dateFormat.format(eventEndDate);
        final String startDate =  DateFormat.getDateInstance(DateFormat.FULL).format(eventStartDate);
        final String endDate =  DateFormat.getDateInstance(DateFormat.FULL).format(eventEndDate);
        final String userID = mPost.get(i).getEventPoster();
/*
        if(modelEventPost.getEventName().equals("")){
            //use this to hide if there is no event name (but that's impossible, unless like a no image then hide)
            viewHolder.eventName.setVisibility(View.GONE);
        }else{
            //else show the view (applicable for image if it exist)
            viewHolder.eventName.setVisibility(View.VISIBLE);
        }



*/

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Event");
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        //To apply user's profile picture to event post
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child("Users").child(userID).child("Profile pic").child(userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).placeholder(R.drawable.ic_default_img).into(viewHolder.eventPosterProfilePic);
            }
        });

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                viewHolder.userName.setText(userProfile.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //To apply event details to event post
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelEventPost modelEventPost = dataSnapshot.getValue(ModelEventPost.class);

                //Profile image may be needed here

                viewHolder.eventName.setText(eventName);
                viewHolder.eventDescription.setText(eventDescription);
                viewHolder.eventStartDate.setText("The event will start at: "+ startDate);
                viewHolder.eventEndDate.setText("The event will end at: "+ endDate);

                System.out.println("Event name of modelEventPost: " + modelEventPost.getEventName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //viewHolder.eventName.setText(modelEventPost.getEventName());

        //eventPostInfo(viewHolder.eventName, modelEventPost.getEventId());

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    //Where elements within view holder is declared
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName, eventName, eventDescription, eventStartDate, eventEndDate;
        ImageView eventPosterProfilePic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.txtEventPostUserName);
            eventName = itemView.findViewById(R.id.txtEventPostTitle);
            eventDescription = itemView.findViewById(R.id.txtEventPostDescription);
            eventStartDate = itemView.findViewById(R.id.txtEventPostStartDate);
            eventEndDate = itemView.findViewById(R.id.txtEventPostEndDate);
            eventPosterProfilePic = itemView.findViewById(R.id.eventPosterProfilePic);
        }
    }

    private void eventPostInfo(final TextView eventName, String eventId){

    }
}
