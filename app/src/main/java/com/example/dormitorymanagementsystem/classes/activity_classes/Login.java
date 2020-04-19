package com.example.dormitorymanagementsystem.classes.activity_classes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dormitorymanagementsystem.DormVars;
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

public class Login extends AppCompatActivity implements RegisterDialog.RegisterDialogListener {

    // View variables
    private Button buttonSignIn;
    private TextView textViewRegLink;
    private ProgressBar progressBarLogin;
    private EditText editTextUsername,editTextPassword;
    private Spinner spinnerRoles;

    // Database variables
    private DatabaseReference profilesTable;
    private FirebaseAuth mAuth;

    // Other variables
    private RegisterDialog reg = new RegisterDialog();
    private AdminEditDialog testReg = new AdminEditDialog();

    // OVERRIDE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeVariables();
        mAuth.signOut();
        textViewRegLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg.show(getSupportFragmentManager(),"Registry");
                //testReg.show(getSupportFragmentManager(),"Test");
            }
        });

        buttonSignIn.setOnClickListener(executeSignIn);
    }

    @Override
    public void closeDialog() {
        reg.dismiss();
    }


    // EVENT HANDLERS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    View.OnClickListener executeSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBarLogin.setVisibility(View.VISIBLE);
            if (allFieldsValid()) {
                String username = editTextUsername.getText().toString();
                profilesTable.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String password = editTextPassword.getText().toString();
                        String role = spinnerRoles.getSelectedItem().toString();

                        if (dataSnapshot.getChildrenCount() == 0) {
                            Toast.makeText(Login.this, "Username does not exist", Toast.LENGTH_LONG).show();
                            progressBarLogin.setVisibility(View.GONE);
                        }
                        else {
                            for (DataSnapshot tableSnapshot : dataSnapshot.getChildren()) {
                                final Profile thisProfile = tableSnapshot.getValue(Profile.class);
                                if (thisProfile.getRole().equals(role)) {
                                    mAuth.signInWithEmailAndPassword(thisProfile.getEmail(),password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                ((DormVars)getApplication()).setActiveProfile(thisProfile);
                                                if (thisProfile.getRole().equals("Occupant")) {
                                                    startActivity(new Intent(Login.this, OccupantMain.class));
                                                }
                                                else {
                                                    startActivity(new Intent(Login.this, AdminMain.class));
                                                }
                                            }
                                            else {
                                                Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                progressBarLogin.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(Login.this, "Username and role does not match", Toast.LENGTH_LONG).show();
                                    progressBarLogin.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    };

    // METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void initializeVariables() {
        buttonSignIn = findViewById(R.id.loginButton);
        textViewRegLink = findViewById(R.id.loginNewUserLink);
        progressBarLogin = findViewById(R.id.loginProgressBar);
        editTextUsername = findViewById(R.id.loginUsername);
        editTextPassword = findViewById(R.id.loginPassword);
        spinnerRoles = findViewById(R.id.loginRoleSpinner);

        profilesTable = FirebaseDatabase.getInstance().getReference("Profiles");
        mAuth = FirebaseAuth.getInstance();
    }

    private boolean allFieldsValid() {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if (username.isEmpty()) {
            editTextUsername.setError("Required field");
            editTextUsername.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Required field");
            editTextPassword.requestFocus();
            return false;
        }

        return true;
    }
}
