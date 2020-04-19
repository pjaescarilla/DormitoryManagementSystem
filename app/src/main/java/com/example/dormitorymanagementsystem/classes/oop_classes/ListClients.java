package com.example.dormitorymanagementsystem.classes.oop_classes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dormitorymanagementsystem.R;

import java.util.List;

public class ListClients extends ArrayAdapter<Profile> {
    private Activity context;
    private List<Profile> clientList;

    public ListClients(Activity context, List<Profile> clientList) {
        super(context, R.layout.list_clients, clientList);
        this.context = context;
        this.clientList = clientList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View clientListView = inflater.inflate(R.layout.list_clients,null,true);

        TextView textViewFullName = clientListView.findViewById(R.id.listFullname);
        TextView textViewRoomNo = clientListView.findViewById(R.id.listRoomNo);
        TextView textViewUsername = clientListView.findViewById(R.id.listUsername);

        Profile thisClient = clientList.get(position);
        String fullName = constructFullName(thisClient.getFirstName(),thisClient.getMiddleName(),thisClient.getLastName());

        textViewFullName.setText(fullName);
        textViewRoomNo.setText(thisClient.getRoom());
        textViewUsername.setText(thisClient.getUsername());

        return clientListView;
    }

    private String constructFullName(String fName, String mName, String lName) {
        if (fName == null) {
            return "<Name not set>";
        }
        else if (mName.isEmpty()) {
            return fName + " " + lName;
        }
        else {
            return fName + " " + mName.toUpperCase().charAt(0) + ". " + lName;
        }
    }
}
