package com.example.dormitorymanagementsystem.classes.activity_classes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.dormitorymanagementsystem.DormVars;
import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.oop_classes.Bill;
import com.example.dormitorymanagementsystem.classes.oop_classes.Profile;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dormitorymanagementsystem.ui.occupant_main.SectionsPagerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class OccupantMain extends AppCompatActivity implements UserDetailsEntryDialog.UserEntryDialogListener,BillingFragment.paymayaCustomListener, PayMayaCheckoutCallback {

    private UserDetailsEntryDialog reg = new UserDetailsEntryDialog();
    private SectionsPagerAdapter sectionsPagerAdapter;

    private static final String PUBLIC_FACING_API_KEY = "pk-eo4sL393CWU5KmveJUaW8V730TTei2zY8zE4dHJDxkF";
    private PayMayaCheckout mPayMayaCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occupant_main);

        setupUI();
        checkForUnsetProfile();
    }

    private void setupUI() {
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        PayMayaConfig.setEnvironment(PayMayaConfig.ENVIRONMENT_SANDBOX);
        mPayMayaCheckout = new PayMayaCheckout(PUBLIC_FACING_API_KEY,this);
    }

    private void checkForUnsetProfile() {
        if (((DormVars)getApplication()).getActiveProfile().getFirstName() == null){
            reg.show(getSupportFragmentManager(),"UserDetailsEntry");
        }
    }

    @Override
    public void closeDialog() {
        reg.dismiss();
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }




    // PAYMAYA PROCESSING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void executePaymayaCheckout(Profile thisProfile, String payAmount, String remarks, String requestReference) {
        // SHIPPING DETAILS
        Contact contact = new Contact("09051233249", thisProfile.getEmail());
        Address address = new Address("line 1", "line 2", "city", "state", "zip code", "PH");
        Buyer buyer = new Buyer(thisProfile.getFirstName(),thisProfile.getMiddleName(),thisProfile.getLastName());
        buyer.setContact(contact);
        buyer.setBillingAddress(address);
        buyer.setShippingAddress(address);

        // COST DETAILS
        BigDecimal amountToPay = new BigDecimal(Double.parseDouble(payAmount)).setScale(2, RoundingMode.HALF_UP);
        TotalAmount totalAmount = new TotalAmount(amountToPay,"PHP");
        List paymentDetailsList = new ArrayList<>();
        Item paymentDetails = new Item(remarks,1,totalAmount);
        paymentDetailsList.add(paymentDetails);

        // Thank you pages
        String successURL = "http://yourshop.com/success";
        String failedURL = "http://yourshop.com/failed";
        String canceledURL = "http://yourshop.com/canceled";
        RedirectUrl redirectUrl = new RedirectUrl(successURL, failedURL, canceledURL);

        // Execution of payment
        //requestReference = new Transaction().generateReferenceNumber();
        Checkout checkout = new Checkout(totalAmount,buyer,paymentDetailsList,requestReference,redirectUrl);
        mPayMayaCheckout.execute(this,checkout);
    }

    @Override
    public void onCheckoutSuccess() {
        Log.d("TESTING","SUCCESS");
        Toast.makeText(this, "PAYMENT SUCCESSFUL", Toast.LENGTH_LONG).show();
        applyBillUpdates();
    }

    @Override
    public void onCheckoutCanceled() {
        Log.d("TESTING","cancel");
        Toast.makeText(this, "PAYMENT CANCELLED", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckoutFailure(String message) {
        Log.d("TESTING","fail");
        Toast.makeText(this, "PAYMENT FAILED", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPayMayaCheckout.onActivityResult(requestCode,resultCode,data);
    }

    private void applyBillUpdates() {
        String username = ((DormVars)getApplication()).getActiveProfile().getUsername();
        Bill billToUpdate = ((DormVars)getApplication()).getBillToUpdate();
        Transaction transactionToAdd = ((DormVars)getApplication()).getTransactionToAdd();

        FirebaseDatabase.getInstance().getReference("BillingInfo").child(username).setValue(billToUpdate);
        FirebaseDatabase.getInstance().getReference("Transactions").child(username).child(transactionToAdd.getReferenceNumber()).setValue(transactionToAdd);
    }
}