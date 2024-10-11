package com.example.gascalculation;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesHelper {

    private static final String LOCATIONS_KEY = "saved_locations";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public SharedPreferencesHelper(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.gson = new Gson();
    }

    // Save list of LocationItems to SharedPreferences
    public void saveLocationsList(List<LocationItem> locationList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Convert the list to JSON
        String json = gson.toJson(locationList);
        // Save the JSON string in SharedPreferences
        editor.putString(LOCATIONS_KEY, json);
        editor.apply(); // Apply changes asynchronously
    }

    public void saveCarModel(String carModel){
        // Save to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CAR_MODEL", carModel);
        editor.apply(); // Apply changes
    }

    public void saveCarCC( String carCC){
        // Save to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CAR_CC", carCC);
        editor.apply(); // Apply changes
    }

    public void saveGasPrice(String gasPrice){
        // Save to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("GAS_PRICE", gasPrice);
        editor.apply(); // Apply changes
    }

    // Retrieve list of LocationItems from SharedPreferences
    public List<LocationItem> getLocationsList() {
        String json = sharedPreferences.getString(LOCATIONS_KEY, null);

        if (json == null) {
            return new ArrayList<>(); // Return an empty list if no data is found
        }

        // Define the type for List<LocationItem>
        Type type = new TypeToken<List<LocationItem>>() {}.getType();

        // Deserialize the JSON string back into a list of LocationItems
        return gson.fromJson(json, type);
    }

    public String getCarModel() {
        return sharedPreferences.getString("CAR_MODEL", "");
    }

    public String getCarCC() {
        return sharedPreferences.getString("CAR_CC", "");
    }

    public String getGasPrice() {
        String gasPrice = sharedPreferences.getString("GAS_PRICE", "");
        return gasPrice;
    }


}

