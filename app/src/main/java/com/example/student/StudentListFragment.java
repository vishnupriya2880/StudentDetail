package com.example.student;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import android.util.Log;
import android.view.*;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class StudentListFragment extends Fragment {

    private RecyclerView recyclerView;
    private OnStudentSelectedListener callback;

    public interface OnStudentSelectedListener {
        void onStudentSelected(Student student);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnStudentSelectedListener) {
            callback = (OnStudentSelectedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnStudentSelectedListener");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton addFab = view.findViewById(R.id.addFab);
        addFab.setOnClickListener(v -> {
            Log.d("StudentListFragment", "FAB clicked");
            Toast.makeText(getContext(), "Add clicked", Toast.LENGTH_SHORT).show();


            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AddStudentFragment())
                    .addToBackStack(null)
                    .commit();
        });

        loadStudents();

        return view;
    }

    private void loadStudents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("students")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Student> studentList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Student s = doc.toObject(Student.class);
                        studentList.add(s);
                    }

                    // ✅ ONLY create adapter once callback is guaranteed
                    recyclerView.setAdapter(new StudentAdapter(studentList, callback));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load students", Toast.LENGTH_SHORT).show();
                });
    }
}

