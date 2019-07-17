package com.example.donary.adapters;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.donary.R;
import com.example.donary.UserProfile;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder> {
    private Context mContext;
    private List<UserProfile> mUsers;

    private FirebaseUser firebaseUser;

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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //final UserProfile userProfile = mUsers.get(i);
        final String userName = mUsers.get(i).getUserName();
        //viewHolder.userName.setText(userProfile.getUserName());
        viewHolder.userName.setText(userName);

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
