package com.example.dormitorymanagementsystem;

import android.app.Application;

import com.example.dormitorymanagementsystem.classes.oop_classes.Profile;

public class DormVars extends Application {
    private Profile activeProfile = null;
    private Profile adminSelectedProfile = new Profile("TestRun","test@gmail.com","Occupant");

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
}
