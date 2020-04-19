package com.example.dormitorymanagementsystem.classes.oop_classes;

public class Room {
    private String roomNo, availability;
    private int capacity, currentOccupants;

    public Room() {

    };

    public Room(String roomNo, String availability, int capacity, int currentOccupants) {
        this.roomNo = roomNo;
        this.availability = availability;
        this.capacity = capacity;
        this.currentOccupants = currentOccupants;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentOccupants() {
        return currentOccupants;
    }

    public void setCurrentOccupants(int currentOccupants) {
        this.currentOccupants = currentOccupants;
    }
}
