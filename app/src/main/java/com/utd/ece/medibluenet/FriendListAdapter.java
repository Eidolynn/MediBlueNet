package com.utd.ece.medibluenet;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nath4 on 10/31/2017.
 * Will add a Friends object to item_friends to be displayed on the ListVIew activity_friends_list
 */

public class FriendListAdapter extends ArrayAdapter<Friend> {

    private Context context;
    private int layoutResourceId;
    private List<Friend> friendsList;

    //constructor
    public FriendListAdapter(Context context, int layoutResourceId, List<Friend> friendsList){
        super(context,layoutResourceId,friendsList);
        this.context = context;
        this.friendsList = friendsList;
        this.layoutResourceId = layoutResourceId;
    }//end constructor

    @Override
    public View getView(int position, View row, ViewGroup parent){

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId,parent,false);

        FriendHolder holder = new FriendHolder();
        holder.friends = friendsList.get(position);
        holder.removeFriend = row.findViewById(R.id.removeFriendButton);
        holder.removeFriend.setTag(holder.friends);
        holder.viewBiometric = row.findViewById(R.id.viewBioButton);
        holder.viewBiometric.setTag(holder.friends);
        holder.fullName = row.findViewById(R.id.friendsFullNameTextView);
        holder.email = row.findViewById(R.id.friendsEmailTextView);

        row.setTag(holder);
        setUpItem(holder);

        return row;
    }//end getView

    private void setUpItem(FriendHolder holder){
        holder.fullName.setText(holder.friends.getFullName());
        holder.email.setText(holder.friends.getEmail());
    }//end setUpItem

    public static class FriendHolder{
        TextView fullName, email;
        Friend friends;
        Button removeFriend, viewBiometric;
    }//end FriendHolder

}//end FriendListAdapter
