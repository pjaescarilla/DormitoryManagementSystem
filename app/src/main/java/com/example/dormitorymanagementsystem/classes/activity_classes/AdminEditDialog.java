package com.example.dormitorymanagementsystem.classes.activity_classes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import com.example.dormitorymanagementsystem.DormVars;
import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.oop_classes.Bill;
import com.example.dormitorymanagementsystem.classes.oop_classes.ListRooms;
import com.example.dormitorymanagementsystem.classes.oop_classes.Profile;
import com.example.dormitorymanagementsystem.classes.oop_classes.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminEditDialog extends AppCompatDialogFragment {

    // View variables
    private View thisView;
    private EditText editTest;

    // Client variables
    private EditText editTextFname, editTextMname, editTextLname, editTextContactNo, editTextEname, editTextEno;
    private Spinner spinnerGender;
    private TextView textViewBirthday, textViewClientEdit, textViewClientSave;

    // Contract variables
    private EditText editTextContractLength, editTextMonthlyDue;
    private Spinner spinnerPaySched;
    private TextView textViewContractEdit, textViewContractSave;

    // Room assign variables
    //private EditText editTextRoomNo;
    private Spinner spinnerRole;
    private ListView listViewRoomList;
    private TextView textViewRoomAssignEdit, textViewRoomAssignSave, editTextRoomNo;

    // Billing variables
    private EditText editTextAmountDue;
    private TextView textViewDueDate, textViewBillingEdit, textViewBillingSave, textViewTransHistory;

    // Database variables
    private DatabaseReference profilesTable, billingsTable, roomsTable, transactionsTable;

    // Other variables
    private List<EditText> clientEditTexts = new ArrayList<>();
    private List<EditText> contractEditTexts = new ArrayList<>();
    private List<Room> roomList = new ArrayList<>();
    private Profile selectedProfile;
    private Room selectedRoom;
    private Bill thisProfilesBill;
    private TextView dateToChange, textViewDeleteUser;
    private EditText editTextDefault;
    private TransactionHistoryDialog reg = new TransactionHistoryDialog();
    private AdminEditDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        thisView = inflater.inflate(R.layout.dialog_client_edit,null);
        builder.setView(thisView)
                .setNegativeButton("Close",null);

        initializeVariables();
        populateFields();
        clientInfoFieldsEnabled(false);
        contractFieldsEnabled(false);
        roomAssignFieldsEnabled(false);
        billingFieldsEnabled(false);

        // BUTTON EVENTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        textViewClientEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientInfoFieldsEnabled(true);
            }
        });

        textViewClientSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientInfoFieldsEnabled(false);
                selectedProfile.setFirstName(editTextFname.getText().toString());
                selectedProfile.setMiddleName(editTextMname.getText().toString());
                selectedProfile.setLastName(editTextLname.getText().toString());
                selectedProfile.setContactNo(editTextContactNo.getText().toString());
                selectedProfile.seteContactName(editTextEname.getText().toString());
                selectedProfile.seteContactNo(editTextEno.getText().toString());
                selectedProfile.setBirthDate(textViewBirthday.getText().toString());
                selectedProfile.setGender(spinnerGender.getSelectedItem().toString());
                profilesTable.child(selectedProfile.getUsername()).setValue(selectedProfile);
            }
        });

        textViewContractSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contractFieldsEnabled(false);
                thisProfilesBill.setContractLengthMo(Integer.parseInt(editTextContractLength.getText().toString()));
                thisProfilesBill.setPaymentSched(spinnerPaySched.getSelectedItem().toString());
                thisProfilesBill.setMonthlyDue(Double.parseDouble(editTextMonthlyDue.getText().toString()));
                billingsTable.child(selectedProfile.getUsername()).setValue(thisProfilesBill);
            }
        });

        textViewContractEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contractFieldsEnabled(true);
            }
        });

        textViewBillingEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billingFieldsEnabled(true);
            }
        });

        textViewBillingSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billingFieldsEnabled(false);
                thisProfilesBill.setDueDate(textViewDueDate.getText().toString());
                if (!editTextAmountDue.getText().toString().equals(thisProfilesBill.getBalance())) {
                    double previousBalance = thisProfilesBill.getBalance();
                    double newBalance = Double.parseDouble(editTextAmountDue.getText().toString());
                    thisProfilesBill.setBalance(new BigDecimal(newBalance).setScale(2, RoundingMode.HALF_UP).doubleValue());

                    double change = new BigDecimal(thisProfilesBill.getBalance()-previousBalance).setScale(2,RoundingMode.HALF_UP).doubleValue();
                    Transaction newTransaction = new Transaction(thisProfilesBill.getBalance(),change);
                    newTransaction.setChangeOwner(((DormVars)getActivity().getApplication()).getActiveProfile().getUsername());
                    newTransaction.setTransDate(newTransaction.extractDateTimeNow());
                    String referenceNo = newTransaction.generateReferenceNumber();
                    transactionsTable.child(((DormVars)getActivity().getApplication()).getAdminSelectedProfile().getUsername()).child(referenceNo).setValue(newTransaction);
                }
                billingsTable.child(selectedProfile.getUsername()).setValue(thisProfilesBill);
            }
        });

        textViewTransHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DormVars)getActivity().getApplication()).setTransactionToSee(selectedProfile);
                reg.show(getChildFragmentManager(),"TransHistoryFromAdmin");
            }
        });


        textViewRoomAssignEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomAssignFieldsEnabled(true);
            }
        });

        textViewRoomAssignSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomAssignFieldsEnabled(false);
                saveRoomEdit();
            }
        });

        editTextRoomNo.setOnClickListener(showAllRooms);

        listViewRoomList.setOnItemClickListener(selectThisRoom);

        textViewDeleteUser.setOnClickListener(deleteSelectedUser);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AdminEditDialogListener) context;
    }


    // EVENTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    View.OnClickListener showDateDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int day,month,year;
            if (dateToChange.getText().toString().equals("Select Date")) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }
            else {
                String[] prevDate = dateToChange.getText().toString().split("/");
                year=Integer.parseInt(prevDate[2]);
                month=Integer.parseInt(prevDate[0])-1;
                day=Integer.parseInt(prevDate[1]);
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog_MinWidth,getSelectedBirthDate,year,month,day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }
    };

    DatePickerDialog.OnDateSetListener getSelectedBirthDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month+=1;
            String date = month+"/"+dayOfMonth+"/"+year;
            dateToChange.setText(date);
        }
    };

    AdapterView.OnItemClickListener selectThisRoom = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedRoom=roomList.get(position);
            if (selectedRoom.getAvailability().equals("AVAILABLE")) {
                //editTextRoomNo.clearFocus();
                editTextRoomNo.setText(selectedRoom.getRoomNo());
                //editTextRoomNo.clearFocus();
                listViewRoomList.setVisibility(View.GONE);
            }
            else {
                Toast.makeText(getContext(), "ROOM UNAVAILABLE!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    TextWatcher displayRooms = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!editTextRoomNo.hasFocus()) {
                listViewRoomList.setVisibility(View.GONE);
            }
            else {
                String keyword = editTextRoomNo.getText().toString();
                roomsTable.orderByChild("roomNo").startAt(keyword).endAt(keyword+"\uf8ff").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        roomList.clear();
                        for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                            roomList.add(roomSnapshot.getValue(Room.class));
                        }
                        ListRooms theseRooms = new ListRooms(getActivity(),roomList);
                        listViewRoomList.setAdapter(theseRooms);
                        listViewRoomList.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }
    };

    View.OnClickListener showAllRooms = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listViewRoomList.setVisibility(View.VISIBLE);
            roomsTable.orderByChild("roomNo").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    roomList.clear();
                    for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                        roomList.add(roomSnapshot.getValue(Room.class));
                    }
                    ListRooms theseRooms = new ListRooms(getActivity(),roomList);
                    listViewRoomList.setAdapter(theseRooms);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    };

    View.OnClickListener deleteSelectedUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (((DormVars)getActivity().getApplication()).getAdminSelectedProfile().getRoom()!=null && !((DormVars)getActivity().getApplication()).getAdminSelectedProfile().getRoom().equals("Unassigned")) {
                roomsTable.child(((DormVars)getActivity().getApplication()).getAdminSelectedProfile().getRoom()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Room thisRoom = dataSnapshot.getValue(Room.class);
                        int previousOccupant = thisRoom.getCurrentOccupants();
                        thisRoom.setCurrentOccupants(previousOccupant-1);
                        if (thisRoom.getCapacity()-thisRoom.getCurrentOccupants() < 1) {
                            thisRoom.setAvailability("NOT AVAILABLE");
                        }
                        roomsTable.child(thisRoom.getRoomNo()).setValue(thisRoom);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            profilesTable.child(((DormVars)getActivity().getApplication()).getAdminSelectedProfile().getUsername()).removeValue();
            billingsTable.child(((DormVars)getActivity().getApplication()).getAdminSelectedProfile().getUsername()).removeValue();
            Toast.makeText(getContext(), "User removed", Toast.LENGTH_SHORT).show();
            listener.closeAdminEditDialog();
        }
    };


    // METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void initializeVariables() {
        // Client Variables
        editTextFname = thisView.findViewById(R.id.adminEditFname);
        editTextMname = thisView.findViewById(R.id.adminEditMname);
        editTextLname = thisView.findViewById(R.id.adminEditLname);
        editTextContactNo = thisView.findViewById(R.id.adminEditContactNo);
        editTextEname = thisView.findViewById(R.id.adminEditEname);
        editTextEno = thisView.findViewById(R.id.adminEditEcon);
        spinnerGender = thisView.findViewById(R.id.adminEditGenderSpinner);
        textViewBirthday = thisView.findViewById(R.id.adminEditBday);
        textViewClientEdit = thisView.findViewById(R.id.adminEditButtonClient);
        textViewClientSave = thisView.findViewById(R.id.adminEditSaveClient);
        clientEditTexts.add(editTextFname);
        clientEditTexts.add(editTextMname);
        clientEditTexts.add(editTextLname);
        clientEditTexts.add(editTextContactNo);
        clientEditTexts.add(editTextEname);
        clientEditTexts.add(editTextEno);

        // Contract Variables
        editTextContractLength = thisView.findViewById(R.id.adminEditContractLen);
        editTextMonthlyDue = thisView.findViewById(R.id.adminEditMDue);
        contractEditTexts.add(editTextContractLength);
        contractEditTexts.add(editTextMonthlyDue);
        textViewContractEdit = thisView.findViewById(R.id.adminEditContractButton);
        textViewContractSave = thisView.findViewById(R.id.adminEditContractSave);
        spinnerPaySched = thisView.findViewById(R.id.adminEditContractDdate);
        Integer[] monthDays = new Integer[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
        ArrayAdapter<Integer> dDateAdapter = new ArrayAdapter<Integer>(getContext(),android.R.layout.simple_spinner_item, monthDays);
        spinnerPaySched.setAdapter(dDateAdapter);

        // Room assign variables
        //editTextRoomNo = thisView.findViewById(R.id.adminEditRoom);
        editTextRoomNo = thisView.findViewById(R.id.adminEditRoom2);
        spinnerRole = thisView.findViewById(R.id.adminEditRole);
        listViewRoomList = thisView.findViewById(R.id.adminEditRoomList);
        textViewRoomAssignEdit = thisView.findViewById(R.id.adminEditAssignment);
        textViewRoomAssignSave = thisView.findViewById(R.id.adminEditSaveAssignment);

        // Billing variables
        editTextAmountDue = thisView.findViewById(R.id.adminEditAmountDue);
        textViewDueDate = thisView.findViewById(R.id.adminEditDueDate);
        textViewBillingEdit = thisView.findViewById(R.id.adminEditButtonBilling);
        textViewBillingSave = thisView.findViewById(R.id.adminEditBillingSave);
        textViewTransHistory = thisView.findViewById(R.id.adminEditTransactionHistory);

        // General variables
        profilesTable = FirebaseDatabase.getInstance().getReference("Profiles");
        billingsTable = FirebaseDatabase.getInstance().getReference("BillingInfo");
        roomsTable = FirebaseDatabase.getInstance().getReference("Rooms");
        transactionsTable = FirebaseDatabase.getInstance().getReference("Transactions");
        selectedProfile = ((DormVars)getActivity().getApplication()).getAdminSelectedProfile();
        editTextDefault = thisView.findViewById(R.id.defaultEditText);
        spinnerPaySched.setBackground(editTextDefault.getBackground());
        textViewDeleteUser = thisView.findViewById(R.id.adminEditeDeleteUser);
    }

    private void clientInfoFieldsEnabled(boolean toEdit) {
        if (toEdit) {
            for (EditText thisField : clientEditTexts) {
                thisField.setEnabled(toEdit);
                thisField.setBackground(editTextDefault.getBackground());
                thisField.setTextColor(editTextDefault.getTextColors());
            }
            dateToChange = textViewBirthday;
            textViewBirthday.setOnClickListener(showDateDialog);
            textViewBirthday.setTextColor(Color.BLACK);
            textViewClientSave.setVisibility(View.VISIBLE);
        }
        else {
            for (EditText thisField : clientEditTexts) {
                thisField.setEnabled(toEdit);
                thisField.setBackgroundResource(R.drawable.edittext_disabled);
                thisField.setTextColor(ContextCompat.getColor(getContext(),R.color.inactive_text));
            }
            textViewBirthday.setOnClickListener(null);
            textViewBirthday.setTextColor(ContextCompat.getColor(getContext(),R.color.inactive_text));
            textViewClientSave.setVisibility(View.GONE);
        }
        spinnerGender.setEnabled(toEdit);

    }

    private void contractFieldsEnabled(boolean toEdit) {
        if (toEdit) {
            for (EditText thisField : contractEditTexts) {
                thisField.setEnabled(toEdit);
                thisField.setBackground(editTextDefault.getBackground());
                thisField.setTextColor(editTextDefault.getTextColors());
            }
            spinnerPaySched.setBackground(editTextDefault.getBackground());
            textViewContractSave.setVisibility(View.VISIBLE);
        }
        else {
            for (EditText thisField : contractEditTexts) {
                thisField.setEnabled(toEdit);
                thisField.setBackgroundResource(R.drawable.edittext_disabled);
                thisField.setTextColor(ContextCompat.getColor(getContext(),R.color.inactive_text));
            }
            spinnerPaySched.setBackgroundResource(R.drawable.edittext_disabled);
            textViewContractSave.setVisibility(View.GONE);
        }
        spinnerPaySched.setEnabled(toEdit);

    }

    private void roomAssignFieldsEnabled(boolean toEdit) {
        if (toEdit) {
            editTextRoomNo.setEnabled(toEdit);
            editTextRoomNo.setBackgroundResource(android.R.drawable.edit_text);
            editTextRoomNo.setTextColor(editTextDefault.getTextColors());
            textViewRoomAssignSave.setVisibility(View.VISIBLE);
        }
        else {
            editTextRoomNo.setEnabled(toEdit);
            editTextRoomNo.setBackgroundResource(R.drawable.edittext_disabled);
            editTextRoomNo.setTextColor(ContextCompat.getColor(getContext(),R.color.inactive_text));
            textViewRoomAssignSave.setVisibility(View.GONE);
        }
        spinnerRole.setEnabled(toEdit);
    }

    private void billingFieldsEnabled(boolean toEdit) {
        if (toEdit) {
            editTextAmountDue.setEnabled(toEdit);
            editTextAmountDue.setBackground(editTextDefault.getBackground());
            editTextAmountDue.setTextColor(editTextDefault.getTextColors());

            dateToChange = textViewDueDate;
            textViewDueDate.setOnClickListener(showDateDialog);
            textViewDueDate.setTextColor(Color.BLACK);
            textViewBillingSave.setVisibility(View.VISIBLE);
        }
        else {
            editTextAmountDue.setEnabled(toEdit);
            editTextAmountDue.setBackgroundResource(R.drawable.edittext_disabled);
            editTextAmountDue.setTextColor(ContextCompat.getColor(getContext(),R.color.inactive_text));

            textViewDueDate.setOnClickListener(null);
            textViewDueDate.setTextColor(ContextCompat.getColor(getContext(),R.color.inactive_text));
            textViewBillingSave.setVisibility(View.GONE);
        }
    }

    private void populateFields () {
        // Client Fields
        if (selectedProfile.getFirstName() == null) {
            for (EditText thisField : clientEditTexts) {
                thisField.setText("N/A");
            }
        }
        else {
            editTextFname.setText(selectedProfile.getFirstName());
            editTextMname.setText(selectedProfile.getMiddleName());
            editTextLname.setText(selectedProfile.getLastName());
            editTextContactNo.setText(selectedProfile.getContactNo());
            editTextEname.setText(selectedProfile.geteContactName());
            editTextEno.setText(selectedProfile.geteContactNo());
            textViewBirthday.setText(selectedProfile.getBirthDate());
            if (selectedProfile.getGender().equals("Male")) {
                spinnerGender.setSelection(1);
            } else {
                spinnerGender.setSelection(2);
            }
        }

        // Contract & Billing Fields
        billingsTable.child(selectedProfile.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()==0) {
                    thisProfilesBill = new Bill();
                }
                else {
                    // Contract
                    thisProfilesBill=dataSnapshot.getValue(Bill.class);
                    spinnerPaySched.setSelection(Integer.parseInt(thisProfilesBill.getPaymentSched())-1);
                    editTextMonthlyDue.setText(Double.toString(thisProfilesBill.getMonthlyDue()));
                    editTextContractLength.setText(Integer.toString(thisProfilesBill.getContractLengthMo()));

                    // Billing
                    editTextAmountDue.setText(Double.toString(thisProfilesBill.getBalance()));
                    if (thisProfilesBill.getDueDate()==null || thisProfilesBill.getDueDate().isEmpty()) {
                        textViewDueDate.setText("Select Date");
                    }
                    else {
                        textViewDueDate.setText(thisProfilesBill.getDueDate());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Room Field
        if (selectedProfile.getRoom() == null) {
            editTextRoomNo.setText("Unassigned");
        }
        else {
            editTextRoomNo.setText(selectedProfile.getRoom());
        }
        if (selectedProfile.getRole().equals("Occupant")) {
            spinnerRole.setSelection(0);
        }
        else {
            spinnerRole.setSelection(1);
        }
    }

    private void saveRoomEdit () {
        if (selectedProfile.getRoom() != null && !selectedProfile.getRoom().equals("Unassigned") && !editTextRoomNo.getText().toString().equals(selectedProfile.getRoom())) {     // if occupant is already assigned to a room previously, change # of occupants in previous room
            String oldRoomNo = selectedProfile.getRoom();
            roomsTable.child(oldRoomNo).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Room oldRoom = dataSnapshot.getValue(Room.class);
                    int prevOccupantCount = oldRoom.getCurrentOccupants();
                    oldRoom.setCurrentOccupants(prevOccupantCount-1);
                    if (oldRoom.getCapacity()-oldRoom.getCurrentOccupants() > 0) {
                        oldRoom.setAvailability("AVAILABLE");
                    }
                    roomsTable.child(oldRoom.getRoomNo()).setValue(oldRoom);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            String newRoomNo = selectedRoom.getRoomNo();
            roomsTable.child(newRoomNo).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Room newRoom = dataSnapshot.getValue(Room.class);
                    int prevOccupantCount = newRoom.getCurrentOccupants();
                    newRoom.setCurrentOccupants(prevOccupantCount+1);
                    if (newRoom.getCapacity()-newRoom.getCurrentOccupants()< 1) {
                        newRoom.setAvailability("NOT AVAILABLE");
                    }
                    roomsTable.child(newRoom.getRoomNo()).setValue(newRoom);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if (selectedProfile.getRoom() == null) {
            if (!editTextRoomNo.getText().toString().equals("Unassigned") && selectedRoom!=null) {
                String newRoomNo = selectedRoom.getRoomNo();
                roomsTable.child(newRoomNo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Room newRoom = dataSnapshot.getValue(Room.class);
                        int prevOccupantCount = newRoom.getCurrentOccupants();
                        newRoom.setCurrentOccupants(prevOccupantCount + 1);
                        if (newRoom.getCapacity() - newRoom.getCurrentOccupants() < 1) {
                            newRoom.setAvailability("NOT AVAILABLE");
                        }
                        roomsTable.child(newRoom.getRoomNo()).setValue(newRoom);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        else if (selectedProfile.getRoom().equals("Unassigned")) {
            if (!editTextRoomNo.getText().toString().equals("Unassigned") && selectedRoom!=null) {
                String newRoomNo = selectedRoom.getRoomNo();
                roomsTable.child(newRoomNo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Room newRoom = dataSnapshot.getValue(Room.class);
                        int prevOccupantCount = newRoom.getCurrentOccupants();
                        newRoom.setCurrentOccupants(prevOccupantCount + 1);
                        if (newRoom.getCapacity() - newRoom.getCurrentOccupants() < 1) {
                            newRoom.setAvailability("NOT AVAILABLE");
                        }
                        roomsTable.child(newRoom.getRoomNo()).setValue(newRoom);
                        Log.d("Testing","After editing: " + Boolean.toString(editTextRoomNo.hasFocus()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        selectedProfile.setRoom(editTextRoomNo.getText().toString());
        selectedProfile.setRole(spinnerRole.getSelectedItem().toString());
        profilesTable.child(selectedProfile.getUsername()).setValue(selectedProfile);
    }



    // LISTENER INTERFACE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public interface AdminEditDialogListener {
        void closeAdminEditDialog();
    }
}
