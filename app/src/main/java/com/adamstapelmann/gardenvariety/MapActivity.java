package com.adamstapelmann.gardenvariety;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.adamstapelmann.gardenvariety.application.MyApplication;
import com.adamstapelmann.gardenvariety.data.Plant;
import com.adamstapelmann.gardenvariety.location_manager.PlantLocationManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import io.realm.RealmResults;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, PlantLocationManager.OnNewLocationAvailable {

    private GoogleMap mMap;
    private boolean canGetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        canGetLocation = true;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public Realm getRealm () {
        return ((MyApplication)getApplication()).getRealmPlants();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        RealmResults<Plant> plants = getRealm().where(Plant.class).findAll();

        if (plants.size() > 0) {

            LatLng marker = new LatLng(plants.get(0).getLatitude(),
                    plants.get(0).getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));

            for (Plant plant : plants) {
                marker = new LatLng(plant.getLatitude(), plant.getLongitude());
                mMap.addMarker(new MarkerOptions()
                .position(marker)
                .title(plant.getName())
                );
            }


        }

    }

    @Override
    public void onNewLocation(Location location) {

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        if (canGetLocation) {
            LatLng currentLocation = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            canGetLocation = false;
        }
    }
}
