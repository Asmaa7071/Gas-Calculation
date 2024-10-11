package com.example.gascalculation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.adapters.TextViewBindingAdapter;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import com.example.gascalculation.databinding.ActivityMainBinding;
import com.example.gascalculation.databinding.DialogAddLocationBinding;
import com.example.gascalculation.databinding.InfoDialogBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import mumayank.com.airlocationlibrary.AirLocation;

public class MainActivity extends AppCompatActivity implements AirLocation.Callback{
    ActivityMainBinding binding;
    private List<LocationItem> items;
    private LocationAdapter locationAdapter;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // Adjust thread pool size if needed
    private final Handler mainHandler = new Handler(Looper.getMainLooper()); // Handler to post results to the main thread
    AirLocation airLocation;
    Geocoder geocoder;
    SharedPreferences sharedPreferences;
    SharedPreferencesHelper sharedPreferencesHelper;
    List<String> carModel = new ArrayList<>();
    List<String> carCC = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addPlacesButton.setOnClickListener(view -> openAddLocationDialog());

        // Initialize the items list
        items = new ArrayList<>();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        sharedPreferencesHelper = new SharedPreferencesHelper(sharedPreferences);

        carModel = Arrays.asList("Select Car Model...","Toyota", "Honda", "Ford", "Chevrolet", "Nissan",
                "Hyundai", "Kia", "Volkswagen", "Subaru", "Mazda", "BMW", "Mercedes-Benz", "Audi", "Lexus");

        carCC = Arrays.asList("Select Car CC...","1000 CC", "1200 CC", "1400 CC", "1600 CC", "1800 CC",
                "2000 CC", "2200 CC", "2400 CC", "2600 CC", "2800 CC", "3000 CC", "3200 CC");

        airLocation = new AirLocation(MainActivity.this,this,true,0,"");

        geocoder = new Geocoder(this, Locale.getDefault());

        // Initialize the adapter and set it to the RecyclerView
        locationAdapter = new LocationAdapter(items);
        binding.placesRV.setAdapter(locationAdapter);

        // Attach the swipe-to-delete functionality
        attachSwipeToDelete(binding.placesRV);

        binding.calculateButton.setOnClickListener(view -> {
            if (validateDate()){
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                startActivity(intent);
            }
        });
        binding.toolbar.setNavigationIcon(R.drawable.city);
        binding.infoIcon.setOnClickListener(view -> {
            // Inflate the dialog layout using View Binding
            InfoDialogBinding dialogBinding = InfoDialogBinding.inflate(getLayoutInflater());

            // Create an AlertDialog.Builder and set the custom view
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogBinding.getRoot());

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            dialogBinding.okButton.setOnClickListener(view1 -> dialog.dismiss());
            // Prevent the dialog from dismissing when clicked outside
            dialog.setCanceledOnTouchOutside(false);
        });

        //  ArrayAdapter with the list of data and a simple spinner layout
        ArrayAdapter<String> carModelAdapterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carModel);
        carModelAdapterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.carModelSpinner.setAdapter(carModelAdapterAdapter);

        //  ArrayAdapter with the list of data and a simple spinner layout
        ArrayAdapter<String> carCCAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carCC);
        carCCAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.carCcSpinner.setAdapter(carCCAdapter);


        binding.carModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sharedPreferencesHelper.saveCarModel(carModel.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.carCcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sharedPreferencesHelper.saveCarCC(carCC.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.gasPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sharedPreferencesHelper.saveGasPrice(editable.toString());
            }
        });

    }

    private Boolean validateDate() {
        if (binding.carModelSpinner.getSelectedItem().equals("Select Car Model...")){
            Toast.makeText(this, "Select Car Model", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (binding.carCcSpinner.getSelectedItem().equals("Select Car CC...")){
            Toast.makeText(this, "Select Car CC", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (Objects.requireNonNull(binding.gasPrice.getText()).toString().trim().isEmpty()){
            binding.gasPrice.setError("Enter Gas Price");
            return false;
        } else if (items.size()<2) {
            Toast.makeText(this, "Palaces Must Be More Than One ", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Retrieve the list of locations from SharedPreferences
        items = sharedPreferencesHelper.getLocationsList();
        if (!items.isEmpty()){
            // Set the adapter
            locationAdapter = new LocationAdapter(items); // Create your adapter
            binding.placesRV.setAdapter(locationAdapter);
        }
        if (!sharedPreferencesHelper.getCarModel().isEmpty()){
            binding.carModelSpinner.setSelection(carModel.indexOf(sharedPreferencesHelper.getCarModel()));
        }
        if (!sharedPreferencesHelper.getCarCC().isEmpty()){
            binding.carCcSpinner.setSelection(carCC.indexOf(sharedPreferencesHelper.getCarCC()));
        }
        if (!sharedPreferencesHelper.getGasPrice().isEmpty()){
            binding.gasPrice.setText(sharedPreferencesHelper.getGasPrice());
        }
    }

    private void openAddLocationDialog() {
        // Inflate the dialog layout using View Binding
        DialogAddLocationBinding dialogBinding = DialogAddLocationBinding.inflate(getLayoutInflater());

        // Create an AlertDialog.Builder and set the custom view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogBinding.getRoot());

        // Create and show the dialog
        AlertDialog dialog = builder.create();

        dialog.show();

        // Prevent the dialog from dismissing when clicked outside
        dialog.setCanceledOnTouchOutside(false);

        // Handle the button click inside the dialog
        dialogBinding.buttonAddLocation.setOnClickListener(v -> {
            String location = Objects.requireNonNull(dialogBinding.editTextLocation.getText()).toString().trim();
            if (!location.isEmpty()) {
                calculateLocationAsync(location,dialogBinding.editTextLocation,dialog);
            } else {
                dialogBinding.editTextLocation.setError("Location is required");
            }
        });

        dialogBinding.currentLocationTV.setOnClickListener(view -> {
            airLocation.start();
            dialog.dismiss();
        });

        dialogBinding.buttonCancel.setOnClickListener(view -> dialog.dismiss());
    }

    // Execute location calculation on a background thread
    @SuppressLint("NotifyDataSetChanged")
    private void calculateLocationAsync(String addressName, EditText locationEditText, AlertDialog dialog) {
        for (LocationItem locationItem : sharedPreferencesHelper.getLocationsList()) {
            if(locationItem.getLocationName().equals(addressName)){
                mainHandler.post(() -> {
                    Toast.makeText(MainActivity.this, "This Location Added Before", Toast.LENGTH_SHORT).show();
                    locationEditText.setError("This Location Added Before");
                });
                return;
            }
        }
        executorService.submit(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocationName(addressName, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    if (!LocationChecker.isLocationInEgypt(address.getLatitude(),address.getLongitude())){
                        mainHandler.post(() -> locationEditText.setError("This Location Is OutSide of Egypt"));
                        return;
                    }
                    items.add(new LocationItem(addressName, address.getLatitude(),+ address.getLongitude()));
                    sharedPreferencesHelper.saveLocationsList(items);
                    // Add the new location to the list on the UI thread
                    mainHandler.post(() -> {
                        Toast.makeText(MainActivity.this,"Added Successfully", Toast.LENGTH_LONG).show();
                        locationAdapter.notifyDataSetChanged();// Notify the adapter that the data set has changed
                        dialog.dismiss();
                    });
                } else {
                    mainHandler.post(() -> {
                        Toast.makeText(MainActivity.this,"This Location not Found", Toast.LENGTH_LONG).show();
                        locationEditText.setError("This Location not Found");
                    });
                }
            } catch (IOException e) {
                Log.e("Geocoder", "Error finding location for " + addressName, e);
                mainHandler.post(() -> Toast.makeText(MainActivity.this, "Error finding location for " + addressName, Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Method to attach swipe to delete functionality
    private void attachSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();

                // Move the item in the data source
                Collections.swap(items, fromPosition, toPosition);
                locationAdapter.notifyItemMoved(fromPosition, toPosition); // Notify adapter of the move
                sharedPreferencesHelper.saveLocationsList(items);
                return true; // Return true to indicate that the move was handled
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // When swiped, remove the item from the adapter
                int position = viewHolder.getBindingAdapterPosition();
                locationAdapter.deleteItem(position);
                sharedPreferencesHelper.saveLocationsList(items);
                showSnackBar();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // Draw the red background and the delete icon as the item is swiped
                new RecyclerViewSwipeDecorator.Builder(getApplicationContext(),c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red)) // Set background color for swipe
                        .addActionIcon(R.drawable.ic_delete) // Set your delete icon here
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // Attach ItemTouchHelper to RecyclerView
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    private void showSnackBar() {
        // Show the SnackBar with the Undo option
        Snackbar snackbar = Snackbar.make(binding.placesRV, "Item deleted", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", v -> {
            // Restore the deleted item
            locationAdapter.restoreItem();
        });
        snackbar.show();
    }

    @Override
    public void onFailure(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {
        Toast.makeText(MainActivity.this, "Location not Found", Toast.LENGTH_LONG).show();

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccess(@NonNull ArrayList<Location> arrayList) {
        List<Address> addresses;
        try {
             addresses = geocoder.getFromLocation(arrayList.get(0).getLatitude(),arrayList.get(0).getLongitude(),1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LocationItem item = new LocationItem(addresses.get(0).getSubAdminArea(),arrayList.get(0).getLatitude() ,arrayList.get(0).getLongitude());
        if (locationIsFoundBefore(item)) {
            Toast.makeText(MainActivity.this, "This Location Added Before", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(MainActivity.this, "Added Successfully", Toast.LENGTH_LONG).show();
        items.add(item);
        sharedPreferencesHelper.saveLocationsList(items);
        locationAdapter.notifyDataSetChanged();  // Notify the adapter that the data set has changed
    }

    public Boolean locationIsFoundBefore(LocationItem location) {
        return sharedPreferencesHelper.getLocationsList().contains(location);
    }


}
