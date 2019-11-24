package edu.csci373.unca;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private FirebaseFirestore db;
    private CollectionReference mFences;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Created");

        db = FirebaseFirestore.getInstance();
        mFences = db.collection("geofences");

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "User at Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
                searchFireBase(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "Status chanaged");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "Provider enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "Provider disabled");
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission not granted");
                return;
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, locationListener);   // Every 20sec
                Log.d(TAG, "Created location manager");

                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.d(TAG, "Location: " + location);
            }
        } else {
            Log.d(TAG, "Wrong version");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Searches firebase to find if user is within a geofence
     * Send notification if they are in a geofence
     * @param location of user
     */
    private void searchFireBase(Location location) {
        mFences.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docList = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : docList) {
                        double lat = doc.getDouble("lat");
                        double lon = doc.getDouble("lon");
                        double radius = doc.getDouble("radius");
                        Location geoLocation = new Location("");
                        geoLocation.setLatitude(lat);
                        geoLocation.setLongitude(lon);

                        Location newLocation = new Location("");
                        newLocation.setLatitude(lat);
                        newLocation.setLongitude(lon);


                        float distance = newLocation.distanceTo(geoLocation);

                        // Is within already existing geofence
                        
                        if (distance <= radius) {
                            Log.d(TAG, "Deleting document " + doc.getId());
                        }
                        Log.d(TAG, "Geofence at Latitude: " + lat + " Longitude: " + lon);
                    }
                    }
                }

        });


    }

    private void sendNotification() {










    }

}
