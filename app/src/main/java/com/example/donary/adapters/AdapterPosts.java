package com.example.donary.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donary.R;
import com.example.donary.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    Context context;
    List<ModelPost> postList;

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
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        String donater = postList.get(i).getDonater();
        String pImage = postList.get(i).getPosImage();
        String ptitle = postList.get(i).getTitle();
        String uDp = postList.get(i).getDescription();
        String donateid = postList.get(i).getDonateid();

        //set user default pic
        try{
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_img).into(myHolder.uPicturerIv);
        }catch (Exception e){

        }

        myHolder.pTitleTv.setText(ptitle);

        if(pImage.equals("noImage")){
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

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView pImageIv, uPicturerIv ;
        TextView pTitleTv, pInterestTv;
        ImageButton moreBtm;
        Button interestBtn, commentBtn;

        public MyHolder(@NonNull View itemView){
            super(itemView);

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
