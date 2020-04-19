package com.example.dormitorymanagementsystem.classes.activity_classes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.dormitorymanagementsystem.DormVars;
import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.oop_classes.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserDetailsEntryDialog extends AppCompatDialogFragment {

    // View variables
    private View thisView;
    private EditText editTextFirstName, editTextMidName, editTextLastName, editTextContactNo, editTextEContactName, editTextEContactNo;
    private TextView textViewBirthday, textViewSubmit;
    private Spinner spinnerGender;
    private ProgressBar progressBarEntry;

    // Database variables
    private DatabaseReference profilesTable;

    // Other variables
    private List <EditText> fieldsToValidate = new ArrayList<EditText>();
    private UserEntryDialogListener listener;


    // OVERRIDE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        thisView = inflater.inflate(R.layout.dialog_user_details_entry,null);
        builder.setView(thisView)
                .setTitle("New User")
                .setCancelable(false);

        initializeVariables();
        textViewBirthday.setOnClickListener(showDateDialog);
        textViewSubmit.setOnClickListener(writeToDB);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (UserEntryDialogListener) context;
    }

    // EVENTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    View.OnClickListener writeToDB = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBarEntry.setVisibility(View.VISIBLE);
            if (allFieldsValidated()) {
                String fName = editTextFirstName.getText().toString();
                String mName = editTextMidName.getText().toString();
                String lName = editTextLastName.getText().toString();
                String gender = spinnerGender.getSelectedItem().toString();
                String birthday = textViewBirthday.getText().toString();
                String contactNo = editTextContactNo.getText().toString();
                String eName = editTextEContactName.getText().toString();
                String eNo = editTextEContactNo.getText().toString();
                String username = ((DormVars)getActivity().getApplication()).getActiveProfile().getUsername();
                String email = ((DormVars)getActivity().getApplication()).getActiveProfile().getEmail();

                Profile updatedProfile = new Profile(username,fName,mName,lName,gender,birthday,contactNo,eName,eNo,email,"Occupant","Unassigned");
                profilesTable.child(username).setValue(updatedProfile);
                listener.closeDialog();
            }
        }
    };

    View.OnClickListener showDateDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int day,month,year;
            if (textViewBirthday.getText().toString().equals("Select Date")) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }
            else {
                String[] prevDate = textViewBirthday.getText().toString().split("/");
                year=Integer.parseInt(prevDate[2]);
                month=Integer.parseInt(prevDate[0])-1;
                day=Integer.parseInt(prevDate[1]);
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog_MinWidth,getSelectedBirthDate,year,month,day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }
    };

    DatePickerDialog.OnDateSetListener getSelectedBirthDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month+=1;
            String date = month+"/"+dayOfMonth+"/"+year;
            textViewBirthday.setText(date);
        }
    };

    // METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void initializeVariables()
    {
        editTextFirstName = thisView.findViewById(R.id.entryFirstName);
        editTextMidName = thisView.findViewById(R.id.entryMiddleName);
        editTextLastName = thisView.findViewById(R.id.entryLastName);
        editTextContactNo = thisView.findViewById(R.id.entryContactNumber);
        editTextEContactName = thisView.findViewById(R.id.entryEContactName);
        editTextEContactNo = thisView.findViewById(R.id.entryEContactNo);
        spinnerGender = thisView.findViewById(R.id.entryGender);
        textViewBirthday = thisView.findViewById(R.id.entryBirthday);
        textViewSubmit = thisView.findViewById(R.id.entrySubmit);
        progressBarEntry = thisView.findViewById(R.id.entryProgressBar);

        fieldsToValidate.add(editTextFirstName);
        fieldsToValidate.add(editTextLastName);
        fieldsToValidate.add(editTextContactNo);
        fieldsToValidate.add(editTextEContactName);
        fieldsToValidate.add(editTextEContactNo);

        profilesTable = FirebaseDatabase.getInstance().getReference("Profiles");
    }

    private boolean allFieldsValidated()
    {
        for (EditText thisField : fieldsToValidate){
            if (thisField.getText().toString().isEmpty()) {
                progressBarEntry.setVisibility(View.GONE);
                thisField.setError("Required field");
                thisField.requestFocus();
                return false;
            }
        }

        if (spinnerGender.getSelectedItemPosition()==0) {
            progressBarEntry.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Please select a gender", Toast.LENGTH_SHORT).show();
            spinnerGender.requestFocus();
            return false;
        }

        if (textViewBirthday.getText().toString().equals("Select Date")) {
            progressBarEntry.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Please enter your birthday", Toast.LENGTH_SHORT).show();
            textViewBirthday.requestFocus();
            return false;
        }

        return true;
    }

    // LISTENER INTERFACE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public interface UserEntryDialogListener {
        void closeDialog();
    }
}
