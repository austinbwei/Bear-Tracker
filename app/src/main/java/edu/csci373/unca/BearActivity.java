package edu.csci373.unca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BearActivity extends AppCompatActivity {

    private static final String TAG = "BearActivity";
    private static final int PERMISSION_REQUEST = 1;
    private FusedLocationProviderClient client;
    private FirebaseFirestore db;
    private Button mAddFence;
    private Button mShowMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        mAddFence = (Button) findViewById(R.id.bear_spotted);
        mAddFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Adding bear location");

                checkPermissions();
                bearSpotted();
            }
        });

        mShowMap = (Button) findViewById(R.id.view_map);
        mShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Opening map");

                checkPermissions();
                client.getLastLocation().addOnSuccessListener(BearActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                            Log.d(TAG, "User at Latitude: " + lat + " Longitude: " + lon);

                            Intent intent = new Intent(BearActivity.this, MapsActivity.class);
                            intent.putExtra("userLocation", userLocation);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    /**
     * Acquire user location and add their location as a geofence to firebase
     */
    private void bearSpotted() {
        // Add location to firebase
        client.getLastLocation().addOnSuccessListener(BearActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    DocumentReference newFenceRef = db.collection("geofences").document();

                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    GeoFence geoFence = new GeoFence();
                    geoFence.setLat(lat);
                    geoFence.setLon(lon);

                    Log.d(TAG, "Bear spotted at Latitude: " + lat + " Longitude: " + lon);

                    newFenceRef.set(geoFence).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(BearActivity.this, R.string.success_toast, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BearActivity.this, R.string.fail_toast, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        Log.d(TAG, "Requesting permissions");

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
    }

}
