package com.example.dormitorymanagementsystem.classes.activity_classes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Transaction {
    private String referenceNumber, transDate, changeOwner;
    private double balance, change;

    public Transaction () {
    }

    public Transaction(String referenceNumber, String transDate, String changeOwner, double balance, double change) {
        this.referenceNumber = referenceNumber;
        this.transDate = transDate;
        this.changeOwner = changeOwner;
        this.balance = balance;
        this.change = change;
    }

    public Transaction(double balance, double change) {
        this.balance = balance;
        this.change = change;
    }

    public String getChangeOwner() {
        return changeOwner;
    }

    public void setChangeOwner(String changeOwner) {
        this.changeOwner = changeOwner;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public String extractDateTimeNow() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date date = new Date();
        String dateToday = dateFormat.format(date);
        return dateToday;
    }

    public String generateReferenceNumber() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date date = new Date();
        String referenceNumber = dateFormat.format(date);
        setReferenceNumber(referenceNumber);
        return referenceNumber;
    }
}
