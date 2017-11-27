package com.utd.ece.medibluenet;

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

public class FriendRequestsListView extends AppCompatActivity {

    private FrRequestsListAdapter adapter;
    List<FrRequests> requests = new ArrayList<>();
    List<String> uids = new ArrayList<>();
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;

    private String email = null; //to be used for messy business

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        populateUidList();
        setUpAdapter();

        ProgressBar bar = (ProgressBar)findViewById(R.id.friendRequestsProgressBar);
        bar.setVisibility(ProgressBar.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //if friend requesets exist
                ProgressBar bar = (ProgressBar)findViewById(R.id.friendRequestsProgressBar);
                bar.setVisibility(ProgressBar.INVISIBLE);
                if(uids.size()>0) {
                    populateListView();
                }//end if
                else{
                    TextView noNewFriends = (TextView) findViewById(R.id.noNewFriends);
                    noNewFriends.setVisibility(View.VISIBLE);
                }//end else
            }
        }, 1000);   //5 seconds
    }//end onCreate

    private void populateUidList() {
        setUpDatabase(getcUid());
        mDatabaseRef = mDatabaseRef.child("friendsList");
        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!(boolean)dataSnapshot.getValue()) {
                    uids.add(dataSnapshot.getKey());
                }//end if friendList child is false indicating that there is a pending request
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
                Toast.makeText(FriendRequestsListView.this, "Database error. "+databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }//end populateAdapter

    private void setUpDatabase(String uid) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mFirebaseDatabase.getReference("users/"+uid);

    }//end setUpDatabase

    private void setUpAdapter(){
        adapter = new FrRequestsListAdapter(FriendRequestsListView.this, R.layout.item_frequest, requests );
        ListView friendReqListView = (ListView) findViewById(R.id.friendReqListView);
        friendReqListView.setAdapter(adapter);
    }//end set up Adapter

    private String getcUid(){
        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser cUser = mFirebaseAuth.getCurrentUser();
        if (cUser != null) {
            return cUser.getUid();
        }
        else{
            Toast.makeText(FriendRequestsListView.this, "Interal Error...Close and try again :(", Toast.LENGTH_SHORT).show();
            return null;
        }

    }  //end getcUid method

    public void acceptFrRequest(View v){
        FrRequests row = (FrRequests)v.getTag();

        //update Fbase-- change value of frUid  on both sides (current users and friend's side
        setUpDatabase(getcUid());
        mDatabaseRef.child("friendsList").child(row.getUid()).setValue(true);
        setUpDatabase(row.getUid());
        mDatabaseRef.child("friendsList").child(getcUid()).setValue(true);

        //remove row from listView
        adapter.remove(row);
    }//end acceptFriendRequest

    public void denyFrRequest(View v){
        FrRequests row = (FrRequests)v.getTag();

        //update Fbase-- change value of frUid  on both sides (current users and friend's side
        setUpDatabase(getcUid());
        mDatabaseRef.child("friendsList").child(row.getUid()).removeValue();
        setUpDatabase(row.getUid());
        mDatabaseRef.child("friendsList").child(getcUid()).removeValue();

        //remove row from listView
        adapter.remove(row);
    }//end acceptFriendRequest

    private void populateListView(){

        //search through the userLookUp child for and find the uid that corresponds to the requested friendsEmail var
        mDatabaseRef = mFirebaseDatabase.getReference("userLookUp");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < uids.size(); i++) {
                    for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                        if (uids.get(i).equals(snapShot.getKey())) {
                            FrRequests request = new FrRequests(snapShot.getValue().toString(), uids.get(i));
                            adapter.add(request);
                            break;
                        }//end if match found
                    }//end for each child
                }//end for the data in userLookUp
            }//end for number of uids

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FriendRequestsListView.this, "Database Error: "+databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        });//end ListenerForSingleValueEvent
    }//end forRequestEmail
}//end FriendRequestsListView Activity
