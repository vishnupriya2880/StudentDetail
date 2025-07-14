package com.example.student;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "📡 GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        if (event.hasError()) {
            Log.e(TAG, "❌ Geofence error: " + event.getErrorCode());
            return;
        }

        int transition = event.getGeofenceTransition();
        Log.d(TAG, "📥 Geofence BroadcastReceiver triggered!");

        // Get this device's ID
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "📱 Current Device ID: " + deviceId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Match student by deviceId
        db.collection("students")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Log.e(TAG, "🚫 No student found with this device ID");
                        return;
                    }

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Log.d(TAG, "👤 Found student: " + doc.getString("name"));

                        boolean isEntering = (transition == Geofence.GEOFENCE_TRANSITION_ENTER);
                        boolean isExiting = (transition == Geofence.GEOFENCE_TRANSITION_EXIT);

                        if (isEntering) {
                            Toast.makeText(context, "You entered the school area!", Toast.LENGTH_LONG).show();
                            doc.getReference().update("present", true)
                                    .addOnSuccessListener(unused -> Log.d(TAG, "📡 Firestore updated: present = true"))
                                    .addOnFailureListener(e -> Log.e(TAG, "❌ Failed to update Firestore", e));
                        } else if (isExiting) {
                            Toast.makeText(context, "You exited the school area!", Toast.LENGTH_LONG).show();
                            doc.getReference().update("present", false)
                                    .addOnSuccessListener(unused -> Log.d(TAG, "📡 Firestore updated: present = false"))
                                    .addOnFailureListener(e -> Log.e(TAG, "❌ Failed to update Firestore", e));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "❌ Firestore query failed", e));
    }
}
