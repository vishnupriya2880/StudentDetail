package com.example.student;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.imageview.ShapeableImageView;

public class StudentDetailFragment extends Fragment {

    public static StudentDetailFragment newInstance(Student student) {
        StudentDetailFragment fragment = new StudentDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("student", student);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_detail, container, false);

        // ✅ Correct way to access SharedPreferences in a Fragment
        SharedPreferences prefs = requireContext().getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE);
        boolean present = prefs.getBoolean("present", false);

        // 🧑 Get student object from args
        Student student = (Student) getArguments().getSerializable("student");

        // 🖊️ Set values to text views
        ((TextView) view.findViewById(R.id.detail_name)).setText("Name: " + student.getName());
        ((TextView) view.findViewById(R.id.detail_email)).setText("Email: " + student.getEmail());
        ((TextView) view.findViewById(R.id.detail_age)).setText("Age: " + student.getAge());
        ((TextView) view.findViewById(R.id.detail_isPresent)).setText("isPresent: " + (present ? "Yes " : "No "));
        ShapeableImageView imageView = view.findViewById(R.id.profileImage);
        imageView.setImageResource(R.drawable.profile); // replace with your image


        return view;
    }
}
