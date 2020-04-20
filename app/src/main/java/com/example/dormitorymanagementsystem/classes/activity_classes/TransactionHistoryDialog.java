package com.example.dormitorymanagementsystem.classes.activity_classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.dormitorymanagementsystem.DormVars;
import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.oop_classes.ListTransactions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryDialog extends AppCompatDialogFragment {

    private ListView listViewTransactionHistory;
    private View thisView;
    private DatabaseReference transactionsTable;
    private List<Transaction> transactionList = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        thisView = inflater.inflate(R.layout.dialog_transaction_history,null);
        builder.setView(thisView)
                .setTitle("TransactionHistory")
                .setNegativeButton("Close",null);


        listViewTransactionHistory = thisView.findViewById(R.id.listViewTransactions);
        Log.d("TESTING",((DormVars)getActivity().getApplication()).getTransactionToSee().getUsername());
        transactionsTable = FirebaseDatabase.getInstance().getReference("Transactions").child(((DormVars)getActivity().getApplication()).getTransactionToSee().getUsername());
        transactionsTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionList.clear();
                Log.d("TESTING",Long.toString(dataSnapshot.getChildrenCount()));
                for (DataSnapshot tranSnap : dataSnapshot.getChildren()) {
                    Transaction thisTransaction = tranSnap.getValue(Transaction.class);
                    transactionList.add(thisTransaction);
                }

                ListTransactions allTransactions = new ListTransactions(getActivity(),transactionList);
                listViewTransactionHistory.setAdapter(allTransactions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return builder.create();
    }
}
