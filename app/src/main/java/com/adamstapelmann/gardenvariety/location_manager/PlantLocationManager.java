package com.adamstapelmann.gardenvariety.location_manager;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class PlantLocationManager implements LocationListener{

    public interface OnNewLocationAvailable {
        void onNewLocation(Location location);
    }

    private LocationManager locationManager;
    private OnNewLocationAvailable onNewLocationAvailable;

    public PlantLocationManager(OnNewLocationAvailable onNewLocationAvailable) {
        this.onNewLocationAvailable = onNewLocationAvailable;
    }

    public void startLocationMonitoring(Context context) throws SecurityException {
        locationManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE
        );

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, this);
    }

    public void stopLocationMonitoring() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        onNewLocationAvailable.onNewLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



}
