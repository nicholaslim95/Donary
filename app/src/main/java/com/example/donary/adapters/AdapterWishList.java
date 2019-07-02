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
import android.widget.TextView;
import android.widget.Toast;

import com.example.donary.AddWishlistActivity;
import com.example.donary.CommentsActivity;
import com.example.donary.ProfileActivity;
import com.example.donary.R;
import com.example.donary.UserProfile;
import com.example.donary.models.ModelWishlist;
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

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterWishList extends RecyclerView.Adapter<AdapterWishList.MyHolder>{

    Context context;
    List<ModelWishlist> wishlist;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    String myUid;

    public AdapterWishList(Context context, List<ModelWishlist> wishlist) {
        this.context = context;
        this.wishlist = wishlist;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_wishlists, viewGroup, false);
        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {

        //get data
        final String requester = wishlist.get(i).getRequester();
        final String pImage = wishlist.get(i).getPosImage();
        String ptitle = wishlist.get(i).getTitle();
        final String wishlistid = wishlist.get(i).getWishlistid();
        String pdescription = wishlist.get(i).getDescription();
        String pTime = wishlist.get(i).getTime();
        String pStatus = wishlist.get(i).getStatus();

        final ModelWishlist wishlist = this.wishlist.get(i);

        //convert time to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTime));
        String sTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set user default pic
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child("Users").child(requester).child("Profile pic").child(requester).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).placeholder(R.drawable.ic_default_img).into(myHolder.uPicturerIv);
            }
        });

        //display the user name
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(requester);
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
        myHolder.pTitleTv.setText(ptitle +  " - "+pStatus);
        myHolder.pDescriptionTv.setText(pdescription);
        myHolder.pTimeTv.setText(sTime);

        try {
            Picasso.get().load(pImage).into(myHolder.pImageIv);
        } catch (Exception e) {

        }

        myHolder.moreBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(myHolder.moreBtm, requester, myUid, wishlistid, pImage);
            }
        });


        //count the number of requests for request post
        getComments(wishlist.getWishlistid(), myHolder.pCommentTv);
        myHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentsActivity.class);
                //since the cooment Activity inside use donateid so cannot change to wishlistid
                intent.putExtra("donateid", wishlist.getWishlistid());
                intent.putExtra("commenter", wishlist.getRequester());
                context.startActivity(intent);
            }
        });

        myHolder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*click to go to Profile Activity with uid, this uid is of clicked user
                * which will be used to show user specific data/posts*/
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("requester", requester);
                context.startActivity(intent);

            }
        });

    }

    private void getComments(String wishlistid, final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(wishlistid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText(dataSnapshot.getChildrenCount() + "Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMoreOptions(ImageButton moreBtm, String requester, String myUid, final String wishlistId, final String pImage) {
        //creating popup meny currently having option Delete,
        PopupMenu popupMenu = new PopupMenu(context, moreBtm, Gravity.END);

        //show delete option in only post(s) of currently signed-in user
        if(requester.equals(myUid)){
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
                    beginDelete(wishlistId, pImage);
                }
                else if(id==1){
                    //Edit is clicked
                    //start AddWishlistActivity with key "editPost" and the id of the post clicked
                    Intent intent = new Intent(context, AddWishlistActivity.class);
                    intent.putExtra("key","editWishlist");
                    intent.putExtra("editWishlistId", wishlistId);
                    context.startActivity(intent);
                }
                return false;
            }
        });

        //show menu;
        popupMenu.show();
    }

    private void beginDelete(String wishlisid, String pImage) {
        //delete the post and image
        deleteWithImage(wishlisid, pImage);
    }

    private void deleteWithImage(final String wishlistid, String pImage) {
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
                        Query fquery = FirebaseDatabase.getInstance().getReference("Wishlist").orderByChild("wishlistid").equalTo(wishlistid);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        ds.getRef().removeValue(); //remove values from firebase where wishlistid matches
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
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return wishlist.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views from row_post.xml
        ImageView pImageIv, uPicturerIv ;
        TextView pTitleTv, pCommentTv, uNameTv, pDescriptionTv, pTimeTv;
        ImageButton moreBtm;
        Button commentBtn;
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
            pCommentTv = itemView.findViewById(R.id.pCommentTv);
            moreBtm = itemView.findViewById(R.id.moreBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);
        }
    }
}
