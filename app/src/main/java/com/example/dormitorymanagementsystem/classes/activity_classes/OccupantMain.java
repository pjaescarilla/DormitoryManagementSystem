package com.example.dormitorymanagementsystem.classes.activity_classes;

import android.os.Bundle;

import com.example.dormitorymanagementsystem.DormVars;
import com.example.dormitorymanagementsystem.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dormitorymanagementsystem.ui.occupant_main.SectionsPagerAdapter;

public class OccupantMain extends AppCompatActivity implements UserDetailsEntryDialog.UserEntryDialogListener {

    private UserDetailsEntryDialog reg = new UserDetailsEntryDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occupant_main);

        setupUI();
        checkForUnsetProfile();
    }

    private void setupUI() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
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
}