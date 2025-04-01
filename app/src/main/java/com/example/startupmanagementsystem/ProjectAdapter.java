package com.example.startupmanagementsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    private final Context context;
    private final List<Project> projectList;

    public ProjectAdapter(Context context, List<Project> projectList) {
        this.context = context;
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.projectNameTextView.setText(project.getName());
        holder.projectDescriptionTextView.setText(project.getDescription());
        holder.projectStartTimeTextView.setText(String.valueOf(project.getStartDate()));
        holder.projectFinishTimeTextView.setText(String.valueOf(project.getEndDate()));
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView projectNameTextView;
        final TextView projectDescriptionTextView;
        final TextView projectStartTimeTextView;
        final TextView projectFinishTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            projectNameTextView = itemView.findViewById(R.id.projectNameTextView);
            projectDescriptionTextView = itemView.findViewById(R.id.projectDescriptionTextView);
            projectStartTimeTextView = itemView.findViewById(R.id.projectStartTimeTextView);
            projectFinishTimeTextView = itemView.findViewById(R.id.projectFinishTimeTextView);
        }
    }
}
