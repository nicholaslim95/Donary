import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donary.R;
import com.example.donary.UserProfile;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

public class AdapterEventPost extends RecyclerView.Adapter<AdapterEventPost.ViewHolder> {

    public Context mContext;
    public List<EventPost> mPost;

    private FirebaseUser firebaseUser;

    public AdapterEventPost(Context mContext, List<EventPost> mPost) {
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userName, eventTitle, eventDescription, eventStartDate, eventEndDate;

        public TextView
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userEventPostPic);
            eventTitle = itemView.findViewById(R.id.eventPostTitle);
            eventDescription = itemView.findViewById(R.id.eventPostDescription);
            eventStartDate = itemView.findViewById(R.id.txtEventPostStartDate);
            eventEndDate = itemView.findViewById(R.id.txtEventPostEndDate);
        }
    }

    private void eventPostInfo(final TextView userName, TextView eventTitle, TextView eventDescription, TextView eventStartDate, TextView eventEndDate){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Event").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                //Profile image may be needed here

                userName.setText();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        })
    }
}
