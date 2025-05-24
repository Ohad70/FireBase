package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class LifeStyleFragment extends Fragment {

    private TextView textPhase;
    private Button btnStart;
    private Button button6;  // כפתור למעבר לעמוד Breath
    private TextView timerText;
    private View breathingCircle;
    private TextView textView2;

    private Handler handler;
    private boolean isBreathMode = true;  // מצב התרגול מופעל בהתחלה
    private WebView webView;

    // שלבים וקביעות
    private final int INHALE = 0;
    private final int HOLD = 1;
    private final int EXHALE = 2;

    private final int[] phaseDurations = {4000, 2000, 4000};  // משכי שלבים במילישניות
    private final String[] phaseNames = {"Inhale", "Hold", "Exhale"};
    private final float[] scaleValues = {2f, 2f, 1f};         // הגדלה בשלבים

    private int currentPhase = INHALE;
    private boolean isPhaseRunning = false;

    // טיימר
    private int remainingTime;  // שניות שנשארו בכל שלב
    private int totalElapsedTime = 0;  // סך הזמן שעבר בשניות
    private final int totalCycleTime = 60;  // סה"כ דקה (60 שניות)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_life_style, container, false);

        // קישור אל ה־Views
        textPhase = view.findViewById(R.id.textPhase);
        btnStart = view.findViewById(R.id.btnStart);
        button6 = view.findViewById(R.id.button6);  // קישור לכפתור watch
        timerText = view.findViewById(R.id.timerText);
        breathingCircle = view.findViewById(R.id.breathingCircle);
        textView2 = view.findViewById(R.id.textView2);

        webView = view.findViewById(R.id.webView);

        // הפעלת JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // דפדפן פנימי
        webView.setWebViewClient(new WebViewClient());

        String videoHtml1 = "<iframe width=\"100%\" height=\"315\" src=\"https://www.youtube.com/embed/kpSkoXRrZnE?start=17\" " +
                "frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" " +
                "allowfullscreen></iframe>";

        String videoHtml2 = "<iframe width=\"100%\" height=\"315\" src=\"https://www.youtube.com/embed/Rt08wzTYKHg\" " +
                "frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" " +
                "allowfullscreen></iframe>";

        String videoHtml3 = "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/etfrYXSbEDc?si=urhzgZNaIW2ip_th\" " +
                "title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" " +
                "referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";

        String html = "<html><body style='margin:0;padding:0;'>" + videoHtml1 + "<br><br>" + videoHtml2 + "<br><br>" + videoHtml3 +"</body> </html>";

        webView.loadData(html, "text/html", "utf-8");

        handler = new Handler();

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnStart.setOnClickListener(v -> {
            if (!isPhaseRunning) {
                startBreathingExercise();
                handler.removeCallbacks(breathRunnable);
                handler.post(breathRunnable);
            }
        });

        button6.setOnClickListener(v -> {
            toggleMode();
        });

        return view;

    }

    // Runnable שמבצע את האנימציה לפי שלבים עם עצירה ב-Hold
    private final Runnable breathRunnable = new Runnable() {
        @Override
        public void run() {
            // עדכון טקסט השלב
            textPhase.setText(phaseNames[currentPhase]);

            // עדכון אנימציית הסקייל
            breathingCircle.animate()
                    .scaleX(scaleValues[currentPhase])
                    .scaleY(scaleValues[currentPhase])
                    .setDuration(phaseDurations[currentPhase])
                    .withEndAction(() -> {
                        // מעבר לשלב הבא במחזור
                        currentPhase = (currentPhase + 1) % 3;

                        // עצירה של חצי שניה בין השלבים
                        handler.postDelayed(this, 500);
                    })
                    .start();

            // עדכון הטיימר והזמן שנותר
            remainingTime = phaseDurations[currentPhase] / 1000;
            startTimer();
        }
    };

    private void startBreathingExercise() {
        btnStart.setEnabled(false);  // נטרול הכפתור למניעת לחיצות חוזרות
        isPhaseRunning = true;
        currentPhase = INHALE;
        totalElapsedTime = 0;
        remainingTime = phaseDurations[currentPhase] / 1000;
        textPhase.setText(phaseNames[currentPhase]);
        timerText.setText(String.format("00:00"));
    }

    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (remainingTime > 0 && isPhaseRunning) {
                    timerText.setText(String.format("%02d:%02d", remainingTime / 60, remainingTime % 60));
                    remainingTime--;
                    handler.postDelayed(this, 1000);
                } else {
                    // השלב נגמר
                    totalElapsedTime += phaseDurations[currentPhase] / 1000;

                    if (totalElapsedTime >= totalCycleTime) {
                        // סיום התרגיל
                        btnStart.setEnabled(true);
                        isPhaseRunning = false;
                        timerText.setText("00:00");
                        textPhase.setText("Done");
                        breathingCircle.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(500)
                                .start();
                        handler.removeCallbacks(breathRunnable);
                    }
                }
            }
        }, 0);
    }

    private void toggleMode() {
        isBreathMode = !isBreathMode;

        if (isBreathMode) {
            webView.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
            textPhase.setVisibility(View.VISIBLE);
            timerText.setVisibility(View.VISIBLE);
            breathingCircle.setVisibility(View.VISIBLE);
            button6.setText("Watch");
            textView2.setText("YouTube Guides:");
        } else {
            webView.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.GONE);
            textPhase.setVisibility(View.GONE);
            timerText.setVisibility(View.GONE);
            breathingCircle.setVisibility(View.GONE);
            button6.setText("Exercises");
            textView2.setText("Breathing Exercises:"); // ← שורת השינוי החשוב
        }
    }

}
