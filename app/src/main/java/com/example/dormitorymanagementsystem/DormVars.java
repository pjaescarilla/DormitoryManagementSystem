package com.example.dormitorymanagementsystem;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.dormitorymanagementsystem.classes.activity_classes.Transaction;
import com.example.dormitorymanagementsystem.classes.oop_classes.Bill;
import com.example.dormitorymanagementsystem.classes.oop_classes.Profile;
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
import java.util.ArrayList;
import java.util.List;

public class DormVars extends Application {
    private Profile activeProfile = null;
    private Profile adminSelectedProfile = new Profile("TestRun","test@gmail.com","Occupant");
    private Bill billToUpdate = null;
    private Transaction transactionToAdd = null;

    public Profile getAdminSelectedProfile() {
        return adminSelectedProfile;
    }

    public void setAdminSelectedProfile(Profile adminSelectedProfile) {
        this.adminSelectedProfile = adminSelectedProfile;
    }

    public Profile getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(Profile activeProfile) {
        this.activeProfile = activeProfile;
    }

    public Bill getBillToUpdate() {
        return billToUpdate;
    }

    public void setBillToUpdate(Bill billToUpdate) {
        this.billToUpdate = billToUpdate;
    }

    public Transaction getTransactionToAdd() {
        return transactionToAdd;
    }

    public void setTransactionToAdd(Transaction transactionToAdd) {
        this.transactionToAdd = transactionToAdd;
    }
}
