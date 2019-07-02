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


public class tab4_fragment extends Fragment {
    private static final String TAG = "Tab4Fragment";

    Button btnCreateEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1_fragment, container, false);

        btnCreateEvent = (Button) view.findViewById(R.id.btnCreateEvent);

        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateEvent.class);
                startActivity(intent);
            }
        });
        return view;
    }


}
