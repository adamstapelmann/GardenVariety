package com.adamstapelmann.gardenvariety;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.adamstapelmann.gardenvariety.adapter.PlantListAdapter;
import com.adamstapelmann.gardenvariety.application.MyApplication;
import com.adamstapelmann.gardenvariety.data.Plant;

import io.realm.Realm;

import static com.adamstapelmann.gardenvariety.R.id.recyclerViewPlants;

public class ViewPlantsListActivity extends AppCompatActivity {

    public static final String KEY_EDIT = "KEY_EDIT";
    public static final int REQUEST_EDIT_PLANT = 101;
    private RecyclerView recyclerView;
    private PlantListAdapter plantListAdapter;
    private int plantToEditPosition = -1;
    private RelativeLayout layoutContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plants_list);

        layoutContent = (RelativeLayout) findViewById(R.id.layoutContent);
        setUpRecycler();
        setUpToolbar();


    }

    private void setUpRecycler() {
        recyclerView = (RecyclerView) findViewById(recyclerViewPlants);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        plantListAdapter = new PlantListAdapter(this, getRealm());
        recyclerView.setAdapter(plantListAdapter);

    }

    private void setUpToolbar() {
        Toolbar tbSortPlants = (Toolbar) findViewById(R.id.tbSortPlants);

        setSupportActionBar(tbSortPlants);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tbSortPlants.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_sort_by_name:
                        plantListAdapter.sortByName();
                        showSnackBarMessage("Sorted plants by name");
                        break;
                }
                return false;
            }
        });
    }


    public Realm getRealm() {
        return ((MyApplication)getApplication()).getRealmPlants();
    }

    public void showPlantDetails (String plantId, int position) {
        Intent intentEdit = new Intent(ViewPlantsListActivity.this, AddPlantActivity.class);
        plantToEditPosition = position;

        intentEdit.putExtra(KEY_EDIT, plantId);
        startActivityForResult(intentEdit, REQUEST_EDIT_PLANT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String plantId = data.getStringExtra(
                        AddPlantActivity.KEY_ADD_PLANT);


                Plant plant = getRealm().where(Plant.class)
                        .equalTo("plantId", plantId)
                        .findFirst();

                if (requestCode == REQUEST_EDIT_PLANT) {
                    plantListAdapter.updatePlant(plantToEditPosition, plant);
                    showSnackBarMessage("Plant edited successfully");
                }
                break;
            case RESULT_CANCELED:
                showSnackBarMessage("Edit canceled");
                break;
            case AddPlantActivity.RESULT_CODE_DELETE:
                String delPlaceId  = data.getStringExtra(
                        AddPlantActivity.KEY_ADD_PLANT);

                plantListAdapter.removePlantByKey(delPlaceId);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_sort_plants, menu);
        return true;
    }

    public void deletePlant(Plant plant) {
        getRealm().beginTransaction();
        plant.deleteFromRealm();
        getRealm().commitTransaction();
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(layoutContent,
                message,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.action_hide, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
            }
        }).show();
    }

}
