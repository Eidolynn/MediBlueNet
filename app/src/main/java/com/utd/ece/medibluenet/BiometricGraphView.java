package com.utd.ece.medibluenet;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.GregorianCalendar;
import java.text.DateFormat;


public class BiometricGraphView extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private String bioChosen, quickFix, fUid;
    List<Biometric> biometricArray = new ArrayList<>();
    private Boolean datafound = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric_graph);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            bioChosen = extras.getString("biometric");
            if(extras.getString("friendUid") != null)
                fUid = extras.getString("friendUid");
        }
        populateBiometricListArray(bioChosen);
        ProgressBar bar = (ProgressBar)findViewById(R.id.friendRequestsProgressBar);
        bar.setVisibility(ProgressBar.VISIBLE);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                ProgressBar bar = (ProgressBar)findViewById(R.id.friendRequestsProgressBar);
                bar.setVisibility(ProgressBar.INVISIBLE);
                if(datafound){
                    GraphView g = (GraphView) findViewById(R.id.graphView);
                    g.setVisibility(GraphView.VISIBLE);
                    populateGraphView();
                    setLabelFormat();
                }//end data found
                else{
                    TextView textView = (TextView) findViewById(R.id.noDataFound);
                    textView.setText("No data was Found");
                    textView.setVisibility(TextView.VISIBLE);
                }//end data not found
            }
        },1000);

        //Create GraphView with ListArray contents
        //add ListArray to ListView adapter

        //listen for "SetDate click"
            //Robustly check to see if dates are valid
            //RecreateGraphView with only the biometrics between the given dates

        //Listen for "ShowAll click"
            //Call Create GraphView with ListArray Content

    }//end onCreate method

    private void populateGraphView() {
        GraphView graph = (GraphView) findViewById(R.id.graphView);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String dateInString;
        Date date;
        try {
            for(int i = 0; i < biometricArray.size(); i++){
                dateInString = biometricArray.get(i).getFullTime();
                date=sdf.parse(dateInString);
                series.appendData(new DataPoint(date, Double.parseDouble(biometricArray.get(i).getValue())),true,1000);
            }//end for each biometric under child node. What's up Bae the password is lympNode spagetti spagehtii? no w.e.
        }catch (Exception e ){
            Toast.makeText(BiometricGraphView.this, "Excpetion- SDF - "+e.toString(),Toast.LENGTH_SHORT);
        }//end try and catch statement
        graph.addSeries(series);
    }//end populateGraphView

    private void populateBiometricListArray(final String biometric) {
        if( fUid != null)
            setUpDatabase(fUid,biometric);
        else
            setUpDatabase(getcUid(),biometric);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Biometric b;
                if(dataSnapshot.getChildrenCount() == 0){
                    datafound = false;
                    return;
                }//end if no data found
                for( DataSnapshot snapShot : dataSnapshot.getChildren()){
                    b = new Biometric(biometric, snapShot.child("fulltime").getValue().toString(),
                                        snapShot.child("figure").getValue().toString());
                    biometricArray.add(b);
                }//end for each child loop
            }//end onDataChange

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BiometricGraphView.this,"DatabaseError - populateBiometricList",Toast.LENGTH_SHORT);
            }//onCancelled
        });
    }//end populateBiometricList

    private void setUpDatabase(String uid, String biometric){
        mFirebaseDatabase= mFirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("users/"+uid+"/biometrics/"+biometric);
    }//end setUpDatabase

    private String getcUid(){
        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser cUser = mFirebaseAuth.getCurrentUser();
        if (cUser != null) {
            return cUser.getUid();
        }
        else{
            Toast.makeText(BiometricGraphView.this, "Interal Error...Close and try again :(", Toast.LENGTH_SHORT).show();
            return null;
        }
    }  //end getcUid method

    //set the format for the graphView to have all dates and shtuff
    private void setLabelFormat(){
        GraphView g = (GraphView)findViewById(R.id.graphView);
        g.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(BiometricGraphView.this));
        g.getGridLabelRenderer().setNumVerticalLabels(biometricArray.size());
        g.setTitle(biometricArray.get(0).getBiometric()+" in units ("+biometricArray.get(0).getUnitString()+")");
        g.getGridLabelRenderer().setHumanRounding(true);
        g.getGridLabelRenderer().setTextSize(40);
        g.getGridLabelRenderer().reloadStyles();
        g.getViewport().setScrollable(true);
    }//end

    //onClick method for date picker buttons
    public void datePicker(View view){
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getFragmentManager(), "date");
        quickFix = view.getTag().toString();
    }//end date picket onClick

    //set the textViews to display current dates
    private void setDate(final Calendar calendar){
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        if(quickFix.equals("startDate"))
            ((TextView) findViewById(R.id.startDate)).setText(dateFormat.format(calendar.getTime()));
        else
            ((TextView) findViewById(R.id.endDate)).setText(dateFormat.format(calendar.getTime()));
    }//end setDate method

    //recieves call back when user sets a date
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day){
            Calendar c = new GregorianCalendar(year,month,day);
            setDate(c);
    }

    //datePicker Fragment
    public static class DatePickerFragment extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c  = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),
                    (DatePickerDialog.OnDateSetListener) getActivity(),
                    year, month, day);
        }//end onCreateDialog
    }//end DatePickerFragment Static Class

    //onClick method for "Set Time" buttons -- it's funny
    public void nosePicker(View view){
        if(view.getTag().toString().equals("endTime")){
            Button b = (Button) findViewById(R.id.refreshGraphButton);
            b.setEnabled(true);
        }//end if

        DialogFragment fragment = new TimePickerFragment();
        Bundle extra = new Bundle();
        extra.putString("timeViewTag",view.getTag().toString());
        fragment.setArguments(extra);
        fragment.show(getFragmentManager(),"TimePicker");
    }//end nosePicker

    //onClick for refreshing the graph
    public void refreshGraph(View view){

        //set min and max x values for graph
        GraphView graph = (GraphView) findViewById(R.id.graphView);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
        Toast.makeText(BiometricGraphView.this, "Hello", Toast.LENGTH_SHORT);
        try {
            String fullDate = ((TextView) findViewById(R.id.startDate)).getText().toString();
            Date dMin = sdf.parse(fullDate
                                +" "+((TextView) findViewById(R.id.startTime)).getText().toString());
            Date dMax = sdf.parse(((TextView) findViewById(R.id.endDate)).getText().toString()
                    +" "+((TextView) findViewById(R.id.endTime)).getText().toString());
            if(dMax.before(dMin)){
                Toast.makeText(BiometricGraphView.this, "Please select a valid date range.", Toast.LENGTH_SHORT);
                Button b = (Button) findViewById(R.id.refreshGraphButton);
                b.setEnabled(false);
                return;
            }//end if
            graph.getViewport().setMinX(dMin.getTime());
            graph.getViewport().setMaxX(dMax.getTime());
            graph.getViewport().setXAxisBoundsManual(true);
        }catch (Exception e){
            Toast.makeText(BiometricGraphView.this, "Excpetion- SDF - "+e.toString(),Toast.LENGTH_SHORT);
        }

        Button b = (Button) findViewById(R.id.refreshGraphButton);
        b.setEnabled(false);

    }//end refreshGraph

}//end BiometricGraphView class
