package com.example.user.bustracker;

public class PassDataDriver {
    public String name;
    public String password,driverID;
    public String imguri,direction,emailId,selected;
    Double lat,lng;

    PassDataDriver(){

    }
    PassDataDriver(String name, String password,String driverID,String imguri,String direction,String emailId, double lat, double lng){
        this.name = name;
        this.password = password;
        this.lat= lat;
        this.lng = lng;
        this.driverID = driverID;
        this.imguri = imguri;
        this.direction = direction;
        this.emailId = emailId;
        this.selected = selected;
    }
    PassDataDriver(double lat, Double lng){
        this.lat = lat;
        this.lng = lat;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getDriverID() {
        return driverID;
    }

    public String getImguri() {
        return imguri;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public void setImguri(String imguri) {
        this.imguri = imguri;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getSelected() {
        return selected;
    }
}
