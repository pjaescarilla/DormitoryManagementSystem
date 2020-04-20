package com.example.dormitorymanagementsystem.classes.activity_classes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dormitorymanagementsystem.DormVars;
import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.oop_classes.Bill;
import com.example.dormitorymanagementsystem.classes.oop_classes.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paymaya.sdk.android.PayMayaConfig;
import com.paymaya.sdk.android.checkout.PayMayaCheckout;
import com.paymaya.sdk.android.checkout.PayMayaCheckoutCallback;
import com.paymaya.sdk.android.checkout.models.Address;
import com.paymaya.sdk.android.checkout.models.Buyer;
import com.paymaya.sdk.android.checkout.models.Checkout;
import com.paymaya.sdk.android.checkout.models.Contact;
import com.paymaya.sdk.android.checkout.models.Item;
import com.paymaya.sdk.android.checkout.models.RedirectUrl;
import com.paymaya.sdk.android.checkout.models.TotalAmount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class BillingFragment extends Fragment implements PayMayaCheckoutCallback {

    // View Variables
    private View thisView;
    private Button buttonPay;
    private TextView textViewPaymentSched, textViewMonthlyDue, textViewContractLength, textViewAmtDue, textViewDueDate;
    private EditText editTextPayAmount, editTextRemarks;

    // Database Variables
    private DatabaseReference billingInfoTable, transactionsTable;

    // Paymaya Variables
    private static final String PUBLIC_FACING_API_KEY = "pk-eo4sL393CWU5KmveJUaW8V730TTei2zY8zE4dHJDxkF";
    private PayMayaCheckout mPayMayaCheckout;
    private String requestReference;

    // Other Variables
    private Bill thisProfilesBill;
    private Profile thisProfile;
    private paymayaCustomListener listener;


    // OVERRIDE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_billing,container,false);
        PayMayaConfig.setEnvironment(PayMayaConfig.ENVIRONMENT_SANDBOX);
        mPayMayaCheckout = new PayMayaCheckout(PUBLIC_FACING_API_KEY,this);

        initializeVariables();
        populateFields();

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paymentFieldsValidated()) {
                    updateBalanceValues();
                    //executePaymayaCheckout();
                    listener.executePaymayaCheckout(((DormVars)getActivity().getApplication()).getActiveProfile(),editTextPayAmount.getText().toString(),editTextRemarks.getText().toString(),requestReference);
                }
            }
        });

        //testPaymaya();

        return thisView;
    }

    @Override
    public void onCheckoutSuccess() {
        Toast.makeText(getContext(), "Payment Successful", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckoutCanceled() {
        Toast.makeText(getContext(), "Payment Cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckoutFailure(String message) {
        Toast.makeText(getContext(), "Payment Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (paymayaCustomListener) context;
    }

    // METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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
        thisProfile=((DormVars)getActivity().getApplication()).getActiveProfile();
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
            double previousBalance = thisProfilesBill.getBalance();
            double change = new BigDecimal(Double.parseDouble(editTextPayAmount.getText().toString())).setScale(2, RoundingMode.HALF_UP).doubleValue() * -1;
            double newBalance = new BigDecimal(previousBalance + change).setScale(2, RoundingMode.HALF_UP).doubleValue();
            Transaction newTransaction = new Transaction(newBalance, change);
            newTransaction.setChangeOwner(((DormVars) getActivity().getApplication()).getActiveProfile().getUsername());
            newTransaction.setTransDate(newTransaction.extractDateTimeNow());
            //String referenceNo = newTransaction.generateReferenceNumber();
            transactionsTable.child(((DormVars) getActivity().getApplication()).getActiveProfile().getUsername()).child(requestReference).setValue(newTransaction);

            thisProfilesBill.setBalance(newBalance);
            billingInfoTable.child(((DormVars)getActivity().getApplication()).getActiveProfile().getUsername()).setValue(thisProfilesBill);
    }

    private void updateBalanceValues() {
        double previousBalance = thisProfilesBill.getBalance();
        double change = new BigDecimal(Double.parseDouble(editTextPayAmount.getText().toString())).setScale(2, RoundingMode.HALF_UP).doubleValue() * -1;
        double newBalance = new BigDecimal(previousBalance + change).setScale(2, RoundingMode.HALF_UP).doubleValue();
        Transaction newTransaction = new Transaction(newBalance, change);
        newTransaction.setChangeOwner(((DormVars) getActivity().getApplication()).getActiveProfile().getUsername());
        newTransaction.setTransDate(newTransaction.extractDateTimeNow());
        requestReference = newTransaction.generateReferenceNumber();
        //transactionsTable.child(((DormVars) getActivity().getApplication()).getActiveProfile().getUsername()).child(requestReference).setValue(newTransaction);
        ((DormVars)getActivity().getApplication()).setTransactionToAdd(newTransaction);

        thisProfilesBill.setBalance(newBalance);
        //billingInfoTable.child(((DormVars)getActivity().getApplication()).getActiveProfile().getUsername()).setValue(thisProfilesBill);
        ((DormVars)getActivity().getApplication()).setBillToUpdate(thisProfilesBill);
    }

    private void executePaymayaCheckout() {
        // SHIPPING DETAILS
        Contact contact = new Contact("09051233249", thisProfile.getEmail());
        Address address = new Address("line 1", "line 2", "city", "state", "zip code", "PH");
        Buyer buyer = new Buyer(thisProfile.getFirstName(),thisProfile.getMiddleName(),thisProfile.getLastName());
        buyer.setContact(contact);
        buyer.setBillingAddress(address);
        buyer.setShippingAddress(address);

        // COST DETAILS
        BigDecimal amountToPay = new BigDecimal(Double.parseDouble(editTextPayAmount.getText().toString())).setScale(2,RoundingMode.HALF_UP);
        TotalAmount totalAmount = new TotalAmount(amountToPay,"PHP");
        List paymentDetailsList = new ArrayList<>();
        Item paymentDetails = new Item(editTextRemarks.getText().toString(),1,totalAmount);
        paymentDetailsList.add(paymentDetails);

        // Thank you pages
        String successURL = "http://yourshop.com/success";
        String failedURL = "http://yourshop.com/failed";
        String canceledURL = "http://yourshop.com/canceled";
        RedirectUrl redirectUrl = new RedirectUrl(successURL, failedURL, canceledURL);

        // Execution of payment
        requestReference = new Transaction().generateReferenceNumber();
        Checkout checkout = new Checkout(totalAmount,buyer,paymentDetailsList,requestReference,redirectUrl);
        mPayMayaCheckout.execute(getActivity(),checkout);
    }


    // PAYMAYA CUSTOM CALLBACK BY PEJEY ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public interface paymayaCustomListener {
        void executePaymayaCheckout(Profile thisProfile, String payAmount, String remarks, String requestReference);
    }
}
