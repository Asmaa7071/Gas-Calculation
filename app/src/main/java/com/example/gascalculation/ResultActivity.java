package com.example.gascalculation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.gascalculation.databinding.ActivityResultBinding;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;
    List<LocationItem> recommendedRoute = new ArrayList<>();
    RouteCalculator routeCalculator = new RouteCalculator();
    SharedPreferences sharedPreferences;
    SharedPreferencesHelper sharedPreferencesHelper;
    List<LocationItem> savedLocations;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        binding.toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        // Access the same SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        sharedPreferencesHelper = new SharedPreferencesHelper(sharedPreferences);

        // Retrieve the list of locations from SharedPreferences
        savedLocations = sharedPreferencesHelper.getLocationsList();

        CalculateRecommendedRoute();

        String carModel = sharedPreferencesHelper.getCarModel();;
        String carCC = sharedPreferencesHelper.getCarCC();
        double gasPrice = Double.parseDouble(sharedPreferencesHelper.getGasPrice());
        double originalDistance = routeCalculator.calculateTotalDistance(savedLocations,getIndices(savedLocations));
        double recommendedDistance = routeCalculator.calculateTotalDistance(recommendedRoute,getIndices(recommendedRoute));
        double originalFuel = routeCalculator.calculateFuelUsed(originalDistance,carCC);
        double recommendedFuel = routeCalculator.calculateFuelUsed(recommendedDistance,carCC);
        double originalCost = originalFuel * gasPrice;
        double recommendedCost = recommendedFuel * gasPrice;

        TextAdapter textAdapter = new TextAdapter();
        textAdapter.SetData(savedLocations);

        binding.carModel.setText("Car Model: "+ carModel);
        binding.engineCapacity.setText("Engine Capacity: "+ carCC);
        binding.fuelPrice.setText("Fuel Price: "+ sharedPreferencesHelper.getGasPrice()+ " EGP/L");
        binding.totalFuelNeeded.setText("Total Fuel Needed: " + getRoundDecimal(originalFuel));
        binding.totalDistance.setText("Total Distance: "+ getRoundDecimal(originalDistance) + " KM");
        binding.totalCost.setText("Total Cost: " + getRoundDecimal(originalCost) + " EGP");
        binding.placesRecyclerView.setAdapter(textAdapter);

        binding.toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                textAdapter.SetData(recommendedRoute);
                binding.totalDistance.setText("Total Distance: "+ getRoundDecimal(recommendedDistance) + " KM");
                binding.totalFuelNeeded.setText("Total Fuel Needed: " + getRoundDecimal(recommendedFuel)+ " L");
                binding.totalCost.setText("Total Cost: " + getRoundDecimal(recommendedCost) + " EGP");

            } else {
                textAdapter.SetData(savedLocations);
                binding.totalDistance.setText("Total Distance: "+ getRoundDecimal(originalDistance) + " KM");
                binding.totalFuelNeeded.setText("Total Fuel Needed: " + getRoundDecimal(originalFuel) + " L");
                binding.totalCost.setText("Total Cost: " + getRoundDecimal(originalCost) + " EGP");
            }
            // Notify the adapter that the data has changed
            textAdapter.notifyDataSetChanged();
        });
    }

    private void CalculateRecommendedRoute() {
        List<Integer> data= routeCalculator.calculateShortestRoute(sharedPreferencesHelper.getLocationsList());
        for (Integer datum : data) {
            Log.d("TAG", "onCreate: " + datum);
            recommendedRoute.add(sharedPreferencesHelper.getLocationsList().get(datum));
        }
    }

    private double getRoundDecimal(Double number){
        // Convert to BigDecimal and set scale to 2 with rounding
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private List<Integer>getIndices(List<LocationItem>locationItems){
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < locationItems.size(); i++) {
            indices.add(i);
        }
        return indices;
    }



}