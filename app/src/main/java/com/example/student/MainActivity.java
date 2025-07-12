package com.example.student;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity
        implements StudentListFragment.OnStudentSelectedListener {

    private GeofenceHelper geofenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        geofenceHelper = new GeofenceHelper(this);

        // Request location permissions
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // ✅ Delay geofence creation after permissions are confirmed
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                geofenceHelper.addSchoolGeofence();
            }, 3000);

        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    1001
            );
        }

        // Load initial fragment
        if (savedInstanceState == null) {
            openFragment(new StudentListFragment(), false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int rq, @NonNull String[] perms,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(rq, perms, grantResults);
        if (rq == 1001 && grantResults.length > 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            // ✅ Delay geofence after permission granted
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                geofenceHelper.addSchoolGeofence();
            }, 3000);
        }
    }

    @Override
    public void onStudentSelected(Student student) {
        openFragment(StudentDetailFragment.newInstance(student), true);
    }

    private void openFragment(Fragment next, boolean addToBackStack) {
        FragmentTransaction tx = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                )
                .replace(R.id.fragment_container, next);

        if (addToBackStack) {
            tx.addToBackStack(null);
        }

        tx.commit();
    }
}
