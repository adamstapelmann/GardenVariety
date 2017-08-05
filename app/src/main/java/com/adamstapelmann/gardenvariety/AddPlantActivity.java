package com.adamstapelmann.gardenvariety;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.adamstapelmann.gardenvariety.application.MyApplication;
import com.adamstapelmann.gardenvariety.data.Plant;
import com.adamstapelmann.gardenvariety.location_manager.PlantLocationManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

import io.realm.Realm;

public class AddPlantActivity extends AppCompatActivity implements PlantLocationManager.OnNewLocationAvailable {

    public static final String KEY_ADD_PLANT = "KEY_ADD_PLANT";
    public static final int RESULT_CODE_DELETE = 101;

    public static final int REQUEST_GET_LOCATION = 201;

    private Plant plant;

    private EditText etName;

    private boolean canCreate;

    private double currentLat, currentLng;
    private double lat, lng;
    private PlantLocationManager plantLocationManager = new PlantLocationManager(this);
    private boolean canGetCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        requestNeededPermission();

        setUpUI();
        canCreate = true;
        canGetCurrentLocation = false;

        if (getIntent().getSerializableExtra(ViewPlantsListActivity.KEY_EDIT) != null) {
            canCreate = false;
            initializeEdit();
        } else {
            canCreate = true;
            canGetCurrentLocation = true;
        }

    }

    private void setUpUI() {
        setUpImageViews();
        setUpBtns();
        setUpEditTexts();
    }

    private void setUpImageViews() {
        setUpIvSavePicture();
        setUpIvViewPictures();
        setUpIvViewMap();
    }
    private void setUpBtns() {
        setUpBtnSave();
        setUpBtnCancel();
        setUpBtnDelete();
        setUpBtnGetCurrentLocation();
        setUpBtnChooseOnMap();
    }
    private void setUpEditTexts() {
        etName = (EditText) findViewById(R.id.etPlantName);
    }

    private void setUpIvSavePicture() {
        ImageView ivSavePicture = (ImageView) findViewById(R.id.ivTakePicture);
        ivSavePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch activity to open camera
            }
        });
    }
    private void setUpIvViewPictures() {
        ImageView ivViewPictures = (ImageView) findViewById(R.id.ivViewPictures);
        ivViewPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch activity to view pictures
            }
        });
    }
    private void setUpIvViewMap() {
        ImageView ivViewMap = (ImageView) findViewById(R.id.ivViewMap);
        ivViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch activity to view map
            }
        });
    }

    private void setUpBtnSave() {
        Button btnSave = (Button) findViewById(R.id.btnSavePlant);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }
    private void setUpBtnCancel() {
        Button btnCancel = (Button) findViewById(R.id.btnCancelAddPlant);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canCreate) {

                    String plantId = plant.getPlantId();

                    Intent intentResult = new Intent();
                    intentResult.putExtra(KEY_ADD_PLANT, plantId);
                    setResult(RESULT_CANCELED, intentResult);

                }

                finish();
            }
        });
    }
    private void setUpBtnDelete() {
        Button btnDelete = (Button) findViewById(R.id.btnDeleteAddPlant);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canCreate) {

                    String plantId = plant.getPlantId();

                    Intent intentResult = new Intent();
                    intentResult.putExtra(KEY_ADD_PLANT, plantId);
                    setResult(RESULT_CODE_DELETE, intentResult);

                }
                    finish();
            }
        });
    }
    private void setUpBtnGetCurrentLocation() {
        Button btnUseCurrentLocation = (Button) findViewById(R.id.btnGetCurrentLocation);
        btnUseCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canGetCurrentLocation = true;
            }
        });
    }
    private void setUpBtnChooseOnMap() {
        Button btnChooseOnMap = (Button) findViewById(R.id.btnChooseLocationOnMap);
        btnChooseOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOpenMap = new Intent(AddPlantActivity.this, MapAddPlantActivity.class);

                if (getIntent().getSerializableExtra(ViewPlantsListActivity.KEY_EDIT) != null) {
                    intentOpenMap.putExtra(KEY_ADD_PLANT, plant.getPlantId());
                }

                startActivityForResult(intentOpenMap, REQUEST_GET_LOCATION);
                // Handle results sent back from map fragment
            }
        });

    }

    public Realm getRealm() {
        return ((MyApplication)getApplication()).getRealmPlants();
    }

    private void save() {
        if (canSave()) {
            if (canCreate) {
                initializeCreate();
            }

            getRealm().beginTransaction();

            plant.setName(etName.getText().toString());
            plant.setLatitude(lat);
            plant.setLongitude(lng);

            getRealm().commitTransaction();

            Intent intentResult = new Intent();
            intentResult.putExtra(KEY_ADD_PLANT, plant.getPlantId());
            setResult(RESULT_OK, intentResult);

            finish();
        }
    }

    private boolean canSave() {
        boolean canSave = true;

        if (TextUtils.isEmpty(etName.getText())) {
            canSave = false;
            etName.setError("Field required");
        }

        return canSave;
    }

    private void initializeCreate() {
        getRealm().beginTransaction();
        plant = getRealm().createObject(Plant.class, UUID.randomUUID().toString());
        getRealm().commitTransaction();
    }

    private void initializeEdit() {
        String plantId = getIntent().getStringExtra(ViewPlantsListActivity.KEY_EDIT);
        plant = getRealm().where(Plant.class)
                .equalTo("plantId", plantId)
                .findFirst();

        etName.setText(plant.getName());

        lat = plant.getLatitude();
        lng = plant.getLongitude();

    }

    @Override
    public void onNewLocation(Location location) {
        currentLat = location.getLatitude();
        currentLng = location.getLongitude();

        if (canGetCurrentLocation) {
            lat = currentLat;
            lng = currentLng;
            canGetCurrentLocation = false;
        }

    }

    private void requestNeededPermission() {

        Log.i("permissionmethod","method");

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)) {

            Log.i("START MONITORING","monitoring");
            plantLocationManager.startLocationMonitoring(this);

        }else{
            Log.i("REQUEST PERM","requesting");


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    101);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
                plantLocationManager.startLocationMonitoring(this);

                Log.i("PERMISSIONS GRANTED", "granted");

            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Bundle bundle = data.getParcelableExtra(MapAddPlantActivity.LOCATION_BUNDLE);
                LatLng locationFromMap = bundle.getParcelable(MapAddPlantActivity.PLANT_LOCATION);
                lat = locationFromMap.latitude;
                lng = locationFromMap.longitude;
                break;
            case RESULT_CANCELED:
                // do nothing?
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (plantLocationManager != null) {
            plantLocationManager.stopLocationMonitoring();
        }

        super.onDestroy();
    }
}
