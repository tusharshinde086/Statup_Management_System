package com.example.startupmanagementsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.List;
import java.util.stream.Collectors;

public class TaskAdapter extends BaseAdapter {
    private final Context context;
    private List<Task> taskList;
    private final EmployeeDatabase employeeDatabase;

    public TaskAdapter(Context context, List<Task> taskList, EmployeeDatabase employeeDatabase) {
        this.context = context;
        this.taskList = (taskList != null) ? taskList : List.of();
        this.employeeDatabase = employeeDatabase;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Task getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_row, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bind(getItem(position));
        return convertView;
    }

    /** ViewHolder class for performance optimization */
    private class ViewHolder {
        private final TextView taskNameTextView, assignedEmployeesTextView, progressPercentageTextView;
        private final Spinner taskPrioritySpinner, taskStatusSpinner;
        private final SeekBar taskProgressBar;
        private Task currentTask;

        ViewHolder(View view) {
            taskNameTextView = view.findViewById(R.id.task_name);
            assignedEmployeesTextView = view.findViewById(R.id.task_assign);
            taskPrioritySpinner = view.findViewById(R.id.task_priority_spinner);
            taskStatusSpinner = view.findViewById(R.id.task_status_spinner);
            taskProgressBar = view.findViewById(R.id.task_progress_bar);
            progressPercentageTextView = view.findViewById(R.id.task_progress_percentage);

            setupSeekBarListener();
        }

        void bind(Task task) {
            this.currentTask = task;

            taskNameTextView.setText(task.getTaskName());
            assignedEmployeesTextView.setText(getEmployeeNames(task.getAssignedEmployeeIds()));

            // Set progress bar and text
            taskProgressBar.setTag(task);
            taskProgressBar.setProgress(task.getProgress());
            progressPercentageTextView.setText(task.getProgress() + "%");
        }

        /** Updates task progress in UI and database */
        private void setupSeekBarListener() {
            taskProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && currentTask != null) {
                        currentTask.setProgress(progress);
                        progressPercentageTextView.setText(progress + "%");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    notifyDataSetChanged(); // Ensures UI updates when the progress is changed
                }
            });
        }

        /** Returns assigned employee names as a comma-separated string */
        private String getEmployeeNames(List<Integer> employeeIds) {
            if (employeeIds == null || employeeIds.isEmpty()) return "No assigned employees";

            return employeeIds.stream()
                    .map(id -> {
                        Employee employee = employeeDatabase.getEmployeeById(id);
                        return (employee != null) ? employee.getName() : null;
                    })
                    .filter(name -> name != null)
                    .collect(Collectors.joining(", "));
        }
    }

    /** Filters tasks assigned to a specific employee */
    public List<Task> getTasksForEmployee(int employeeId) {
        return taskList.stream()
                .filter(task -> task.getAssignedEmployeeIds().contains(employeeId))
                .collect(Collectors.toList());
    }

    /** Updates task list and refreshes UI */
    public void updateTaskList(List<Task> newTaskList) {
        this.taskList = (newTaskList != null) ? newTaskList : List.of();
        notifyDataSetChanged();
    }
}
