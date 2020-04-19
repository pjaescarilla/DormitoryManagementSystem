package com.example.dormitorymanagementsystem.classes.activity_classes;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.service.autofill.Dataset;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dormitorymanagementsystem.DormVars;
import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.oop_classes.ListClients;
import com.example.dormitorymanagementsystem.classes.oop_classes.Profile;
import com.example.dormitorymanagementsystem.classes.oop_classes.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClientFragment extends Fragment {

    // View variables
    private View thisView;
    private EditText editTextSearch;
    private ListView listViewClients;

    // Database variables
    private DatabaseReference profilesTable;

    // Other variables
    private List<Profile> clientList = new ArrayList<>();
    private AdminEditDialog adminDialog = new AdminEditDialog();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_clients,container,false);

        initializeVariables();
        profilesTable.addValueEventListener(displayClients);
        editTextSearch.addTextChangedListener(searchThisClient);
        listViewClients.setOnItemClickListener(showDetailsForAdmin);

        return thisView;
    }

    // METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void initializeVariables() {
        editTextSearch = thisView.findViewById(R.id.clientsSearch);
        listViewClients = thisView.findViewById(R.id.clientsList);

        profilesTable = FirebaseDatabase.getInstance().getReference("Profiles");
    }

    public void closeDialog() {
        adminDialog.dismiss();
    }

    // EVENTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ValueEventListener displayClients = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            clientList.clear();
            for (DataSnapshot clientSnap : dataSnapshot.getChildren()) {
                Profile thisClient = clientSnap.getValue(Profile.class);
                if (thisClient.getRole().equals("Occupant")) {
                    clientList.add(thisClient);
                }
            }

            ListClients allClients = new ListClients(getActivity(),clientList);
            listViewClients.setAdapter(allClients);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    TextWatcher searchThisClient = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (editTextSearch.getText().toString().isEmpty()) {
                profilesTable.addValueEventListener(displayClients);
            }
            else {
                profilesTable.orderByChild("firstName").startAt(editTextSearch.getText().toString()).endAt(editTextSearch.getText().toString()+"\uf8ff").addValueEventListener(displayClients);
            }
        }
    };

    AdapterView.OnItemClickListener showDetailsForAdmin = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ((DormVars)getActivity().getApplication()).setAdminSelectedProfile(clientList.get(position));
            adminDialog.show(getFragmentManager(),"AdminEdit");
        }
    };

}
