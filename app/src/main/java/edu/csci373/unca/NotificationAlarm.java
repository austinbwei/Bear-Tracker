package edu.csci373.unca;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;
import java.util.concurrent.Executor;

public class NotificationAlarm extends BroadcastReceiver {

    private static final String TAG = "NotificationAlarm";
    private FirebaseFirestore db;
    private CollectionReference mFences;
    private LatLng userLocation;
    private int alarmID;
    private FusedLocationProviderClient client;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "Received alarm broadcast");

        alarmID = intent.getExtras().getInt("alarmID");
        client = (FusedLocationProviderClient) intent.getExtras().getSerializable("client");

        db = FirebaseFirestore.getInstance();
        mFences = db.collection("geofences");
        userLocation = getUserLocation();

        // Add geofences from firebase
        mFences.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docList = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : docList) {
                        double lat = doc.getDouble("lat");
                        double lon = doc.getDouble("lon");
                        double radius = doc.getDouble("radius");
                        LatLng location = new LatLng(lat, lon);
                        Log.d(TAG, "Geofence at Latitude: " + lat + " Longitude: " + lon);


                    }
                }
            }
        });
    }

    private LatLng getUserLocation() {
        final LatLng[] userLocation = {null};

        client.getLastLocation().addOnSuccessListener((Executor) this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    userLocation[0] = new LatLng(location.getLatitude(), location.getLongitude());

                    Log.d(TAG, "User at Latitude: " + lat + " Longitude: " + lon);
                }
            }
        });
        return userLocation[0];
    }

}
