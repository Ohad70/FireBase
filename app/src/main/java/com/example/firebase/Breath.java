package com.example.firebase;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Breath extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView textPhase;
    private Button btnStart;
    private TextView timerText;

    private Handler handler;
    private int currentProgress = 0;
    private int phaseIndex = 0;
    private int[] phaseDurations = {4000, 2000, 4000};  // Inhale, Hold, Exhale durations in milliseconds
    private String[] phaseNames = {"Inhale", "Hold", "Exhale"};
    private int remainingTime;  // Remaining time for the current phase in seconds
    private boolean isPhaseRunning = false;  // To prevent multiple starts
    private int totalElapsedTime = 0;  // Total time elapsed in seconds
    private final int totalCycleTime = 60;  // One minute (60 seconds)
    private int phaseDurationInSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_breath);

        progressBar = findViewById(R.id.circularProgressBar);
        textPhase = findViewById(R.id.textPhase);
        btnStart = findViewById(R.id.btnStart);
        timerText = findViewById(R.id.timerText);

        handler = new Handler();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnStart.setOnClickListener(v -> {
            if (!isPhaseRunning) {
                startBreathingExercise();
            }
        });
    }

    private void startBreathingExercise() {
        btnStart.setEnabled(false);  // Disable the start button to avoid multiple starts
        isPhaseRunning = true;  // Indicate that the phase has started
        phaseIndex = 0;
        totalElapsedTime = 0;  // Reset total elapsed time
        updatePhase();
    }

    private void updatePhase() {
        // Update the current phase text
        textPhase.setText(phaseNames[phaseIndex]);

        // Reset progress bar
        progressBar.setProgress(0);
        currentProgress = 100;

        // Get the phase duration in milliseconds
        final int duration = phaseDurations[phaseIndex];
        phaseDurationInSeconds = duration / 1000;  // Convert milliseconds to seconds
        remainingTime = phaseDurationInSeconds;

        // Update the progress bar and start the countdown
        startProgressBar(duration);

        // Start the countdown timer
        startTimer();
    }

    private void startProgressBar(final int duration) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentProgress < 100) {
                    currentProgress++;
                    progressBar.setProgress(currentProgress);
                    handler.postDelayed(this, duration / 100);  // Repeat until 100%
                }
            }
        }, 0);
    }

    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (remainingTime > -1) {
                    // Format the time as MM:SS
                    timerText.setText(String.format("%02d:%02d", remainingTime / 60, remainingTime % 60));
                    remainingTime--;
                    handler.postDelayed(this, 1000);  // Update every second
                } else {
                    // Timer complete, move to the next phase
                    phaseIndex = (phaseIndex + 1) % 3;  // Cycle between 3 phases (Inhale, Hold, Exhale)
                    totalElapsedTime += phaseDurations[(phaseIndex - 1 + 3) % 3] / 1000;  // Add phase duration to elapsed time

                    if (totalElapsedTime >= totalCycleTime) {
                        // When total time reaches 60 seconds, stop the cycle
                        btnStart.setEnabled(true);  // Re-enable start button after full cycle time (1 minute)
                        isPhaseRunning = false;  // Reset the phase running flag
                    } else {
                        updatePhase();  // Continue to the next phase
                    }
                }
            }
        }, 1000);
    }
}

