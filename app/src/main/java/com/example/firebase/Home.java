package com.example.firebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.firebase.databinding.ActivityHomeBinding;


public class Home extends AppCompatActivity {

    ActivityHomeBinding binding;
    private WifiReceiver wifiReceiver;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;




        });
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.run:
                    replaceFragment(new RunFragment());
                    break;
                case R.id.life:
                    replaceFragment(new LifeStyleFragment());
                    break;
                case R.id.running:
                    replaceFragment(new RunningLogFragment());
                    break;
                case R.id.settings:
                    replaceFragment(new SettingFragment());
                    break;


            }

            return true;
        });


    }

    private void replaceFragment(Fragment fragment) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

        // Check if the fragment is already added to avoid replacing it
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return;  // Avoid replacing the fragment if it's the same as the current one
        }

        // Create a fragment transaction to replace the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the current fragment
        fragmentTransaction.replace(R.id.frame_layout, fragment);

        // Optionally add to back stack for proper back navigation
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }

    public void onClickToHome(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Create and register the BroadcastReceiver to listen for connectivity changes
        wifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifiReceiver, filter);
    }









}