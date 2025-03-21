package com.example.firebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the current network status
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork == null || !activeNetwork.isConnected() || activeNetwork.getType() != ConnectivityManager.TYPE_WIFI) {
            // Wi-Fi is disconnected
            Log.d("WifiReceiver", "Wi-Fi is disconnected.");

            // Show an alert dialog to the user
            new AlertDialog.Builder(context)
                    .setTitle("Wi-Fi Disconnected")
                    .setMessage("You have been disconnected from Wi-Fi. Please check your connection.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            // Wi-Fi is connected
            Log.d("WifiReceiver", "Wi-Fi is connected.");
        }
    }
}


