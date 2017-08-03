package com.adamstapelmann.gardenvariety.application;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MyApplication  extends Application {

    private Realm realmPlants;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

    }

    public void openRealm() {
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realmPlants = Realm.getInstance(config);
    }

    public void closeRealm() {
        realmPlants.close();
    }

    public Realm getRealmPlants() {
        return realmPlants;
    }
}
