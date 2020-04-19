package com.example.dormitorymanagementsystem.classes.activity_classes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dormitorymanagementsystem.DormVars;
import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.oop_classes.Bill;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BillingFragment extends Fragment {

    // View Variables
    private View thisView;
    private Button buttonPay;
    private TextView textViewPaymentSched, textViewMonthlyDue, textViewContractLength, textViewAmtDue, textViewDueDate;
    private EditText editTextPayAmount, editTextRemarks;

    // Database Variables
    private DatabaseReference billingInfoTable, transactionsTable;

    // Other Variables
    private Bill thisProfilesBill;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_billing,container,false);

        initializeVariables();
        populateFields();

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordTransaction();
            }
        });

        return thisView;
    }

    private void initializeVariables()
    {
        buttonPay = thisView.findViewById(R.id.accountPayButton);
        textViewAmtDue = thisView.findViewById(R.id.accountAmtDue);
        textViewContractLength = thisView.findViewById(R.id.accountContractLength);
        textViewDueDate = thisView.findViewById(R.id.accountDueDate);
        textViewMonthlyDue = thisView.findViewById(R.id.accountMonthlyDue);
        textViewPaymentSched = thisView.findViewById(R.id.accountPdate);
        editTextPayAmount = thisView.findViewById(R.id.accountPayAmount);
        editTextRemarks = thisView.findViewById(R.id.accountRemarks);

        billingInfoTable = FirebaseDatabase.getInstance().getReference("BillingInfo");
        transactionsTable = FirebaseDatabase.getInstance().getReference("Transactions");
    }

    private void fillUpDummyData() {
        buttonPay=thisView.findViewById(R.id.accountPayButton);
        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bill newBillingFo = new Bill (36,12000.98,23000,"3/2/2020","every 4th");
                FirebaseDatabase.getInstance().getReference("BillingInfo").child(((DormVars)getActivity().getApplication()).getActiveProfile().getUsername()).setValue(newBillingFo);
            }
        });
    }

    private void populateFields()
    {
        billingInfoTable.child(((DormVars)getActivity().getApplication()).getActiveProfile().getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thisProfilesBill = dataSnapshot.getValue(Bill.class);
                textViewAmtDue.setText("PHP "+Double.toString(thisProfilesBill.getBalance()));
                textViewContractLength.setText(Integer.toString(thisProfilesBill.getContractLengthMo())+" months");
                textViewDueDate.setText(thisProfilesBill.getDueDate());
                textViewMonthlyDue.setText("PHP "+Double.toString(thisProfilesBill.getMonthlyDue()));
                textViewPaymentSched.setText("every "+thisProfilesBill.getPaymentSched()+" of the month");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean paymentFieldsValidated() {
        if (editTextPayAmount.getText().toString().isEmpty()) {
            editTextPayAmount.setError("Required field");
            editTextPayAmount.requestFocus();
            return false;
        }

        if (editTextRemarks.getText().toString().isEmpty()) {
            editTextRemarks.setError("Required field");
            editTextRemarks.requestFocus();
            return false;
        }

        return true;
    }

    private void recordTransaction() {
        if (paymentFieldsValidated()) {
            double previousBalance = thisProfilesBill.getBalance();
            double change = new BigDecimal(Double.parseDouble(editTextPayAmount.getText().toString())).setScale(2, RoundingMode.HALF_UP).doubleValue() * -1;
            double newBalance = new BigDecimal(previousBalance + change).setScale(2, RoundingMode.HALF_UP).doubleValue();
            Transaction newTransaction = new Transaction(newBalance, change);
            newTransaction.setChangeOwner(((DormVars) getActivity().getApplication()).getActiveProfile().getUsername());
            newTransaction.setTransDate(newTransaction.extractDateTimeNow());
            String referenceNo = newTransaction.generateReferenceNumber();
            transactionsTable.child(((DormVars) getActivity().getApplication()).getActiveProfile().getUsername()).child(referenceNo).setValue(newTransaction);

            thisProfilesBill.setBalance(newBalance);
            billingInfoTable.child(((DormVars)getActivity().getApplication()).getActiveProfile().getUsername()).setValue(thisProfilesBill);
        }
    }
}
