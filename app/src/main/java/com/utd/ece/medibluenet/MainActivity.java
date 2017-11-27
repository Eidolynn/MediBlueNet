package com.utd.ece.medibluenet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //init constants
    public static final String ANONYMOUS = "anonymous";

    //Firebase Instance Variables (For Writing and Reading)
    private FirebaseDatabase mFirebaseDatabase; //firebase databaseobject -- entry point for app to access fbase
        private DatabaseReference mMessagesDatabaseReference; //dbase reference object - class that references specific part of databse
    private ChildEventListener mChildEventListener; //for reading from FireBase
    //Firebase Instance Vars (For Authentication)
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static int RC_SIGN_IN = 123;

    //init Vars
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //init vars
        mUsername = ANONYMOUS;

        //init FireBase instance variables
        mFirebaseDatabase = FirebaseDatabase.getInstance(); //Get instance of class - main access point
        mFirebaseAuth = FirebaseAuth.getInstance(); //for Authentication...init state listener at bottom of onCreate
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("AccType"); //reference node in databases

        //display activity_main.xml and toolbar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //init Firebase Authentication State Listener
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //FirebaseAuth  parameter can guarantee if user is logged in or not (different from FireBaseAuth init up top
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    //user is signed in
                    Toast.makeText(MainActivity.this, "You're signed in. Welcome, " + user.getDisplayName() + ".", Toast.LENGTH_SHORT).show();
                    //add 2 helper methods
                    onSignedInInit(user.getDisplayName());
                    //set username in navigation window
//                    TextView nameTextView = (TextView) findViewById(R.id.userName);
//                    nameTextView.setText(user.getDisplayName());

                }//end if
                else{
                    //user is signed out
                    onSignedOutInit();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setTheme(R.style.FBaseTheme)
                                    .setLogo(R.drawable.mbnlogo)
                                    .setIsSmartLockEnabled(false) //smart lock saves users credentials and trys to log them in
                                    .setProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }//end else
            }//end onAuthStateChanged

        };//end init firebase AuthStateListener

    }//end onCreate method

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){

            case R.id.action_logout:
                //signout
                AuthUI.getInstance().signOut(this);
                return true;
            default:return super.onOptionsItemSelected(item);
        }//end switch
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_friends) {
            //change view to friends
            startActivity(new Intent(MainActivity.this, FriendListView.class));

        } else if (id == R.id.nav_findfriend) {
            //change view to find friends
            startActivity(new Intent(MainActivity.this, RequestFriend.class));

        } else if (id == R.id.nav_requests) {
            //change view to friend requests
            startActivity(new Intent(MainActivity.this, FriendRequestsListView.class));

        } else if (id == R.id.nav_share) {
            //TODO: Change to some other shit
        } else if (id == R.id.nav_send) {
            //TODO: Change to some other shit
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //to allow the user to cancel the sign in .
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        //if activity that's being returned is coming from login flow
        if (requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK){
                Toast.makeText(this,"Log-in Success",Toast.LENGTH_SHORT).show();
            }//end result loged in
            else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this,"Log-in Cancelled",Toast.LENGTH_SHORT).show();
                finish();
            }//end result cancelled
        }//end request
    }//end onActivityResult

    @Override
    protected void onResume(){
        super.onResume();
        // attach the AuthStateListener
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }//end onResume

    @Override
    protected void onPause(){
        super.onPause();
        //removal of AuthStateListener
        if(mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();   //clean up for reasons...in case rotation or some shit.idk man im tried
    }//end onPause

    private void attachDatabaseReadListener(){
        //create eventListener only if an event listener does not exist already
        if(mChildEventListener == null) {
            //Instantiate a new ChildEventListener -- listens for changes in the data
            mChildEventListener = new ChildEventListener() {
                //DataSnapshot contains data from Firebase at a specific location
                // and at the exact time the listener is triggered
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //dataSnapshot's getValue method will get the messages
                    //can take class as a parameter... data will be deserialized from database into a FriendlyMessage Object
                    //TODO: add new biometric data to log
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };//end ChildEventListener
            //The reference mMessagesDatabaseReference defines what listening to
            // listener object (addChildEventListener defines what exactly happened with the data
            //Since mMessageDataRef only ref messages then addChildEventListener called only when
            //data with in Messages node has been changed.
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }//end if robustness
    }//end attach ReadListener

    private void detachDatabaseReadListener(){
        //removes listener...if statement for robustness..should not detach a listener if there is not a listener
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }//end detach ReadListener

    //Helper method to take user to message screen
    private void onSignedInInit(String username){
        mUsername = username;
        attachDatabaseReadListener();

    }//end onSignedinInit

    //Helper method to take user to login page once logged out
    private void onSignedOutInit(){
        //unset username...detach listener
        mUsername = ANONYMOUS;
        detachDatabaseReadListener();
    }//end onSignedOutInit

    //biometric onClick
    public void displayBiometric(View v){
        Intent i = new Intent(this, BiometricGraphView.class);
        i.putExtra("biometric",v.getTag().toString());
        startActivity(i);
    }//end biometricOnClick



}//end MainActivity
