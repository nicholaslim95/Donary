package com.example.donary.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.donary.AddDonationActivity;
import com.example.donary.AddWishlistActivity;
import com.example.donary.R;


public class Message_fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2_fragment, container, false);

        Button btnAdd=(Button) view.findViewById(R.id.btn_add);
        Button btnWishlist=(Button) view.findViewById(R.id.btn_wishlist);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddDonationActivity.class);
                startActivity(intent);
            }
        });

        btnWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddWishlistActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }
}
