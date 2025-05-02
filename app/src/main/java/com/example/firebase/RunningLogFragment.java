package com.example.firebase;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RunningLogFragment extends Fragment {

    private RecyclerView recyclerView;
    private RunRecordAdapter adapter;
    private List<RunRecord> runList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout first
        View view = inflater.inflate(R.layout.fragment_running_log, container, false);

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.runRecordsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RunRecordAdapter(runList);
        recyclerView.setAdapter(adapter);

        // Load data from Firebase
        loadRunRecords();

        return view;
    }

    private void loadRunRecords() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("runs");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                runList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    RunRecord run = data.getValue(RunRecord.class);
                    if (run != null) {
                        runList.add(run);
                    }
                }
                Collections.reverse(runList); // Newest run at the top
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RunningRecords", "Failed to load data", error.toException());
            }
        });
    }
}
