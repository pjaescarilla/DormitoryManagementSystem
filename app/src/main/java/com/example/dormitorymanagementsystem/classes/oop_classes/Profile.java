package com.example.dormitorymanagementsystem.classes.oop_classes;

public class Profile {

    private String username, firstName, middleName, lastName, gender, birthDate, contactNo, eContactName, eContactNo, email, role, room;

    public Profile() {
    }

    public Profile(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public Profile(String username, String firstName, String middleName, String lastName, String gender, String birthDate, String contactNo, String eContactName, String eContactNo, String email, String role, String room) {
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.contactNo = contactNo;
        this.eContactName = eContactName;
        this.eContactNo = eContactNo;
        this.email = email;
        this.role = role;
        this.room = room;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String geteContactName() {
        return eContactName;
    }

    public void seteContactName(String eContactName) {
        this.eContactName = eContactName;
    }

    public String geteContactNo() {
        return eContactNo;
    }

    public void seteContactNo(String eContactNo) {
        this.eContactNo = eContactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
}
