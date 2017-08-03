package com.adamstapelmann.gardenvariety;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.adamstapelmann.gardenvariety.application.MyApplication;
import com.adamstapelmann.gardenvariety.data.Plant;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    int REQUEST_ADD_PLANT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((MyApplication)getApplication()).openRealm();
        setUpUI();

    }


    private void setUpUI() {
        setUpButtons();
    }

    private void setUpButtons() {
        setUpBtnMap();
        setUpBtnViewPlants();
        setUpBtnAddPlant();

    }

    private void setUpBtnMap() {
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(MapActivity.class);
            }
        });
    }
    private void setUpBtnViewPlants() {
        Button btnViewPlants = (Button) findViewById(R.id.btnViewPlants);
        btnViewPlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(ViewPlantsListActivity.class);
            }
        });
    }
    private void setUpBtnAddPlant() {
        Button btnAddPlant = (Button) findViewById(R.id.btnAddPlant);
        btnAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddPlant = new Intent(MainActivity.this, AddPlantActivity.class);
                startActivity(intentAddPlant);
            }
        });
    }

    private void launchActivity(Class c) {
        Intent intent = new Intent(MainActivity.this, c);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        ((MyApplication)getApplication()).closeRealm();
        super.onDestroy();
    }
}
