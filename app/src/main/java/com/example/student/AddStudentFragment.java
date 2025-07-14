package com.example.student;

import android.os.Bundle;
import android.provider.Settings;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddStudentFragment extends Fragment {

    private TextInputLayout nameL, emailL, ageL;
    private TextInputEditText nameIn, emailIn, ageIn;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_student, container, false);

        nameL = v.findViewById(R.id.nameLayout);
        emailL = v.findViewById(R.id.emailLayout);
        ageL = v.findViewById(R.id.ageLayout);

        nameIn = v.findViewById(R.id.nameInput);
        emailIn = v.findViewById(R.id.emailInput);
        ageIn = v.findViewById(R.id.ageInput);

        db = FirebaseFirestore.getInstance();

        v.findViewById(R.id.saveBtn).setOnClickListener(view -> saveStudent());

        return v;
    }

    private void saveStudent() {
        String name = Objects.requireNonNull(nameIn.getText()).toString().trim();
        String email = Objects.requireNonNull(emailIn.getText()).toString().trim();
        String ageStr = Objects.requireNonNull(ageIn.getText()).toString().trim();

        if (name.isEmpty() || email.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(getContext(), "Fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailL.setError("Invalid email");
            return;
        }

        int age = Integer.parseInt(ageStr);

        // Get this device's unique ID
        String deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("age", age);
        data.put("present", false);
        data.put("deviceId", deviceId);

        db.collection("students").add(data)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(getContext(), "Student added!", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
