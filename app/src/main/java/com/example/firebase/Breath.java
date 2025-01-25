package com.example.firebase;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Breath extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView textPhase;
    private Button btnStart;

    private Handler handler;
    private int currentProgress = 0;
    private int phaseIndex = 0;
    private int[] phaseDurations = {4000, 2000, 4000};  // Inhale, Hold, Exhale durations in milliseconds
    private String[] phaseNames = {"Inhale", "Hold", "Exhale"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_breath);

        progressBar = findViewById(R.id.circularProgressBar);
        textPhase = findViewById(R.id.textPhase);
        btnStart = findViewById(R.id.btnStart);

        handler = new Handler();



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBreathingExercise();
            }
        });
    }

    private void startBreathingExercise() {
        btnStart.setEnabled(false);  // Disable the start button to avoid multiple starts
        phaseIndex = 0;
        updatePhase();
    }

    private void updatePhase() {
        // Update text to show current phase
        textPhase.setText(phaseNames[phaseIndex]);

        // Reset progress bar
        progressBar.setProgress(0);
        currentProgress = 0;

        // Determine phase duration
        final int duration = phaseDurations[phaseIndex];
        final int increment = 100;

        // Update the progress bar in increments
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentProgress < 100) {
                    currentProgress++;
                    progressBar.setProgress(currentProgress);
                    handler.postDelayed(this, duration / 100);  // Repeat until 100%
                } else {
                    // Phase complete, move to next phase
                    phaseIndex = (phaseIndex + 1) % 3;  // Cycle between 3 phases
                    if (phaseIndex == 0) {
                        btnStart.setEnabled(true);  // Re-enable start button after full cycle
                    } else {
                        updatePhase();  // Continue with next phase
                    }
                }
            }
        }, 0);
    }
}