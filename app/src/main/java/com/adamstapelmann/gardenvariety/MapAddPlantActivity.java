package com.adamstapelmann.gardenvariety;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.adamstapelmann.gardenvariety.application.MyApplication;
import com.adamstapelmann.gardenvariety.data.Plant;
import com.adamstapelmann.gardenvariety.location_manager.PlantLocationManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import io.realm.RealmResults;

public class MapAddPlantActivity extends FragmentActivity implements OnMapReadyCallback, PlantLocationManager.OnNewLocationAvailable {

    public static final String PLANT_LOCATION = "PLANT_LOCATION";
    public static final String LOCATION_BUNDLE = "LOCATION_BUNDLE";

    private GoogleMap mMap;
    private boolean canGetLocation;

    private MarkerOptions markerOptions;
    private Marker marker;

    private Plant plant;

    private LatLng locationToSend;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_add_plant);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        canGetLocation = true;

        if (getIntent().getSerializableExtra(AddPlantActivity.KEY_ADD_PLANT) != null) {
            String plantId = getIntent().getStringExtra(AddPlantActivity.KEY_ADD_PLANT);

            plant = getRealm().where(Plant.class).equalTo("plantId", plantId).findFirst();
            canGetLocation = false;

        }

        if (getIntent().getParcelableExtra(LOCATION_BUNDLE) != null) {

            Bundle bundle = getIntent().getParcelableExtra(LOCATION_BUNDLE);
            LatLng locationFromMap = bundle.getParcelable(PLANT_LOCATION);
            locationToSend = locationFromMap;

        }

        if (getIntent().getStringExtra("PLANT_TITLE") != null) {

            name = getIntent().getStringExtra("PLANT_TITLE");

        }


    }

    public Realm getRealm () {
        return ((MyApplication)getApplication()).getRealmPlants();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        RealmResults<Plant> plants = getRealm().where(Plant.class).findAll();

        if (locationToSend != null) {
            markerOptions = new MarkerOptions()
                    .position(locationToSend)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            if (name != null) {
                markerOptions = markerOptions.title(name);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationToSend));

            marker = mMap.addMarker(markerOptions);

        }

        for (Plant plant : plants) {
            LatLng location = new LatLng(plant.getLatitude(), plant.getLongitude());
             markerOptions = new MarkerOptions()
                    .position(location)
                    .title(plant.getName());

            if (this.plant != null && this.plant.getPlantId().equals(plant.getPlantId())) {
            } else {
                mMap.addMarker(markerOptions);
            }

        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                markerOptions = new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));


                if (plant != null) {

                    markerOptions.title(plant.getName());

                } else if (name != null) {
                    markerOptions.title(name);
                }

                if (marker != null) {
                    marker.remove();
                }
                locationToSend = latLng;
                marker = mMap.addMarker(markerOptions);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                locationToSend = marker.getPosition();
            }
        });

    }

    @Override
    public void onNewLocation(Location location) {

        if (mMap != null && canGetLocation) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            LatLng currentLocation = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            canGetLocation = false;
        }


    }

    @Override
    public void finish() {

        sendPlantLocation();

        super.finish();
    }

    private void sendPlantLocation() {
        Intent intentResult = new Intent();

        if (marker != null) {
            Bundle args = new Bundle();
            args.putParcelable(PLANT_LOCATION, locationToSend);
            intentResult.putExtra(LOCATION_BUNDLE, args);
            setResult(RESULT_OK, intentResult);
        } else {
            setResult(RESULT_CANCELED, intentResult);
        }
    }
}
