package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText email;
    private EditText password;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.button);
        email = findViewById(R.id.editTextText);
        password = findViewById(R.id.editTextText2);
        auth = FirebaseAuth.getInstance();
        FireBaseHandler f = new FireBaseHandler(auth,this);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("STUDENTS").child("studentId").setValue("studentsInfo");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sEmail = email.getText().toString();
                String sPassword = password.getText().toString();
                f.SignIn(sEmail,sPassword);



            }

        });


    }

    public void onClickToLid(View view) {
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }
    public void OnclickToHome(View view) {
        Intent intent = new Intent(this, Breath.class);
        startActivity(intent);
    }

    public void onClickToHome(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}