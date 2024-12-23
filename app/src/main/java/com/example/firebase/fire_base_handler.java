package com.example.firebase;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class fire_base_handler
{
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef = database.getReference();
    private static FirebaseAuth auth;
    private static Context context;

    public fire_base_handler(FirebaseAuth auth, Context context){
        this.auth = auth;
        this.context = context;
    }

        public boolean SignIn(String sEmail, String sPassword){
            boolean JoinSuc = false;
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
                JoinSuc = true;
            }
            return JoinSuc;
        }
            public void registerUser (String rEmail, String rPassword, String rUsername){
                if (TextUtils.isEmpty(rEmail) || TextUtils.isEmpty(rPassword)) {
                    Toast.makeText(context, "you are acoustic", Toast.LENGTH_SHORT).show();
                } else {
                    auth.createUserWithEmailAndPassword(rEmail, rPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(context, "you are genius+", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
}

