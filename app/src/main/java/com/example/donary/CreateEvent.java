package com.example.donary;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.LocaleList;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donary.models.ModelEventPost;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView txtCreateEventName, txtCreateEventDetails, txtNoOfParticipants, txtCreateEventStartDate, txtCreateEventEndDate;
    private Button btnCreateEvent;
    private String locationInformation = "";
    private FirebaseAuth firebaseAuth;
    private ImageView createEventImage;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private static int PICK_IMAGE = 123;
    Uri imagePath;
    Date startDate, endDate;
    private int startEndDateidentifier= 0; // To let system know which date (start or end) is selected
    PlacesClient placesClient; //relating to Google Places
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS);
    AutocompleteSupportFragment places_fragment;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                createEventImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        setupFindViewByID();

        //initialize Google places API
        initPlaces();
        setupPlaceAutoComplete();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventName = "", eventDetails = "", eventLocation= "", tempStrNoOfParticipants = "";
                int noOfParticipants = 0;

                //locationInformation = "";
                eventName = txtCreateEventName.getText().toString();
                eventDetails = txtCreateEventDetails.getText().toString();
                eventLocation = locationInformation;
                tempStrNoOfParticipants = txtNoOfParticipants.getText().toString();
                Date currentDate = new Date();

                boolean eventNameFilled = false, eventDetailsFilled = false, eventLocationFilled = false,
                        eventStartDateLegitimate = false, eventEndDateLegitimate  = false, eventNoOfParticipantsFilled = false;

                //checking event details
                if (eventName == null || eventName.isEmpty() || eventName.equals("")){
                    Toast.makeText(CreateEvent.this, "Please fill event name.", Toast.LENGTH_SHORT).show();
                }
                else{
                    eventNameFilled = true;
                }
                if (eventDetails == null || eventDetails.isEmpty() || eventDetails.equals("")){
                    Toast.makeText(CreateEvent.this, "Please fill event details.", Toast.LENGTH_SHORT).show();
                }else{
                    eventDetailsFilled = true;
                }
                if (eventLocation == null || eventLocation.isEmpty() || eventLocation.equals("")){
                    Toast.makeText(CreateEvent.this, "Where is this event located?", Toast.LENGTH_SHORT).show();
                }else{
                    eventLocationFilled = true;
                }
                //going around number of participants issue
                if(tempStrNoOfParticipants != null && tempStrNoOfParticipants.isEmpty() == false){
                    noOfParticipants = Integer.parseInt(tempStrNoOfParticipants);
                    if(noOfParticipants <= 0){
                        Toast.makeText(CreateEvent.this, "Make sure there's more than one person attending", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        eventNoOfParticipantsFilled = true;
                    }
                }
                else{
                    Toast.makeText(CreateEvent.this, "How many people attending this event?", Toast.LENGTH_SHORT).show();
                }
                //if event time earlier than current time
                if (startDate == null || endDate == null){
                    Toast.makeText(CreateEvent.this, "Please make sure all dates are selected.", Toast.LENGTH_SHORT).show();
                }
                else if(startDate.after(endDate)){
                    Toast.makeText(CreateEvent.this, "Starting date is later than ending date", Toast.LENGTH_SHORT).show();
                }
                else if(startDate.before(currentDate) || endDate.before(currentDate)) {
                    Toast.makeText(CreateEvent.this, "Please make sure the date is after the current date", Toast.LENGTH_SHORT).show();
                }
                else{
                    eventStartDateLegitimate = true;
                    eventEndDateLegitimate = true;
                }

                if( eventNameFilled == true && eventDetailsFilled == true && eventLocationFilled == true &&
                        eventStartDateLegitimate == true && eventEndDateLegitimate  == true && eventNoOfParticipantsFilled == true){
                    if(imagePath != null){
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = firebaseDatabase.getReference("Event");

                        String eventId = myRef.push().getKey();

                        //Storing event image into Firebase Storage
                        StorageReference imageReference = storageReference.child("Event").child(eventId).child("Event image");
                        UploadTask uploadTask = imageReference.putFile(imagePath);

                        //Storing into event attributes into Firebase Database
                        ModelEventPost newEvent = new ModelEventPost(eventId, eventName,eventDetails, eventLocation, noOfParticipants ,startDate, endDate, firebaseAuth.getUid());
                        newEvent.setEventStatus("Valid");
                        myRef.child(eventId).setValue(newEvent); //.push used to generate unique id when post

                        Toast.makeText(CreateEvent.this, "Event successfully created.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = firebaseDatabase.getReference("Event");

                        String eventId = myRef.push().getKey();

                        //Storing into event attributes into Firebase Database
                        ModelEventPost newEvent = new ModelEventPost(eventId, eventName,eventDetails, eventLocation, noOfParticipants ,startDate, endDate, firebaseAuth.getUid());
                        newEvent.setEventStatus("Valid");
                        myRef.child(eventId).setValue(newEvent); //.push used to generate unique id when post

                        Toast.makeText(CreateEvent.this, "Event successfully created.", Toast.LENGTH_SHORT).show();
                    }
                }
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

        createEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*"); //application/pdf OR application/*(for documentation), audio/* OR audio/mp3
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE);
            }
        });
    }

    public void setupFindViewByID(){
        txtCreateEventName = (TextView) findViewById(R.id.txtCreateEventName);
        txtCreateEventDetails = (TextView) findViewById(R.id.txtCreateEventDetails);
        txtNoOfParticipants = (TextView) findViewById(R.id.txtCreateNumberOfParticipant);
        txtCreateEventStartDate = (TextView) findViewById(R.id.txtCreateEventStartDate);
        txtCreateEventEndDate = (TextView) findViewById(R.id.txtCreateEventEndDate);
        btnCreateEvent = (Button) findViewById(R.id.btnCreateEvent);
        createEventImage = (ImageView) findViewById(R.id.imgCreateEventImage);
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

            startDate = new Date(year, month, dayOfMonth);
            System.out.println("year of startDate: " + startDate.getYear());
            System.out.println("month of startDate: " + startDate.getMonth());
            System.out.println("day of startDate: " + startDate.getDate());
            String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
            txtCreateEventStartDate.setText(currentDateString);
        }else if(startEndDateidentifier == 2){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            endDate = new Date(year, month, dayOfMonth);

            System.out.println("year of endDate: " + endDate.getYear());
            System.out.println("month of endDate: " + endDate.getMonth());
            System.out.println("day of endDate: " + endDate.getDate());
            String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
            txtCreateEventEndDate.setText(currentDateString);
        }else{
            Toast.makeText(CreateEvent.this, "Please make sure that all dates are selected", Toast.LENGTH_SHORT).show();
        }

    }
}
