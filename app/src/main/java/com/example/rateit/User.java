package com.example.rateit;

public class User {
    private String name;
    private String id;
    private String phone;
    private String mail;

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getMail() {
        return mail;
    }

    public String getPhone() {
        return phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    //WE MUST HAVE AN EMPTY CONSTRUCTOR
    public User(){

    }
    public User(String id, String phone, String mail) {

        this.id = id;
        this.phone = phone;
        this.mail = mail;
    }


}
