package com.example.donary.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.donary.ChatActivity;
import com.example.donary.R;
import com.example.donary.UserProfile;
import com.example.donary.models.ModelChat;
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

import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.MyHolder> {

    Context context;
    List<ModelChat> message;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    String myUid;

    public AdapterMessage(Context context, List<ModelChat> message) {
        this.context = context;
        this.message = message;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_message, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {

        //get data
        final String sender = message.get(i).getSender();
        final String receiver = message.get(i).getReceiver();
        String pmessage = message.get(i).getMessage();

        //set user default pic
        StorageReference storageReference = firebaseStorage.getReference();
        if (receiver.equals(myUid)) {
            storageReference.child("Users").child(sender).child("Profile pic").child(sender).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).placeholder(R.drawable.ic_default_img).into(myHolder.uPicturerIv);
                }
            });
        } else if (sender.equals(myUid)) {
            storageReference.child("Users").child(receiver).child("Profile pic").child(receiver).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).placeholder(R.drawable.ic_default_img).into(myHolder.uPicturerIv);
                }
            });
        }

        //display the user name
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        if (receiver.equals(myUid)) {
            databaseReference = firebaseDatabase.getReference("Users").child(sender);
        } else if (sender.equals(myUid)) {
            databaseReference = firebaseDatabase.getReference("Users").child(receiver);
        }
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

        //setdata
        myHolder.pMessageTv.setText(pmessage);

        myHolder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*click to go to Profile Activity with uid, this uid is of clicked user
                 * which will be used to show user specific data/posts*/
                Intent intent = new Intent(context, ChatActivity.class);
                if (receiver.equals(myUid)) {
                    intent.putExtra("hisUid", sender);
                } else {
                    intent.putExtra("hisUid", receiver);
                }
                context.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return message.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        ImageView uPicturerIv;
        TextView uNameTv, pMessageTv;
        LinearLayout profileLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pMessageTv = itemView.findViewById(R.id.mMessageTv);
            uPicturerIv = itemView.findViewById(R.id.uPictureIv);
            profileLayout = itemView.findViewById(R.id.profileLayout);
        }
    }
}
