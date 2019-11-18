package lu.uni.project.eventmanager.pojo;

import android.os.Parcel;

import java.io.Serializable;

public class Location implements Serializable {
    public String latitude="";
    public String longitude="";
    public String address="";
    public String zipCode="";
    public String venueDetails="";

    public Location() {
    }

    public Location(String latitude, String longitude, String address, String zipCode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.zipCode = zipCode;
    }

    public Location(Parcel in) {
        latitude = in.readString();
        longitude = in.readString();
        address = in.readString();
        zipCode = in.readString();
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getVenueDetails() {
        return venueDetails;
    }

    public void setVenueDetails(String venueDetails) {
        this.venueDetails = venueDetails;
    }
}
