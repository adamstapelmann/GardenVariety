package com.adamstapelmann.gardenvariety.data;

import java.util.Comparator;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Plant extends RealmObject{

    @PrimaryKey
    private String plantId;

    private String name;
    private Date pickUpDate;

    private double latitude, longitude;

    private String pictureURL;
    private boolean pictureUploaded;

    public Plant () {
        pictureUploaded = false;
    }

    public Plant (String name) {
        this.name = name;
        pictureUploaded = false;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(Date pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public boolean isPictureUploaded() {
        return pictureUploaded;
    }

    public void setPictureUploaded(boolean pictureUploaded) {
        this.pictureUploaded = pictureUploaded;
    }

    public static class CompareName implements Comparator<Plant> {
        public int compare(Plant a, Plant b) {
            return a.getName().compareTo(b.getName());
        }

    }


}
