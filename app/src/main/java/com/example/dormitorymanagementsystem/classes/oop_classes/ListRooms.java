package com.example.dormitorymanagementsystem.classes.oop_classes;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.dormitorymanagementsystem.R;

import java.util.List;

public class ListRooms extends ArrayAdapter<Room> {
    private Activity context;
    private List<Room> roomList;

    public ListRooms(Activity context, List<Room> roomList) {
        super(context, R.layout.list_rooms, roomList);
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewRooms = inflater.inflate(R.layout.list_rooms,null,true);

        TextView textViewRoomNo = listViewRooms.findViewById(R.id.listRoomNo);
        TextView textViewCapacity = listViewRooms.findViewById(R.id.listRoomCapacity);
        TextView textViewOccupantCt = listViewRooms.findViewById(R.id.listRoomOccupants);
        TextView textViewAvailability = listViewRooms.findViewById(R.id.listRoomAvailability);

        Room thisRoom = roomList.get(position);

        textViewRoomNo.setText(thisRoom.getRoomNo());
        textViewCapacity.setText("Capacity: "+thisRoom.getCapacity());
        textViewOccupantCt.setText("# of Occupants: "+thisRoom.getCurrentOccupants());
        textViewAvailability.setText(thisRoom.getAvailability().toUpperCase());

        if (thisRoom.getAvailability().toUpperCase().equals("AVAILABLE")) {
            textViewAvailability.setTextColor(ContextCompat.getColor(context,R.color.available));
        }

        return listViewRooms;
    }
}
