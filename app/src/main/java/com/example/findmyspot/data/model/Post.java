package com.example.findmyspot.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("MACAddress")
    @Expose
    private String MACAddress;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("encodedImage")
    @Expose
    private String encodedImage;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    public String getMACAddress() {
        return MACAddress;
    }

    public void setMACAddress(String MACAddress) {
        this.MACAddress = MACAddress;
    }

    public String getlatitude() {
        return latitude;
    }

    public void setlatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getlongitude() {
        return longitude;
    }

    public void setlongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getencodedImage() {
        return encodedImage;
    }

    public void setencodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public String gettimestamp() {
        return timestamp;
    }

    public void settimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Post{" +
                "MACAddress='" + MACAddress + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude=" + longitude +
                ", encodedImage=" + encodedImage +
                ", timestamp=" + timestamp +
                '}';
    }


}
