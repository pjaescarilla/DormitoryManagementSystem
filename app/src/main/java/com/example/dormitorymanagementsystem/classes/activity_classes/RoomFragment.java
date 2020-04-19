package com.example.dormitorymanagementsystem.classes.activity_classes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.oop_classes.ListRooms;
import com.example.dormitorymanagementsystem.classes.oop_classes.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RoomFragment extends Fragment {

    // View variables
    private View thisView;
    private TextView textViewAvailableRooms;
    private ListView listViewRooms;

    // Database variables
    private DatabaseReference roomsTable;

    // Other variables
    private List<Room> roomList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_rooms,container,false);

        //insertDummyData();
        initializeVariables();
        getAvailableRooms();
        roomsTable.orderByChild("availability").addValueEventListener(displayRooms);

        return thisView;
    }

    // EVENTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ValueEventListener displayRooms = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            roomList.clear();
            for (DataSnapshot roomSnap : dataSnapshot.getChildren()) {
                Room thisRoom = roomSnap.getValue(Room.class);
                roomList.add(thisRoom);
            }

            ListRooms allRooms = new ListRooms(getActivity(),roomList);
            listViewRooms.setAdapter(allRooms);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    // METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void initializeVariables()
    {
        textViewAvailableRooms = thisView.findViewById(R.id.roomsAvailable);
        listViewRooms = thisView.findViewById(R.id.roomsList);
        roomsTable = FirebaseDatabase.getInstance().getReference("Rooms");
    }

    private void getAvailableRooms() {
        roomsTable.orderByChild("availability").equalTo("AVAILABLE").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textViewAvailableRooms.setText(Long.toString(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void insertDummyData()
    {
        Toast.makeText(getContext(), "Writing new data", Toast.LENGTH_SHORT).show();
        roomsTable=FirebaseDatabase.getInstance().getReference("Rooms");
        String roomNo = "N702";
        Room dummyRoom = new Room (roomNo,"not AVAILABLE",5,4);
        roomsTable.child(roomNo).setValue(dummyRoom);
    }
}
