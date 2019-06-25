package com.example.donary;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CreateEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView txtCreateEventStartDate, txtCreateEventEndDate;
    private Button btnCreateEvent;
    private String locationInformation;
    private int startEndDateidentifier= 0; // To let system know which date (start or end) is selected
    PlacesClient placesClient; //relating to Google Places
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS);
    AutocompleteSupportFragment places_fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        txtCreateEventStartDate = (TextView) findViewById(R.id.txtCreateEventStartDate);
        txtCreateEventEndDate = (TextView) findViewById(R.id.txtCreateEventEndDate);
        btnCreateEvent = (Button) findViewById(R.id.btnCreateEvent);

        //initialize Google places API
        initPlaces();
        setupPlaceAutoComplete();


        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateEvent.this, "About to upload address: " + locationInformation, Toast.LENGTH_SHORT).show();
            }
        });
        txtCreateEventStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startEndDateidentifier = 1;
                DialogFragment startDatePicker = new DatePickerFragment();
                startDatePicker.show(getSupportFragmentManager(), "start date picker");
            }
        });

        txtCreateEventEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEndDateidentifier = 2;
                DialogFragment endDatePicker = new DatePickerFragment();
                endDatePicker.show(getSupportFragmentManager(), "end date picker");
            }
        });
    }
    private void initPlaces(){
        Places.initialize(this, getString(R.string.places_api_key));
        placesClient = Places.createClient(this);
    }

    private void setupPlaceAutoComplete(){
        places_fragment = (AutocompleteSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.places_autocomplete_fragment);
        places_fragment.setPlaceFields(placeFields);
        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                locationInformation = ""+place.getAddress();
                Toast.makeText(CreateEvent.this, ""+place.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(CreateEvent.this, ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if(startEndDateidentifier == 1){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
            txtCreateEventStartDate.setText(currentDateString);
        }else if(startEndDateidentifier == 2){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
            txtCreateEventEndDate.setText(currentDateString);
        }else{
            Toast.makeText(CreateEvent.this, "Please make sure that all dates are selected", Toast.LENGTH_SHORT).show();
        }

    }
}
