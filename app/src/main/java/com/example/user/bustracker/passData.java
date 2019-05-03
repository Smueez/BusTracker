package com.example.user.bustracker;

class PassData {
    public String name;
    public String password;
    public String phoneno;
    public String imguri;


    PassData()
    {

    }

    PassData(String name, String password, String phn,String imguri) {
        this.name = name;
        this.password = password;
        this.phoneno = phn;
        this.imguri=imguri;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }


}
