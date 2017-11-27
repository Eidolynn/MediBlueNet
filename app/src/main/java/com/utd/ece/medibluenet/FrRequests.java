package com.utd.ece.medibluenet;

import java.io.Serializable;

/**
 * Created by nath4 on 10/30/2017.
 * FrRequests class -- Used for populating friends list as a listView in FriendRequestsListView.java
 */

public class FrRequests implements Serializable {

    private String email = "", uid = "";

    public FrRequests(String email, String uid){
        this.setEmail(email);
        this.setUid(uid);
    }//end class constructor

    public void setEmail(String email){
        this.email = email;
    }//end setEmail

    public void setUid(String uid){
        this.uid = uid;
    }//end Uid

    public String getEmail(){
        return email;
    }//end getEmail

    public String getUid(){
        return uid;
    }//end getUid

}//end FrRequests Class
