package com.utd.ece.medibluenet;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendListView extends AppCompatActivity {

    private FriendListAdapter adapter;
    List<Friend> friends = new ArrayList<>();
    List<String> uids = new ArrayList<>();
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private int iterator;
    private String tempUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        setUpAdapter();
        getUids();

        ProgressBar bar = (ProgressBar)findViewById(R.id.friendsListProgressBar);
        bar.setVisibility(ProgressBar.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                ProgressBar bar = (ProgressBar)findViewById(R.id.friendsListProgressBar);
                bar.setVisibility(ProgressBar.INVISIBLE);
                if(uids.size()>0) {
                    populateListView();
                }//end if
                else{
                    TextView noNewFriends = (TextView) findViewById(R.id.noFriends);
                    noNewFriends.setVisibility(View.VISIBLE);
                }//end else
            }
        },1000);//end wait for 1 sec..it is possible to wait too long here.

    }//end OnCreate Method

    private void setUpAdapter(){
        adapter = new FriendListAdapter(FriendListView.this, R.layout.item_friends, friends );
        ListView friendListView = (ListView) findViewById(R.id.friendListView);
        friendListView.setAdapter(adapter);
    }//end setUpAdapter

    private void setUpDatabase(String uid) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mFirebaseDatabase.getReference("users/"+uid);
    }//end setUpDatabase

    private String getcUid(){
        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser cUser = mFirebaseAuth.getCurrentUser();
        if (cUser != null) {
            return cUser.getUid();
        }
        else{
            Toast.makeText(FriendListView.this, "Interal Error...Close and try again :(", Toast.LENGTH_SHORT).show();
            return null;
        }

    }  //end getcUid method

    private void getUids() {
        setUpDatabase(getcUid());
        mDatabaseRef = mDatabaseRef.child("friendsList");
        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if((boolean)dataSnapshot.getValue())
                    uids.add(dataSnapshot.getKey());
            }//end onChildAdded

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Do nothing
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Do nothing
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //Do nothing
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FriendListView.this, "Database error. "+databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }//end populateAdapter

    private void populateListView() {
        //search through the userLookUp child for and find the uid that corresponds to the requested friendsEmail var
        try{
            for (iterator = 0; iterator < uids.size(); iterator++) {
                mDatabaseRef = mFirebaseDatabase.getReference("users/" + uids.get(iterator));
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String tempEmail = null, tempName = null;
                        tempUid = dataSnapshot.getRef().getKey();
                        for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                            if (snapShot.getKey().equals("userEmail"))
                                tempEmail = snapShot.getValue().toString();
                            if (snapShot.getKey().equals("userName"))
                                tempName = snapShot.getValue().toString();
                        }//end for each children under mDatabaseRef node
                        if (tempName != null & tempEmail != null) {
                            Friend friend = new Friend(tempName, tempEmail,tempUid);
                            adapter.add(friend);//adapter is not working properly...maybe check values of tempName and tempEmail
                        }
                    }//end onDataChange in userLookup

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(FriendListView.this, "Database Error: " + databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });//end ListenerForSingleValueEvent
                Thread.sleep(2000);
            }//end for reach uid
         }catch (InterruptedException e){
            e.printStackTrace();
        }//end try and  catch
    }//end populate ListView

    public void removeFriendOnClick(View v){
        Friend row = (Friend)v.getTag();
//        Toast.makeText(FriendListView.this,"v.getId:"+row.getUid(),Toast.LENGTH_SHORT).show();
        //remove child from cUser
        mDatabaseRef = mFirebaseDatabase.getReference("users/"+getcUid()+"/friendsList");
        mDatabaseRef.child(row.getUid()).removeValue();
        //remove child from friendUser
        mDatabaseRef = mFirebaseDatabase.getReference("users/"+row.getUid()+"/friendsList");
        mDatabaseRef.child(getcUid()).removeValue();

        adapter.remove(row);
    }//end removeFriendOnClick method

    public void viewBiometricOnClick(View v){
        Intent i = new Intent(this, FriendBiometricSelector.class);
        i.putExtra("friendUid",((Friend)v.getTag()).getUid());
        i.putExtra("friendEmail", ((Friend)v.getTag()).getEmail());
        startActivity(i);
    }//end viewBiometricOnClick
}
