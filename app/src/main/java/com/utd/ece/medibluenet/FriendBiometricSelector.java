package com.utd.ece.medibluenet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class FriendBiometricSelector extends AppCompatActivity {

    private String fUid ="", email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_biometric_selector);
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            fUid = extras.getString("friendUid");
            email = extras.getString("friendEmail");
        }

        TextView textView = (TextView) findViewById(R.id.biometricTitle);
        textView.setText(email+"'s Biometric Profile");

    }//end onCreate

    //biometric onClick
    public void displayBiometric(View v){
        Intent i = new Intent(this, BiometricGraphView.class);
        i.putExtra("biometric",v.getTag().toString());
        i.putExtra("friendUid",fUid);
        startActivity(i);
    }//end biometricOnClick

}
