package com.example.dormitorymanagementsystem.classes.oop_classes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.activity_classes.Transaction;

import java.util.List;

public class ListTransactions extends ArrayAdapter<Transaction> {
    private Activity context;
    private List<Transaction> transactionList;

    public ListTransactions(Activity context, List<Transaction> transactionList) {
        super(context, R.layout.list_transactions, transactionList);
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewTrans = inflater.inflate(R.layout.list_transactions,null,true);

        TextView textViewRefNum = listViewTrans.findViewById(R.id.transRefNum);
        TextView textViewInitiator = listViewTrans.findViewById(R.id.transInitiator);
        TextView textViewDate = listViewTrans.findViewById(R.id.transDate);
        TextView textViewChange = listViewTrans.findViewById(R.id.transChange);
        TextView textViewBal = listViewTrans.findViewById(R.id.transBal);

        Transaction thisTransaction = transactionList.get(position);

        textViewRefNum.setText(thisTransaction.getReferenceNumber());
        textViewInitiator.setText(thisTransaction.getChangeOwner());
        textViewDate.setText(thisTransaction.getTransDate());
        textViewChange.setText(Double.toString(thisTransaction.getChange()));
        textViewBal.setText(Double.toString(thisTransaction.getBalance()));

        return listViewTrans;
    }
}
