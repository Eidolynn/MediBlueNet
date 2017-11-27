package com.utd.ece.medibluenet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestFriend extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase; //firebase databaseobject -- entry point for app to access fbase
    private DatabaseReference mFirebaseRef; //dbase reference object - class that references specific part of databse
    private FirebaseAuth mFirebaseAuth;

    private String friendsEmail, userUid, friendUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser cUser = mFirebaseAuth.getCurrentUser();
        if (cUser != null) {
            userUid = cUser.getUid();
        }

        //set an onClick listener to the friendRequestButton
        final Button friendRequestButton = (Button) findViewById(R.id.friendRequest);
        friendRequestButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //when it is clicked get the inputted email.
                EditText requestEmail = (EditText) findViewById(R.id.friendsEmail);
                friendsEmail = requestEmail.getText().toString();
                forRequestEmail();
            }
        });//end onClick listener

    }//end onCreate

    private void forRequestEmail(){
        //search through the userLookUp child for and find the uid that corresponds to the requested friendsEmail var
        mFirebaseRef = mFirebaseDatabase.getReference("userLookUp");
        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapShot:dataSnapshot.getChildren()) {
                    if(friendsEmail.equals(snapShot.getValue().toString())){
                        friendUid = snapShot.getKey();
                        if(userUid.equals(snapShot.getKey())){
                            Toast.makeText(RequestFriend.this,"You are already your own friend.", Toast.LENGTH_SHORT).show();
                            return;
                        }//duh
                        Toast.makeText(RequestFriend.this, "Match found! Adding:"+friendUid,Toast.LENGTH_SHORT).show();
                        //under the current and friend's node, set a friendsList child to false...indicating a pending request
                        mFirebaseRef = mFirebaseDatabase.getReference("users/"+userUid);
                        mFirebaseRef.child("friendsList").child(friendUid).setValue(null);
                        mFirebaseRef = mFirebaseDatabase.getReference("users/"+friendUid);
                        mFirebaseRef.child("friendsList").child(userUid).setValue(false);
                        return;
                    }//end if match found
                }//end for each child
                Toast.makeText(RequestFriend.this, "No match found :( Check the email.",Toast.LENGTH_SHORT).show();
            }//end for the data in userLookUp

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RequestFriend.this, "Database Error: "+databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        });//end ListenerForSingleValueEvent

    }//end forRequestEmail

}//end RequestFriend
