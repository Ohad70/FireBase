package com.example.firebase;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class RunFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean isTracking = false;
    private List<LatLng> pathPoints = new ArrayList<>();
    private long startTime = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private TextView timeTextView, distanceTextView, speedTextView;
    private Button startButton;
    private float totalDistance = 0;
    private Location lastLocation = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        timeTextView = view.findViewById(R.id.timeTextView);
        distanceTextView = view.findViewById(R.id.distanceTextView);
        speedTextView = view.findViewById(R.id.speedTextView);
        startButton = view.findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> toggleTracking());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null || !isTracking) return;
                for (Location location : locationResult.getLocations()) {
                    updatePath(location);
                }
            }
        };
        return inflater.inflate(R.layout.fragment_running_log, container, false);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void toggleTracking() {
        if (isTracking) {
            isTracking = false;
            startButton.setText("Start Running");
            fusedLocationClient.removeLocationUpdates(locationCallback);
            timerHandler.removeCallbacks(timerRunnable);
        } else {
            isTracking = true;
            pathPoints.clear();
            totalDistance = 0;
            lastLocation = null;
            startTime = SystemClock.elapsedRealtime();
            startButton.setText("Stop Running");

            requestLocationUpdates();
            startTimer();
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updatePath(Location location) {
        LatLng newPoint = new LatLng(location.getLatitude(), location.getLongitude());
        pathPoints.add(newPoint);

        if (mMap != null) {
            mMap.addPolyline(new PolylineOptions().addAll(pathPoints).color(0xFFFF0000).width(8));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPoint, 17));
        }

        if (lastLocation != null) {
            totalDistance += lastLocation.distanceTo(location);
        }
        lastLocation = location;

        updateStats();
    }

    private void updateStats() {
        long elapsedTime = (SystemClock.elapsedRealtime() - startTime) / 1000;
        double speed = totalDistance / elapsedTime;
        timeTextView.setText("Time: " + elapsedTime + " sec");
        distanceTextView.setText("Distance: " + String.format("%.2f", totalDistance) + " m");
        speedTextView.setText("Avg Speed: " + String.format("%.2f", speed) + " m/s");
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                updateStats();
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.postDelayed(timerRunnable, 1000);
    }



}