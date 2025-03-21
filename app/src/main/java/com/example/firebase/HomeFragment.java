package com.example.firebase;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HomeFragment extends Fragment {

    private WifiReceiver wifiReceiver;
    

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
}



