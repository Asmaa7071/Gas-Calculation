package com.example.gascalculation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class RouteCalculator {
    private double shortestDistance = Double.MAX_VALUE;
    private List<Integer> shortestRoute = new ArrayList<>();
    public List<Integer> calculateShortestRoute(List<LocationItem> LocationItems) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < LocationItems.size(); i++) {
            indices.add(i);
        }
        permute(indices, 1, LocationItems);
        return shortestRoute;
    }

    private void permute(List<Integer> arr, int start, List<LocationItem> LocationItems) {
        if (start == arr.size() - 1) {
            double distance = calculateTotalDistance(LocationItems, arr);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                shortestRoute = new ArrayList<>(arr);
            }
            return;
        }

        for (int i = start; i < arr.size(); i++) {
            List<Integer> newArr = new ArrayList<>(arr);
            // Swap elements
            int temp = newArr.get(start);
            newArr.set(start, newArr.get(i));
            newArr.set(i, temp);
            permute(newArr, start + 1, LocationItems);
        }
    }

    public double calculateTotalDistance(List<LocationItem> LocationItems, List<Integer> route) {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += calculateDistance(
                    LocationItems.get(route.get(i)).getLatitude(),
                    LocationItems.get(route.get(i)).getLongitude(),
                    LocationItems.get(route.get(i + 1)).getLatitude(),
                    LocationItems.get(route.get(i + 1)).getLongitude()
            );
        }
        return totalDistance;
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double earthRadius = 6371; // Earth's radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c; // Distance in kilometers
    }
    // Function to calculate fuel consumption based on engine size (CC)
    private double calculateFuelConsumption(String carCC) {
        // Base consumption for the smallest engine (1000 CC)
        final double baseConsumption = 5.0; // Base consumption (L/100 km) for 1000 CC

        // Consumption increase per 100 CC (e.g., 0.2 L/100 km per 100 CC increase)
        final double ccFactor = 0.2;
        // Parse the carCC string to extract the numeric value
        String[] parts = carCC.split(" ");
        int cc = Integer.parseInt(parts[0]);

        // Calculate additional consumption based on the engine capacity
        double additionalConsumption = ccFactor * ((cc - 1000) / 100);

        // Total consumption
        return baseConsumption + additionalConsumption;
    }

    // Function to calculate fuel usage for a trip based on distance
    public double calculateFuelUsed(double distance, String carCC) {
        double fuelConsumptionPer100Km = calculateFuelConsumption(carCC);

        // Calculate total fuel used for the given distance
        return (fuelConsumptionPer100Km / 100) * distance;
    }

}

