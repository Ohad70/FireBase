package com.example.firebase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RunFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private List<LatLng> pathPoints = new ArrayList<>();
    private Polyline polyline;
    private boolean isRunning = false;
    private Location previousLocation;
    private float totalDistance = 0;
    private long startTime = 0;
    private long elapsedTime = 0;
    private Button startRunButton;
    private TextView distanceTextView, timeTextView, speedTextView;
    private float avgSpeed;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run, container, false);


        databaseReference = FirebaseDatabase.getInstance().getReference("runs");

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize UI components
        startRunButton = view.findViewById(R.id.startRunButton);
        distanceTextView = view.findViewById(R.id.distanceTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        speedTextView = view.findViewById(R.id.speedTextView);

        // Button click listener to start/stop the run
        startRunButton.setOnClickListener(v -> startStopRun());


        return view;
    }

    private void startStopRun() {
        if (!isRunning) {
            // Start the run
            isRunning = true;
            startRunButton.setText("Stop Run");
            startTime = System.currentTimeMillis();
            totalDistance = 0;
            previousLocation = null;
            pathPoints.clear();
            if (polyline != null) {
                polyline.remove();
            }
            startLocationUpdates();
        } else {
            // Stop the run
            isRunning = false;
            startRunButton.setText("Start Run");
            fusedLocationClient.removeLocationUpdates(locationCallback);
            saveRunRecord();
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Check Permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mMap.setMyLocationEnabled(true);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000); // Update every 1 seconds
        locationRequest.setFastestInterval(2000); // Fastest update every 2 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    if (isRunning) {
                        LatLng newPoint = new LatLng(location.getLatitude(), location.getLongitude());
                        pathPoints.add(newPoint);

                        // Calculate distance, time, and speed
                        updateStats(location);

                        // Update the map
                        updateMap(newPoint);
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void updateMap(LatLng newPoint) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPoint, 17));


        if (polyline != null) {
            polyline.remove();
        }
        polyline = mMap.addPolyline(new PolylineOptions()
                .addAll(pathPoints)
                .color(0xFFFF0000) // אדום
                .width(10f));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void updateStats(Location location) {
        if (previousLocation != null) {
            totalDistance += previousLocation.distanceTo(location);  // Add distance between locations
        }

        long currentTime = System.currentTimeMillis();
        elapsedTime = currentTime - startTime;  // Calculate elapsed time

        // Speed in meters per second, then convert to km/h
        float speed = location.getSpeed() * 3.6f;

        // Update the UI with the stats
        updateUI(totalDistance, elapsedTime, speed);

        // Update previous location for the next distance calculation
        previousLocation = location;
    }

    private void updateUI(float distance, long time, float speed) {
        // Convert distance to kilometers
        float distanceInKm = distance / 1000;

        // Format time as minutes:seconds
        long minutes = time / 60000;
        long seconds = (time % 60000) / 1000;

        // Update TextViews with stats
        distanceTextView.setText("Distance: " + String.format("%.2f", distanceInKm) + " km");
        timeTextView.setText("Time: " + minutes + ":" + String.format("%02d", seconds));
        speedTextView.setText("Speed: " + String.format("%.2f", speed) + " km/h");
    }

    private void saveRunRecord() {
        elapsedTime = System.currentTimeMillis() - startTime; // זמן הריצה במילישניות
        avgSpeed = (totalDistance / 1000) / (elapsedTime / 3600000f); // ממיר למהירות בקמ"ש

        RunRecord runRecord = new RunRecord(totalDistance, elapsedTime, avgSpeed, System.currentTimeMillis());

        // שמירת ריצה עם uid של המשתמש המחובר
        FireBaseHandler.saveRunRecordForCurrentUser(runRecord);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);
                        startLocationUpdates();
                    }
                }
            } else {
                Log.e("RunFragment", "Location permission denied");
            }
        }
    }
}