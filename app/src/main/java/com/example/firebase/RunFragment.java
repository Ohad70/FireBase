package com.example.firebase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
public class RunFragment extends Fragment {

    private MapView mapView;
    private IMapController mapController;
    private FusedLocationProviderClient fusedLocationClient;
    private List<GeoPoint> routePoints = new ArrayList<>();
    private Polyline routeLine;

    // Initialize map
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run, container, false);

        // Initialize OSMDroid
        Configuration.getInstance().setUserAgentValue(getActivity().getApplicationContext().getPackageName());

        // Initialize Map
        mapView = view.findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(18.0);

        // Initialize route line and add to overlays
        routeLine = new Polyline();
        routeLine.setColor(android.graphics.Color.RED);
        mapView.getOverlays().add(routeLine);

        // Start tracking location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        startLocationUpdates();

        return view;
    }

    // Start location updates
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)  // 5 seconds
                .setFastestInterval(2000)  // 2 seconds
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    updateMap(location);
                }
            }
        };

        // Check for location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // Update map with new location
    private void updateMap(Location location) {
        // Ensure the routeLine is initialized before using it
        if (routeLine == null) {
            routeLine = new Polyline();
            routeLine.setColor(android.graphics.Color.RED);
            mapView.getOverlays().add(routeLine);
        }

        GeoPoint newPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        routePoints.add(newPoint);

        if (routePoints.size() == 1) {
            mapController.setCenter(newPoint);  // Move map to first location
        }

        routeLine.setPoints(routePoints);  // Update route line
        mapView.invalidate();  // Redraw map with new data
    }
}
