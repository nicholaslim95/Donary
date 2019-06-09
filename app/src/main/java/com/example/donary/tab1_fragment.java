package com.example.donary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class tab1_fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";

    Button testBtnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1_fragment, container, false);
        testBtnLogout = (Button) view.findViewById(R.id.testBtnLogout);

        testBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Logout
               FirebaseAuth.getInstance().signOut();
               //Just a test, or else look back on login manager
                LoginManager.getInstance().logOut();
               openMainActivity();
            }
        });
        return view;
    }

    private void openMainActivity() {
        
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }


}
