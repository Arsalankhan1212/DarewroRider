package com.darewro.rider.view.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class LocationUtils {

    // location types
    public static final String TYPE_PAYMENT = "3";
    public static final String TYPE_DELIVERY = "2";
    public static final String TYPE_PICKUP = "1";
    public static final String TYPE_PAYMENT_STRING = "PAYMENT";
    public static final String TYPE_DELIVERY_STRING = "DELIVERY";
    public static final String TYPE_PICKUP_STRING = "PICKUP";

    private static final DecimalFormat formatter = new DecimalFormat("#.#########");

    public static String getLocationName(Context context, LatLng point) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Log.i("geoCoder = ", "nulllll");
        if (geocoder != null) {
            Log.i("geoCoder = ", "not nulllll");
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                String cityName = addresses.get(0).getLocality();
                cityName = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality();//addresses.get(0).getSubLocality()==null?addresses.get(0).getLocality():addresses.get(0).getSubLocality()+", "+addresses.get(0).getLocality();
                Log.i("City Name = ", cityName + "*****************************");
                Log.i("Address = ", addresses.toString() + "*****************************");
                return cityName;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static String getAddress(Context context, LatLng latLng) {
        String cAddress = "";
        String errorMessage = "";
        if (latLng == null) {
            errorMessage = "no_location_data_provided";
            Log.wtf("address = ", errorMessage);
            return "";
        }
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.

            addresses = geocoder.getFromLocation(
                    Double.parseDouble(String.valueOf(formatter.format(latLng.latitude))),
                    Double.parseDouble(String.valueOf(formatter.format(latLng.longitude))),
                    // In this sample, we get just a single address.
                    1);


        /*    addresses = geocoder.getFromLocation(
                    Double.parseDouble(String.valueOf(latLng.latitude).substring(0, 9)),
                    Double.parseDouble(String.valueOf(latLng.longitude).substring(0, 9)),
                    // In this sample, we get just a single address.
                    1);*/


        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "service_not_available";
            Log.e("address = ", errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "invalid_lat_long_used";
            Log.e("address = ", errorMessage + ". " +
                    "Latitude = " + latLng.latitude +
                    ", Longitude = " + latLng.longitude, illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "no_address_found";
                Log.e("address = ", errorMessage);
            }
        } else {
            Address address = addresses.get(0);
            // Fetch the address lines using {@code getAddressLine},
            // join them, and send them to the thread. The {@link android.location.address}
            // class provides other options for fetching address details that you may prefer
            // to use. Here are some examples:
            // getLocality() ("Mountain View", for example)
            // getAdminArea() ("CA", for example)
            // getPostalCode() ("94043", for example)
            // getCountryCode() ("US", for example)
            // getCountryName() ("United States", for example)
            String allAddress = "";
/*
            for (int i = 0; i < addresses.size(); i++) {
                allAddress += address.getAddressLine(i) + " ";
            }
*/
            if (address.getAddressLine(0) != null || TextUtils.isEmpty(address.getAddressLine(0))) {
                allAddress += address.getAddressLine(0) + " ";
            } else {
                if (address.getFeatureName() != null) {
                    allAddress += address.getFeatureName() + " ";
                } else {
//                allAddress  = "";
                }
                if (address.getSubLocality() != null) {
                    allAddress += address.getSubLocality() + " ";
                } else {
//                allAddress  = "";
                }
                if (address.getLocality() != null) {
                    allAddress += address.getLocality() + " ";
                } else {
//                allAddress  = "";
                }
                if (address.getAdminArea() != null) {
                    allAddress += address.getAdminArea() + " ";
                } else {
//                allAddress  = "";
                }
                if (address.getPostalCode() != null) {
                    allAddress += address.getPostalCode() + " ";
                } else {
//                allAddress = "";
                }
            }
            Log.i("address = ", "address_found");
            //driverAddress = TextUtils.join(System.getProperty("line.separator"), addressFragments);
            cAddress = allAddress;
            Log.e("result", cAddress.toString());
        }
        return cAddress;
    }

    public static String getAddress(Context context, Location latLng) {
        String cAddress = "";
        String errorMessage = "";
        if (latLng == null) {
            errorMessage = "no_location_data_provided";
            Log.wtf("address = ", errorMessage);
            return "";
        }
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    Double.parseDouble(String.valueOf(formatter.format(latLng.getLatitude()))),
                    Double.parseDouble(String.valueOf(formatter.format(latLng.getLongitude()))),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "service_not_available";
            Log.e("address = ", errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "invalid_lat_long_used";
            Log.e("address = ", errorMessage + ". " +
                    "Latitude = " + latLng.getLatitude() +
                    ", Longitude = " + latLng.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "no_address_found";
                Log.e("address = ", errorMessage);
            }
        } else {
            Address address = addresses.get(0);
            // Fetch the address lines using {@code getAddressLine},
            // join them, and send them to the thread. The {@link android.location.address}
            // class provides other options for fetching address details that you may prefer
            // to use. Here are some examples:
            // getLocality() ("Mountain View", for example)
            // getAdminArea() ("CA", for example)
            // getPostalCode() ("94043", for example)
            // getCountryCode() ("US", for example)
            // getCountryName() ("United States", for example)
            String allAddress = "";
/*
            for (int i = 0; i < addresses.size(); i++) {
                allAddress += address.getAddressLine(i) + " ";
            }
*/
            if (address.getAddressLine(0) != null || TextUtils.isEmpty(address.getAddressLine(0))) {
                allAddress += address.getAddressLine(0) + " ";
            } else {
                if (address.getFeatureName() != null) {
                    allAddress += address.getFeatureName() + " ";
                } else {
//                allAddress  = "";
                }
                if (address.getSubLocality() != null) {
                    allAddress += address.getSubLocality() + " ";
                } else {
//                allAddress  = "";
                }
                if (address.getLocality() != null) {
                    allAddress += address.getLocality() + " ";
                } else {
//                allAddress  = "";
                }
                if (address.getAdminArea() != null) {
                    allAddress += address.getAdminArea() + " ";
                } else {
//                allAddress  = "";
                }
                if (address.getPostalCode() != null) {
                    allAddress += address.getPostalCode() + " ";
                } else {
//                allAddress = "";
                }
            }
            Log.i("address = ", "address_found");
            //driverAddress = TextUtils.join(System.getProperty("line.separator"), addressFragments);
            cAddress = allAddress;
            Log.e("result", cAddress.toString());
        }
        return cAddress;
    }
}
