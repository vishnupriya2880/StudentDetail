package com.example.student;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        if (event.hasError()) {
            Log.e(TAG, "❌ Geofence error: " + event.getErrorCode());
            return;
        }

        int transition = event.getGeofenceTransition();

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d(TAG, "✅ Entered geofence area");
            Toast.makeText(context, "You entered the school area!", Toast.LENGTH_LONG).show();

            context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("present", true)
                    .apply();

        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d(TAG, "🚪 Exited geofence area");
            Toast.makeText(context, "You exited the school area!", Toast.LENGTH_LONG).show();

            context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("present", false)
                    .apply();

        } else {
            Log.d(TAG, "🔄 Other geofence transition: " + transition);
        }
    }
}
