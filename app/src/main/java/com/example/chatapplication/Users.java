package com.example.chatapplication;

public class Users {
    String uID;
    String name;
    String emailID;
    String imageURI;
    String status;

    public Users() {
    }

    public Users(String uID, String name, String emailID, String imageURI, String status) {
        this.uID = uID;
        this.name = name;
        this.emailID = emailID;
        this.imageURI = imageURI;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }
}
