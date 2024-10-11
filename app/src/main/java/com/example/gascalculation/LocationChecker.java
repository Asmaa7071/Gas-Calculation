package com.example.gascalculation;

public class LocationChecker {

    // Egypt's approximate latitude and longitude boundaries
    private static final double EGYPT_NORTH_LAT = 31.667;
    private static final double EGYPT_SOUTH_LAT = 22.0;
    private static final double EGYPT_WEST_LON = 25.0;
    private static final double EGYPT_EAST_LON = 36.667;

    /**
     * This function checks if a given latitude and longitude are within the boundaries of Egypt.
     *
     * @param latitude  The latitude of the location.
     * @param longitude The longitude of the location.
     * @return true if the location is within Egypt's boundaries, false otherwise.
     */
    public static boolean isLocationInEgypt(double latitude, double longitude) {
        return (latitude >= EGYPT_SOUTH_LAT && latitude <= EGYPT_NORTH_LAT) &&
                (longitude >= EGYPT_WEST_LON && longitude <= EGYPT_EAST_LON);
    }
}

