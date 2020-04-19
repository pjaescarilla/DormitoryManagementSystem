package com.example.dormitorymanagementsystem.classes.activity_classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.oop_classes.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterDialog extends AppCompatDialogFragment {

    // View variables
    private TextView textViewSubmit;
    private ProgressBar progressBarRegister;
    private EditText editTextEmail, editTextUsername, editTextPassword, editTextPassword2;
    private View thisView;

    // Database variables
    private DatabaseReference profilesTable;
    private FirebaseAuth mAuth;

    // Other variables
    private boolean usernameValid = false;
    private RegisterDialogListener listener;

    // OVERRIDE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        thisView = inflater.inflate(R.layout.dialog_register,null);
        builder.setView(thisView)
                .setTitle("Register")
                .setNegativeButton("Cancel",null);

        initializeVariables();
        textViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewAccount();
            }
        });
        editTextUsername.addTextChangedListener(checkUsernameValidity);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (RegisterDialogListener) context;
    }

    // EVENT HANDLERS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    TextWatcher checkUsernameValidity = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            profilesTable.orderByChild("username").equalTo(editTextUsername.getText().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount()!=0)
                    {
                        editTextUsername.setError("Username already taken");
                        usernameValid = false;
                    }
                    else {
                        usernameValid = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    };

    // METHOD ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void initializeVariables() {
        textViewSubmit = thisView.findViewById(R.id.registerSubmitText);
        progressBarRegister = thisView.findViewById(R.id.registerProgressBar);
        editTextEmail = thisView.findViewById(R.id.registerEmail);
        editTextUsername = thisView.findViewById(R.id.registerUsername);
        editTextPassword = thisView.findViewById(R.id.registerPassword);
        editTextPassword2 = thisView.findViewById(R.id.registerPassword2);

        profilesTable = FirebaseDatabase.getInstance().getReference("Profiles");
        mAuth=  FirebaseAuth.getInstance();
    }

    private void registerNewAccount() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();

        // Add to Firebase authentication
        if (allFieldsValid()) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Add to database
                        Profile newProfile = new Profile(editTextUsername.getText().toString().trim(), editTextEmail.getText().toString().trim(), "Occupant");
                        profilesTable.child(editTextUsername.getText().toString().trim()).setValue(newProfile);

                        Toast.makeText(getContext(), "Account Created!", Toast.LENGTH_SHORT).show();
                        listener.closeDialog();
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        progressBarRegister.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private boolean allFieldsValid() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String password2 = editTextPassword2.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return false;
        }
        
        if (!usernameValid) {
            editTextUsername.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Required field");
            editTextPassword.requestFocus();
            return false;
        }

        if (password2.isEmpty()) {
            editTextPassword2.setError("Required field");
            editTextPassword2.requestFocus();
            return false;
        }

        if (!password.equals(password2)) {
            Toast.makeText(getContext(), "Password does not match", Toast.LENGTH_SHORT).show();
            editTextPassword.setText("");
            editTextPassword2.setText("");
            editTextPassword.requestFocus();
            return false;
        }

        progressBarRegister.setVisibility(View.VISIBLE);
        return true;
    }

    // LISTENER INTERFACE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public interface RegisterDialogListener {
        void closeDialog();
    }
}
