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
 * Created by nath4 on 10/30/2017.
 */

public class FrRequestsListAdapter extends ArrayAdapter<FrRequests> {

    private List<FrRequests> requests;
    private int layoutResourceId;
    private Context context;

    //constructor
    public FrRequestsListAdapter (Context context, int layoutResourceId,List<FrRequests> requests){
        super(context, layoutResourceId, requests);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.requests = requests;
    }//end FrRequestsListAdapter

    @Override
    public View getView(int position, View row, ViewGroup parent) {
//        FrRequestsHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        FrRequestsHolder holder = new FrRequestsHolder();
        holder.frRequests = requests.get(position);
        holder.acceptFrRequest = row.findViewById(R.id.acceptRequest);
        holder.acceptFrRequest.setTag(holder.frRequests);
        holder.denyFrRequest = row.findViewById(R.id.denyRequest);
        holder.denyFrRequest.setTag(holder.frRequests);

        holder.email = row.findViewById(R.id.requestorEmail);

        row.setTag(holder);
        setupItem(holder);
        return row;
    }// getView

    private void setupItem(FrRequestsHolder holder) {
        holder.email.setText(holder.frRequests.getEmail());
    }

    public static class FrRequestsHolder {
        FrRequests frRequests;
        TextView email;
        Button acceptFrRequest;
        Button denyFrRequest;
    }//FrRequestsHolder

}//end FrRequestsListAdapter
