package com.utd.ece.medibluenet;

import android.app.TimePickerDialog;

import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.app.DialogFragment;
import android.app.Dialog;
import java.util.Calendar;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    private String viewTag;
    Bundle bundle;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        bundle = this.getArguments();
        viewTag = bundle.getString("timeViewTag");

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        TextView startView = (TextView) getActivity().findViewById(R.id.startTime);
        TextView endView = (TextView) getActivity().findViewById(R.id.endTime);
        if(viewTag.equals("setStartTime"))
            startView.setText(hourOfDay+":"+minute+":00");
        else
            endView.setText(hourOfDay+":"+minute+":00");
    }
}//end TimePickerFragment Public Class
