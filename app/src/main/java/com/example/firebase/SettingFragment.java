package com.example.firebase;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class SettingFragment extends Fragment {

    private FireBaseHandler fireBaseHandler;
    private EditText editTextNewPassword;
    private Button buttonChangePassword, buttonLogout, buttonDeleteAccount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        buttonDeleteAccount = view.findViewById(R.id.buttonDeleteAccount);

        fireBaseHandler = new FireBaseHandler(FirebaseAuth.getInstance(), requireContext());

        buttonChangePassword.setOnClickListener(v -> {
            String newPassword = editTextNewPassword.getText().toString().trim();
            if (TextUtils.isEmpty(newPassword)) {
                Toast.makeText(getContext(), "Please enter a new password", Toast.LENGTH_SHORT).show();
            } else if (newPassword.length() < 6) {
                Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                fireBaseHandler.changePassword(newPassword);
                editTextNewPassword.setText("");
            }
        });

        buttonLogout.setOnClickListener(v -> fireBaseHandler.logout());

        buttonDeleteAccount.setOnClickListener(v -> {
            // אם תרצה, אפשר להוסיף פה אישור דיאלוג לפני מחיקה
            fireBaseHandler.deleteAccount();
        });

        return view;
    }
}
