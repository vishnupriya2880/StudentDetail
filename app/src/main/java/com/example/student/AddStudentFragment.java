package com.example.student;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.UUID;

public class AddStudentFragment extends Fragment {

    private TextInputLayout nameL, emailL, ageL;

    private TextInputEditText nameIn, emailIn, ageIn;
    private FirebaseFirestore db;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_student, container, false);

        nameL  = v.findViewById(R.id.nameLayout);
        emailL = v.findViewById(R.id.emailLayout);
        ageL   = v.findViewById(R.id.ageLayout);

        nameIn  = v.findViewById(R.id.nameInput);
        emailIn = v.findViewById(R.id.emailInput);
        ageIn   = v.findViewById(R.id.ageInput);

        db = FirebaseFirestore.getInstance();

        v.findViewById(R.id.saveBtn).setOnClickListener(view -> saveStudent());
        return v;
    }

    private void saveStudent() {
        // clear previous errors
        nameL.setError(null); emailL.setError(null); ageL.setError(null);

        String name  = Objects.requireNonNull(nameIn.getText()).toString().trim();
        String email = Objects.requireNonNull(emailIn.getText()).toString().trim();
        String ageStr= Objects.requireNonNull(ageIn.getText()).toString().trim();

        boolean valid = true;

        if (name.isEmpty())  { nameL.setError("Required"); valid = false; }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailL.setError("Valid email required"); valid = false; }
        int age = 0;
        try { age = Integer.parseInt(ageStr); }
        catch (NumberFormatException e) { ageL.setError("Age?"); valid = false; }

        if (!valid) return;

        Student s = new Student(UUID.randomUUID().toString(), name, email, age);

        db.collection("students").document(s.getId())
                .set(s)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack(); // return to list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }
}
