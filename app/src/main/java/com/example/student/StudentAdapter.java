package com.example.student;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private final List<Student> list;
    private final StudentListFragment.OnStudentSelectedListener listener;

    public StudentAdapter(List<Student> list, StudentListFragment.OnStudentSelectedListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, badge;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.student_name);
            badge = view.findViewById(R.id.presentBadge);
        }

        public void bind(Student student, StudentListFragment.OnStudentSelectedListener listener) {
            name.setText(student.getName());

            // Show or hide the presence badge
            if (student.isPresent()) {
                badge.setVisibility(View.VISIBLE);
                badge.setText("✓ Present");
                badge.setTextColor(itemView.getResources().getColor(android.R.color.holo_green_dark));
            } else {
                badge.setVisibility(View.GONE);
            }

            // ✅ Save selected student ID and notify listener
            itemView.setOnClickListener(v -> {
                listener.onStudentSelected(student);

                v.getContext()
                        .getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE)
                        .edit()
                        .putString("studentId", student.getId())
                        .apply();
            });
        }


    }
        @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
