package com.example.firebase;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseHandler
{
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef = database.getReference();
    private static FirebaseAuth auth;
    private static Context context;

    public FireBaseHandler(FirebaseAuth auth, Context context){
        this.auth = auth;
        this.context = context;
    }

        public void SignIn(String sEmail, String sPassword){

            if(TextUtils.isEmpty(sEmail)||TextUtils.isEmpty(sPassword))
                {
                Toast.makeText(context, "you are acoustic", Toast.LENGTH_SHORT).show();
             }
            else {
                auth.signInWithEmailAndPassword(sEmail, sPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(context, "you are genius+", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }
            public void registerUser (String rEmail, String rPassword){
                if (TextUtils.isEmpty(rEmail) || TextUtils.isEmpty(rPassword)) {
                    Toast.makeText(context, "you are acoustic", Toast.LENGTH_SHORT).show();
                } else {
                    auth.createUserWithEmailAndPassword(rEmail, rPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(context, "you are genius+", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                            Log.e("FireBaseHandler", "Email or password is empty");
                        }
                    });

                }
            }
}

