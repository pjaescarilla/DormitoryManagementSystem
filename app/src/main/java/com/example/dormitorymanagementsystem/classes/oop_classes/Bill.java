package com.example.dormitorymanagementsystem.classes.oop_classes;

public class Bill {
    private int contractLengthMo;
    private double monthlyDue, balance;
    private String dueDate, paymentSched;

    public Bill () {
    }

    public Bill(int contractLength, double monthlyDue, double balance, String dueDate, String paymentSched) {
        this.contractLengthMo = contractLength;
        this.monthlyDue = monthlyDue;
        this.balance = balance;
        this.dueDate = dueDate;
        this.paymentSched = paymentSched;
    }

    public int getContractLengthMo() {
        return contractLengthMo;
    }

    public void setContractLengthMo(int contractLengthMo) {
        this.contractLengthMo = contractLengthMo;
    }

    public double getMonthlyDue() {
        return monthlyDue;
    }

    public void setMonthlyDue(double monthlyDue) {
        this.monthlyDue = monthlyDue;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPaymentSched() {
        return paymentSched;
    }

    public void setPaymentSched(String paymentSched) {
        this.paymentSched = paymentSched;
    }
}
