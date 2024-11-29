package com.example.firebase;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth bush;
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
        bush = FirebaseAuth.getInstance();
        button = findViewById(R.id.button);
        email = findViewById(R.id.editTextText);
        password = findViewById(R.id.editTextText2);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("STUDENTS").child("studentId").setValue("studentsInfo");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sEmail = email.getText().toString();
                String sPassword = password.getText().toString();

                if(TextUtils.isEmpty(sEmail)||TextUtils.isEmpty(sPassword))
                {
                    Toast.makeText(MainActivity.this, "you are acoustic", Toast.LENGTH_SHORT).show();
                }
                else {
                    bush.signInWithEmailAndPassword(sEmail, sPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(MainActivity.this, "you are genius+", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //לא עושים פייר בייס במחלקה שהיא לא מחלקה משלו. חובה ליצור מחלקה בשם פייר בייס ולשים שם את כל הפקודות
            }
        });
    }
}