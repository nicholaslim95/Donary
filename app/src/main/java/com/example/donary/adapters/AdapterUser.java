package com.example.donary.adapters;


import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.donary.R;
import com.example.donary.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder> {
    private Context mContext;
    private List<UserProfile> mUsers;

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FirebaseUser firebaseUser;

    StorageReference storageReference;
    public AdapterUser(Context mContext, List<UserProfile> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);

        return new AdapterUser.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //final UserProfile userProfile = mUsers.get(i);
        final String userName = mUsers.get(i).getUserName();
        final String userId = mUsers.get(i).getUserID();
        //viewHolder.userName.setText(userProfile.getUserName());
        viewHolder.userName.setText(userName);

        storageReference = firebaseStorage.getReference();
        storageReference.child("Users").child(userId).child("Profile pic").child(userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(viewHolder.imageProfile);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userName;
        public CircleImageView imageProfile;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            userName = itemView.findViewById(R.id.eventAttendeeUsername);
            imageProfile = itemView.findViewById(R.id.eventAttendeeProfilePic);

        }
    }

}
