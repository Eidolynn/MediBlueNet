package com.utd.ece.medibluenet;

import java.io.Serializable;

/**
 * Created by nath4 on 10/31/2017.
 */

public class Friend implements Serializable {

    private String fullName ="", email = "",uid="";

    public Friend(String fullName, String email, String uid){
        this.fullName = fullName;
        this.email = email;
        this.uid = uid;
    }//end constructor

    public void setFullName(String fullName){
        this.fullName = fullName;
    }//end setFullName Function

    public void setEmail(String email){
        this.email = email;
    }//end setFullName Function

    public String getFullName() {
        return fullName;
    }//end getFullName Function

    public String getEmail() {
        return email;
    }//end getEmail Function
    public void setUid(String uid){ this.uid = uid;}//end set uid method
    public String getUid(){return uid;}//end get uid method

}//end Friend class
