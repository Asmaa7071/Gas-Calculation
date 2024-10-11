package com.example.gascalculation;

import java.util.Objects;

public class LocationItem {
    private String locationName;
    private Double latitude;
    private Double longitude;

    public LocationItem(String locationName, Double latitude, Double longitude) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}

