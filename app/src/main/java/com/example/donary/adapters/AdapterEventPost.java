package com.example.donary.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donary.ChatActivity;
import com.example.donary.EventAttendees;
import com.example.donary.EventPostCommentActivity;
import com.example.donary.EventPostFullScreenImage;
import com.example.donary.R;
import com.example.donary.UserProfile;
import com.example.donary.models.ModelEventPost;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.Date;
import java.util.List;

public class AdapterEventPost extends RecyclerView.Adapter<AdapterEventPost.ViewHolder> {

    public Context mContext;
    public List<ModelEventPost> mPost;
    String myUID;

    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FirebaseDatabase firebaseDatabase;
    public AdapterEventPost(Context mContext, List<ModelEventPost> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
        this.myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_event_post, viewGroup, false);
        return new AdapterEventPost.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        firebaseDatabase = FirebaseDatabase.getInstance();

        //This is where we ge data and assign to viewholder
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //TO CHECK IF EVENT EXPIRED OR NOT
        Date currentDate = new Date();
        currentDate.setYear(currentDate.getYear() + 1900); //Needed after sdk 16
        if(mPost.get(i).getEventEndDate().compareTo(currentDate) < 0 ){
            mPost.get(i).setEventStatus("Expired");
        }else{
            mPost.get(i).setEventStatus("Valid");
        }

        //WILL HIDE POST IF EVENT IS EITHER "DELETED" OR "EXPIRED"
        if(mPost.get(i).getEventStatus().equals("Invalid") || mPost.get(i).getEventStatus().equals("Expired")){
            //Dont bind event as they have been "deleted". (Still in database)
            viewHolder.itemView.setVisibility(View.GONE);
            viewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }else{
            final ModelEventPost modelEventPost = mPost.get(i);

            final String eventId =  mPost.get(i).getEventId();
            final String eventName = mPost.get(i).getEventName();
            final String eventDescription = mPost.get(i).getEventDetail();
            final String eventLocation = mPost.get(i).getEventLocation();
            final Date eventStartDate = mPost.get(i).getEventStartDate();
            eventStartDate.setYear(eventStartDate.getYear() - 1900); //After SDK 16, need to minus years after 1900
            final Date eventEndDate = mPost.get(i).getEventEndDate();
            eventEndDate.setYear(eventEndDate.getYear() - 1900); //After SDK 16, need to minus years after 1900
            //final String startDate = dateFormat.format(eventStartDate);
            //final String endDate = dateFormat.format(eventEndDate);
            final String startDate =  DateFormat.getDateInstance(DateFormat.FULL).format(eventStartDate);
            final String endDate =  DateFormat.getDateInstance(DateFormat.FULL).format(eventEndDate);
            final String userID = mPost.get(i).getEventPoster();

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

            //To apply event image into event post
            //WILL HIDE LAYOUT IF EVENT IMAGE DOES NOT EXIST
            StorageReference eventImageStorageReference = firebaseStorage.getReference();

            viewHolder.eventPostImage.setVisibility(View.VISIBLE);
            eventImageStorageReference.child("Event").child(eventId).child("Event image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerCrop().into(viewHolder.eventPostImage);
                }
            }).addOnFailureListener(new OnFailureListener() { //IF there is no image for event post, imageView is hidden (object not found errors are expected)
                @Override
                public void onFailure(@NonNull Exception e) {
                    viewHolder.eventPostImage.setClickable(false);
                    viewHolder.eventPostImage.setVisibility(View.GONE);
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
                    viewHolder.eventName.setText(eventName);
                    viewHolder.eventDescription.setText(eventDescription);
                    viewHolder.eventStartDate.setText("Event starts at: " + startDate);
                    viewHolder.eventEndDate.setText("Event ends at: " + endDate);
                    viewHolder.eventLocation.setText("Event located at:\n" + eventLocation);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //ADDING IN THE MORE OPTION ON EVENT POST
            viewHolder.btnEventPostMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreOptions(viewHolder.btnEventPostMore, userID, myUID, eventId);
                }
            });

            //TO CHECK WHETHER IF USER JOINED THIS PARTICULAR EVENT OR NOT
            isJoined(modelEventPost.getEventId(), viewHolder.btnJoinEvent);

            //SHOW NUMBER OF ATTENDEES FOR PARTICULAR EVENT POST
            showNumberOfAttendees(viewHolder.numberOfParticipants, modelEventPost.getEventId());

            //SHOW NUMBER OF COMMENTS FOR PARTICULAR EVENT POST
            showNumberOfComments(viewHolder.numberOfComments, modelEventPost.getEventId());

            viewHolder.btnJoinEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewHolder.btnJoinEvent.getTag().equals("Join Event")){
                        FirebaseDatabase.getInstance().getReference().child("EventAttendees").child(modelEventPost.getEventId())
                                .child(firebaseUser.getUid()).setValue(true);
                    }else{
                        FirebaseDatabase.getInstance().getReference().child("EventAttendees").child(modelEventPost.getEventId())
                                .child(firebaseUser.getUid()).removeValue();
                    }
                }
            });

            //GET TO SEE WHO ATTEND THE EVENT
            viewHolder.numberOfParticipants.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EventAttendees.class);
                    intent.putExtra("id", modelEventPost.getEventId());
                    intent.putExtra("title", "Attending");
                    mContext.startActivity(intent);
                }
            });

            viewHolder.btnEventComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EventPostCommentActivity.class);
                    intent.putExtra("eventid", modelEventPost.getEventId());
                    intent.putExtra("commenter", modelEventPost.getEventPoster());
                    mContext.startActivity(intent);
                }
            });

            //ABLE TO VIEW ENLARGED EVENT IMAGE
            viewHolder.eventPostImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EventPostFullScreenImage.class);
                    intent.putExtra("eventid", modelEventPost.getEventId());
                    mContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    //Where elements within view holder is declared
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageButton btnEventPostMore;
        Button btnJoinEvent, btnEventComment;
        TextView userName, eventName, eventDescription,numberOfParticipants,numberOfComments, eventLocation, eventStartDate, eventEndDate;
        ImageView eventPosterProfilePic, eventPostImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.txtEventPostUserName);
            eventName = itemView.findViewById(R.id.txtEventPostTitle);
            eventDescription = itemView.findViewById(R.id.txtEventPostDescription);
            eventStartDate = itemView.findViewById(R.id.txtEventPostStartDate);
            eventEndDate = itemView.findViewById(R.id.txtEventPostEndDate);
            eventPosterProfilePic = itemView.findViewById(R.id.eventPosterProfilePic);
            eventPostImage = itemView.findViewById(R.id.imgEventPostImage);
            btnEventPostMore = itemView.findViewById(R.id.btnEventPostMore);
            eventLocation = itemView.findViewById(R.id.txtEventPostLocation);
            btnJoinEvent = itemView.findViewById(R.id.btnJoinEvent);
            numberOfParticipants = itemView.findViewById(R.id.txtNoOfPeopleGoing);
            numberOfComments = itemView.findViewById(R.id.txtEventPostComment);
            btnEventComment = itemView.findViewById(R.id.btnEventPostComment);
        }
    }

    //To see if user had joined event

    //WILL CHANGE LABEL ON ATTEND BUTTON DEPENDING IF USER ATTEND EVENT OR NOT
    private void isJoined(final String eventId, final Button btnJoinEvent){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("EventAttendees").child(eventId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    btnJoinEvent.setText("Attending");
                    btnJoinEvent.setTag("Attending");
                }else{
                    btnJoinEvent.setText("Join Event");
                    btnJoinEvent.setTag("Join Event");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //To display the number of attendees on event post
    private void showNumberOfAttendees (final TextView numberOfAttendees, String eventId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("EventAttendees").child(eventId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    numberOfAttendees.setText(dataSnapshot.getChildrenCount() + " people attending.");
                    numberOfAttendees.setClickable(true);
                }else{
                    numberOfAttendees.setText(dataSnapshot.getChildrenCount() + " people attending.");
                    numberOfAttendees.setClickable(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //To display the number of attendees on event post
    private void showNumberOfComments (final TextView numberOfComments, String eventId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("EventPostComments").child(eventId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberOfComments.setText(dataSnapshot.getChildrenCount() + " comment.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMoreOptions(ImageButton btnEventPostMore, final String userID, String myUid, final String eventId) {
        //creating popup meny currently having option Delete,
        PopupMenu popupMenu = new PopupMenu(mContext, btnEventPostMore, Gravity.END);

        //show delete option in only post(s) of currently signed-in user
        if (userID.equals(myUid)) {
            //add item into menu
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Delete");
        } else {
            popupMenu.getMenu().add(Menu.NONE, 2, 0, "Message");
        }

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == 1) {
                    //delete is clicked
                    beginDelete(eventId);
                } else if (id == 2) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("hisUid", userID);
                    mContext.startActivity(intent);
                }
                return false;
            }
        });

        //show menu;
        popupMenu.show();
    }

    private void beginDelete(String eventId) {
        //delete the post and image
        deleteWithImage(eventId);
    }

    private void deleteWithImage(final String eventId) {
        //progress bar
        final ProgressDialog pd = new ProgressDialog(mContext);
        pd.setMessage("Deleting...");

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Event").child(eventId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelEventPost modelEventPost = dataSnapshot.getValue(ModelEventPost.class);

                modelEventPost.setEventStatus("Invalid");
                reference.setValue(modelEventPost);
                Toast.makeText(mContext, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*//Delete Image using url
        StorageReference picRef = FirebaseStorage.getInstance().getReference().child("Event").child(eventId).child("Event image");
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Imaged deleted, and delete the databse now
                        Query fquery = FirebaseDatabase.getInstance().getReference("Event").orderByChild("eventId").equalTo(eventId);
                        String strEventID = eventId;
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        ds.getRef().removeValue(); //remove values from firebase where donateid matches
                                    }
                                }
                                //deleted
                                Toast.makeText(mContext, "Deleted Successfully", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

}
