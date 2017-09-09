package com.adamstapelmann.gardenvariety.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamstapelmann.gardenvariety.ViewPlantsListActivity;
import com.adamstapelmann.gardenvariety.data.Plant;
import com.adamstapelmann.gardenvariety.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;


public class PlantListAdapter extends RecyclerView.Adapter<PlantListAdapter.ViewHolder> {

    private ArrayList<Plant> plants;
    private Context context;
    private Realm realm;

    public PlantListAdapter(Context context, Realm realm) {
        this.context = context;
        this.realm = realm;

        RealmResults<Plant> realmResults = realm.where(Plant.class).findAll();
        Plant[] tmp = new Plant[realmResults.size()];
        plants = new ArrayList<>(Arrays.asList(realmResults.toArray(tmp)));
    }

    public void add(Plant plant) {
        plants.add(plant);
        notifyDataSetChanged();
    }

    public void remove(Plant plant) {
        int position = plants.indexOf(plant);
        plants.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(int position) {
        ((ViewPlantsListActivity) context).deletePlant(plants.get(position));
        plants.remove(position);
        notifyItemRemoved(position);
    }

    public void updatePlant(int position, Plant plant) {
        plants.set(position, plant);
        notifyItemChanged(position);
    }

    public void removePlantByKey(String plantId) {
        for (int i = 0; i < plants.size(); i++) {
            if (plants.get(i).getPlantId().equals(plantId)) {
                ((ViewPlantsListActivity) context).deletePlant(plants.get(i));
                plants.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void sortByName() {
        Collections.sort(plants, new Plant.CompareName());
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plant_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String name = plants.get(position).getName();

        realm.beginTransaction();
        holder.name.setText(name);
        realm.commitTransaction();

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewPlantsListActivity) context).showPlantDetails(
                        plants.get(position).getPlantId(),
                        holder.getAdapterPosition()
                );
            }
        });

    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        Button btnDetails;
        ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.tvPlantName);
            btnDetails = (Button) itemView.findViewById(R.id.btnPlantDetails);
            thumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);

        }
    }


}
