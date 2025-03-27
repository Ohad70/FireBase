package com.example.firebase;

import static android.text.TextUtils.replace;

import android.app.AlertDialog;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import android.Manifest;




public class HomeFragment extends Fragment {

    private WifiReceiver wifiReceiver;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Create and register the BroadcastReceiver to listen for connectivity changes
        wifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(wifiReceiver, filter);  // Use getActivity() to access the context
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister the receiver when the fragment is paused
        if (wifiReceiver != null) {
            getActivity().unregisterReceiver(wifiReceiver);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if we have permission to access location
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            // Permission already granted
            // Proceed with using location
        }
    }

    private void requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(getContext())
                    .setMessage("This app requires location access to track your run. Please allow access.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Request permission
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // Permission denied by user
                    })
                    .show();
        } else {
            // Directly request the permission if no rationale is needed
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // Proceed with using location
                Toast.makeText(getContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}



