package com.example.sagiproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class RegistrationFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword, etName;
    private Button btnRegister;
    private Context context;

    public RegistrationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        etEmail = view.findViewById(R.id.etEmailAddress);
        etPassword = view.findViewById(R.id.etNumberPassword);
        etName = view.findViewById(R.id.etName);
        btnRegister = view.findViewById(R.id.btnRegister);
        this.context = getContext();

        btnRegister.setOnClickListener(v -> handleRegistration());
    }

    private void handleRegistration() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();

                        // אתחול הנתונים למשתמש החדש
                        FBsingleton fb = FBsingleton.getInstance(context);
                        fb.setName(name);
                        fb.setScore(0); // תיקון: הוספת 0 כפרמטר
                        fb.initializeDefaultWords(); // העלאת המילים הראשוניות

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);

                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    } else {
                        Toast.makeText(getContext(), "Registration Failed: " +
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
