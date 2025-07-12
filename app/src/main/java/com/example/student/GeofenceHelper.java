package com.example.student;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

public class GeofenceHelper {

    private static final String TAG = "GeofenceHelper";
    private static final String GEOFENCE_ID = "school_area";

    private final GeofencingClient geofencingClient;
    private final Context context;

    // Set your school coordinates
    private final double LATITUDE =11.8672772;  // Example: Google HQ
    private final double LONGITUDE =79.7985143;
    private final float RADIUS = 1; // meters

    public GeofenceHelper(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
    }

    public void addSchoolGeofence() {

        // ✅ Check permissions using ContextCompat
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "❌ Location permission not granted");
            return;
        }

        // ✅ Log current location (for debugging)
        FusedLocationProviderClient fused = LocationServices.getFusedLocationProviderClient(context);
        fused.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Log.d("LOCATION", "📍 Current location: " + location.getLatitude() + ", " + location.getLongitude());
            } else {
                Log.w("LOCATION", "⚠️ Current location is null");
            }
        });

        // ✅ Create geofence
        Geofence geofence = new Geofence.Builder()
                .setRequestId(GEOFENCE_ID)
                .setCircularRegion(LATITUDE, LONGITUDE, RADIUS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        // ✅ Create geofence request
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();

        // ✅ Add geofence
        geofencingClient.addGeofences(geofencingRequest, getPendingIntent())
                .addOnSuccessListener(unused -> Log.d(TAG, "✅ Geofence added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "❌ Failed to add geofence: " + e.getMessage()));
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getBroadcast(
                context,
                0,
                getIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );
    }

    private Intent getIntent() {
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        intent.setAction("com.example.student.GEOFENCE_TRANSITION");
        return intent;
    }
}
