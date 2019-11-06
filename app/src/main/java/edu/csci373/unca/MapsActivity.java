package edu.csci373.unca;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private LatLng userLocation;
    private FirebaseFirestore db;
    private CollectionReference mFences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get user location
        userLocation = getIntent().getExtras().getParcelable("userLocation");

        // Set up firebase connection
        db = FirebaseFirestore.getInstance();
        mFences = db.collection("geofences");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Add a marker where user is
        mMap.addMarker(new MarkerOptions().position(userLocation))
                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));




        // Add geofences from firebase
        mFences.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docList = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : docList) {
                        double lat = doc.getDouble("lat");
                        double lon = doc.getDouble("lon");
                        LatLng location = new LatLng(lat, lon);
                        mMap.addCircle(new CircleOptions()
                                .center(location)
                                .radius(200)
                                .strokeColor(Color.BLUE)
                                .fillColor(0x220000FF)
                                .strokeWidth(5.0f));

                        Log.d(TAG, "Geofence at Latitude: " + lat + " Longitude: " + lon);


                    }
                }
            }
        });

    }

}
