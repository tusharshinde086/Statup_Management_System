package com.example.startupmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;
import java.util.stream.Collectors;

public class TaskStatusActivity extends AppCompatActivity {

    private TableLayout taskTable;
    private DBHelper dbHelper;
    private EmployeeDatabase employeeDatabase;
    private Map<Integer, String> employeeMap = new HashMap<>(); // Employee Cache

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_status);

        // Initialize UI components
        taskTable = findViewById(R.id.task_table);
        dbHelper = new DBHelper(this);
        employeeDatabase = new EmployeeDatabase(this);

        // Load employee names and tasks
        cacheEmployeeNames();
        loadTasks();
    }

    /** Caches Employee Names to minimize database queries */
    private void cacheEmployeeNames() {
        List<Employee> employees = employeeDatabase.getAllEmployees();
        for (Employee emp : employees) {
            employeeMap.put(emp.getId(), emp.getName());
        }
    }

    /** Loads tasks dynamically into TableLayout */
    private void loadTasks() {
        taskTable.removeAllViews(); // Clear existing data
        addHeaderRow(); // Add table header

        List<Task> tasks = dbHelper.getAllTasks();
        if (tasks == null || tasks.isEmpty()) {
            addEmptyRow();
        } else {
            for (Task task : tasks) {
                addTaskRow(task);
            }
        }
    }

    /** Adds Header Row to TableLayout */
    private void addHeaderRow() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.table_header_row, taskTable, false);
        taskTable.addView(headerView);
    }

    /** Adds Empty Row when no tasks exist */
    private void addEmptyRow() {
        TableRow emptyRow = new TableRow(this);
        TextView emptyText = new TextView(this);
        emptyText.setText("No tasks available");
        emptyText.setTextSize(16);
        emptyText.setPadding(16, 16, 16, 16);
        emptyRow.addView(emptyText);
        taskTable.addView(emptyRow);
    }

    /** Adds a Dynamic Task Row */
    private void addTaskRow(Task task) {
        if (task == null) return;

        View rowView = LayoutInflater.from(this).inflate(R.layout.task_row, taskTable, false);
        TableRow row = (TableRow) rowView;

        // Set Task Name
        ((TextView) row.findViewById(R.id.task_name)).setText(task.getTaskName());

        // Set Assigned Employees
        ((TextView) row.findViewById(R.id.task_assign)).setText(getEmployeeNames(task.getAssignedEmployeeIds()));

        // Initialize Priority Spinner
        Spinner prioritySpinner = row.findViewById(R.id.task_priority_spinner);
        setupSpinner(prioritySpinner, R.array.priority_options, task.getPriority().ordinal(), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                task.setPriority(Task.Priority.values()[position]);
                dbHelper.updateTask(task);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Initialize Status Spinner
        Spinner statusSpinner = row.findViewById(R.id.task_status_spinner);
        setupSpinner(statusSpinner, R.array.status_options, task.getStatus().ordinal(), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                task.setStatus(Task.Status.values()[position]);
                dbHelper.updateTask(task);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Initialize Progress Bar
        SeekBar progressBar = row.findViewById(R.id.task_progress_bar);
        TextView progressText = row.findViewById(R.id.task_progress_percentage);
        setupProgressBar(progressBar, progressText, task);

        // Add Row to Table
        taskTable.addView(row);
    }

    /** Helper method to setup Spinner */
    private void setupSpinner(Spinner spinner, int arrayRes, int selectedIndex, AdapterView.OnItemSelectedListener listener) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayRes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedIndex);
        spinner.setOnItemSelectedListener(listener);
    }

    /** Helper method to setup Progress Bar */
    private void setupProgressBar(SeekBar progressBar, TextView progressText, Task task) {
        progressBar.setProgress(task.getProgress());
        progressText.setText(task.getProgress() + "%");

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {  // Prevent unnecessary updates
                    task.setProgress(progress);
                    progressText.setText(progress + "%");
                    dbHelper.updateTask(task);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    /** Converts Employee IDs to Names using Cached Data */
    private String getEmployeeNames(List<Integer> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return "No assigned employees";
        }
        return employeeIds.stream()
                .map(id -> employeeMap.getOrDefault(id, "Unknown"))
                .collect(Collectors.joining(", "));
    }
}
