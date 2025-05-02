package com.example.firebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RunRecordAdapter extends RecyclerView.Adapter<RunRecordAdapter.RunViewHolder> {

    private List<RunRecord> runList;

    public RunRecordAdapter(List<RunRecord> runList) {
        this.runList = runList;
    }

    @NonNull
    @Override
    public RunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.run_record_item, parent, false);
        return new RunViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RunViewHolder holder, int position) {
        RunRecord run = runList.get(position);
        holder.dateTextView.setText("Date: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(run.timestamp)));
        holder.distanceTextView.setText("Distance: " + String.format("%.2f km", run.distance / 1000));
        holder.timeTextView.setText("Time: " + formatTime(run.time));
        holder.speedTextView.setText("Avg Speed: " + String.format("%.2f km/h", run.avgSpeed));
    }

    @Override
    public int getItemCount() {
        return runList.size();
    }

    static class RunViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, distanceTextView, timeTextView, speedTextView;

        RunViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            speedTextView = itemView.findViewById(R.id.speedTextView);
        }
    }

    private String formatTime(long timeMillis) {
        long minutes = timeMillis / 60000;
        long seconds = (timeMillis % 60000) / 1000;
        return minutes + "m " + seconds + "s";
    }
}
