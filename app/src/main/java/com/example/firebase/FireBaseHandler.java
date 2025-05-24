package com.example.firebase;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FireBaseHandler {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef = database.getReference();
    private static FirebaseAuth auth;
    private static Context context;

    public FireBaseHandler(FirebaseAuth auth, Context context) {
        this.auth = auth;
        this.context = context;
    }

    public void SignIn(String sEmail, String sPassword) {
        if (TextUtils.isEmpty(sEmail) || TextUtils.isEmpty(sPassword)) {
            Toast.makeText(context, "please try again", Toast.LENGTH_SHORT).show();
        } else {
            auth.signInWithEmailAndPassword(sEmail, sPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(context, "you are genius", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    public void registerUser(String rEmail, String rPassword) {
        if (TextUtils.isEmpty(rEmail) || TextUtils.isEmpty(rPassword)) {
            Toast.makeText(context, "please try again", Toast.LENGTH_SHORT).show();
        } else {
            auth.createUserWithEmailAndPassword(rEmail, rPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(context, "you are genius", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("FireBaseHandler", "Email or password is empty");
                }
            });
        }
    }

    public static void loadRunRecords(OnRunRecordsLoadedListener listener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Log.e("FireBaseHandler", "No user signed in");
            listener.onRunRecordsLoaded(Collections.emptyList());
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("runs").child(uid);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RunRecord> tempList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    RunRecord run = data.getValue(RunRecord.class);
                    if (run != null) {
                        tempList.add(run);
                    }
                }
                Collections.reverse(tempList);
                listener.onRunRecordsLoaded(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FireBaseHandler", "Failed to load data", error.toException());
            }
        });
    }

    public static void saveRunRecordForCurrentUser(RunRecord runRecord) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Log.e("FireBaseHandler", "No user signed in");
            return;
        }
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference runsRef = FirebaseDatabase.getInstance().getReference("runs").child(uid);
        String runId = runsRef.push().getKey();
        if (runId != null) {
            runsRef.child(runId).setValue(runRecord)
                    .addOnSuccessListener(aVoid -> Log.d("FireBaseHandler", "Run saved successfully"))
                    .addOnFailureListener(e -> Log.e("FireBaseHandler", "Failed to save run", e));
        }
    }





    public interface OnRunRecordsLoadedListener {
        void onRunRecordsLoaded(List<RunRecord> runRecords);
    }
}
