package com.example.donary;

import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;

public class CreateEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView txtCreateEventStartDate, txtCreateEventEndDate;
    private int startEndDateidentifier= 0; // To let system know which date (start or end) is selected
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        txtCreateEventStartDate = (TextView) findViewById(R.id.txtCreateEventStartDate);
        txtCreateEventEndDate = (TextView) findViewById(R.id.txtCreateEventEndDate);

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
