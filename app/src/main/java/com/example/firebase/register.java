package com.example.firebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText email;
    private EditText password;
    private EditText username;
    private Button button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        button = findViewById(R.id.button2);
        email = findViewById(R.id.editTextText3);
        password = findViewById(R.id.editTextText4);
        //username = findViewById(R.id.editTextText5);
        auth = FirebaseAuth.getInstance();
        FireBaseHandler d = new FireBaseHandler(FirebaseAuth.getInstance(),this);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rEmail = String.valueOf(email.getText());
                String rPassword = String.valueOf(password.getText());
                //String rUserName = username.getText().toString();
                d.registerUser(rEmail,rPassword);

            }
        });

    }

    public void onClickToHub(View view) {
        Intent intent = new Intent(this , MainActivity.class);
        startActivity(intent);
    }
}