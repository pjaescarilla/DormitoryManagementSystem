package com.example.dormitorymanagementsystem.classes.activity_classes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dormitorymanagementsystem.DormVars;
import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.oop_classes.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PersonalInfoFragment extends Fragment {

    // View variables
    private View thisView;
    private TextView textViewFName, textViewMName, textViewLName, textViewGender, textViewBirthday, textViewContactNo, textViewRoomNo, textViewEName, textViewECon;

    // Database variables
    private DatabaseReference profilesTable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_occupant_info,container,false);

        initializeVariables();
        populateFields();

        return thisView;
    }

    private void initializeVariables()
    {
        textViewFName = thisView.findViewById(R.id.occupantFirstName);
        textViewMName = thisView.findViewById(R.id.occupantMiddleName);
        textViewLName = thisView.findViewById(R.id.occupantLastName);
        textViewGender = thisView.findViewById(R.id.occupantGender);
        textViewBirthday = thisView.findViewById(R.id.occupantBirthday);
        textViewContactNo = thisView.findViewById(R.id.occupantContact);
        textViewRoomNo = thisView.findViewById(R.id.occupantRoomNo);
        textViewEName = thisView.findViewById(R.id.occupantEname);
        textViewECon = thisView.findViewById(R.id.occupantEContact);

        profilesTable = FirebaseDatabase.getInstance().getReference("Profiles");
    }

    private void populateFields()
    {
        profilesTable.child(((DormVars)getActivity().getApplication()).getActiveProfile().getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Profile thisProfile = dataSnapshot.getValue(Profile.class);
                textViewFName.setText(thisProfile.getFirstName());
                textViewMName.setText(thisProfile.getMiddleName());
                textViewLName.setText(thisProfile.getLastName());
                textViewGender.setText(thisProfile.getGender());
                textViewBirthday.setText(thisProfile.getBirthDate());
                textViewContactNo.setText(thisProfile.getContactNo());
                textViewRoomNo.setText(thisProfile.getRoom());
                textViewEName.setText(thisProfile.geteContactName());
                textViewECon.setText(thisProfile.geteContactNo());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
