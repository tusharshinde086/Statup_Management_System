package com.example.startupmanagementsystem;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManagementActivity extends AppCompatActivity {

    private Spinner selectProjectSpinner;
    private TextView selectEmployeesTextView;
    private EditText taskTitle, taskDescription, taskDeadline;
    private Button addTaskButton, updateTaskButton, removeTaskButton;
    private TaskDatabase taskDatabase;
    private ProjectDatabase projectDatabase;
    private EmployeeDatabase employeeDatabase;

    private final List<String> projectNames = new ArrayList<>();
    private final List<String> employeeNames = new ArrayList<>();
    private final Map<String, Integer> projectMap = new HashMap<>();
    private final Map<String, Integer> employeeMap = new HashMap<>();
    private final List<Integer> selectedEmployeeIds = new ArrayList<>();

    private int selectedTaskId = -1; // Use this consistently for task selection
    private TableLayout taskTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);
        Button viewStatusButton = findViewById(R.id.viewStatusButton);
        viewStatusButton.setOnClickListener(v -> openTaskStatusActivity());
        initializeUI();
        initializeDatabases();
        loadProjects();
        loadEmployees();
        loadTasksIntoTable();
        setupListeners();
    }

    private void initializeUI() {
        selectProjectSpinner = findViewById(R.id.selectProjectSpinner);
        selectEmployeesTextView = findViewById(R.id.selectEmployeesTextView);
        taskTitle = findViewById(R.id.taskTitle);
        taskDescription = findViewById(R.id.taskDescription);
        taskDeadline = findViewById(R.id.taskDeadline);
        addTaskButton = findViewById(R.id.addTaskButton);
        updateTaskButton = findViewById(R.id.updateTaskButton);
        removeTaskButton = findViewById(R.id.removeTaskButton);
        taskTableLayout = findViewById(R.id.taskTableLayout);
    }

    private void initializeDatabases() {
        taskDatabase = new TaskDatabase(this);
        projectDatabase = new ProjectDatabase(this);
        employeeDatabase = new EmployeeDatabase(this);
    }
    private void openTaskStatusActivity() {
        Intent intent = new Intent(this, TaskStatusActivity.class);
        startActivity(intent);
    }

    private void clearFields() {
        taskTitle.setText("");
        taskDescription.setText("");
        taskDeadline.setText("");
        selectEmployeesTextView.setText("Select Employees");
        selectedEmployeeIds.clear();
        selectedTaskId = -1; // Reset selection
        addTaskButton.setText("Add Task");
    }

    private void setupListeners() {
        taskDeadline.setOnClickListener(v -> showDatePicker());
        addTaskButton.setOnClickListener(v -> saveTask());
        updateTaskButton.setOnClickListener(v -> updateTask());
        removeTaskButton.setOnClickListener(v -> removeTask());
        selectEmployeesTextView.setOnClickListener(v -> showEmployeeSelectionDialog());
    }

    private boolean validateTaskInput(String title, String description, String deadline) {
        if (title.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void saveTask() {
        String title = taskTitle.getText().toString().trim();
        String description = taskDescription.getText().toString().trim();
        String deadline = taskDeadline.getText().toString().trim();

        if (!validateTaskInput(title, description, deadline)) return;

        String selectedProject = (String) selectProjectSpinner.getSelectedItem();
        if (selectedProject == null || !projectMap.containsKey(selectedProject)) {
            showToast("Please select a valid project.");
            return;
        }

        int projectId = projectMap.get(selectedProject);

        if (selectedEmployeeIds.isEmpty()) {
            showToast("Please select at least one employee.");
            return;
        }

        Task newTask = new Task(title, description, deadline, projectId, new ArrayList<>(selectedEmployeeIds));

        long taskId = taskDatabase.addTask(newTask);

        if (taskId == -1) {
            showToast("Error: Task insertion failed. Please check database constraints.");
            return;
        }

        showToast("Task added successfully!");
        loadTasksIntoTable();
        clearFields();
    }

    private void updateTask() {
        String title = taskTitle.getText().toString().trim();
        String description = taskDescription.getText().toString().trim();
        String deadline = taskDeadline.getText().toString().trim();

        if (!validateTaskInput(title, description, deadline)) return;

        if (selectedTaskId == -1) {
            showToast("Please select a task to update.");
            return;
        }

        int projectId = projectMap.get(selectProjectSpinner.getSelectedItem().toString());
        // Retrieve the current task to get priority, status, and progress.
        Task currentTask = null;
        for (Task task : taskDatabase.getAllTasks()) {
            if (task.getId() == selectedTaskId) {
                currentTask = task;
                break;
            }
        }

        if (currentTask == null) {
            showToast("Task not found.");
            return;
        }

        Task updatedTask = new Task(selectedTaskId, title, description, deadline, projectId, new ArrayList<>(selectedEmployeeIds), currentTask.getPriority(), currentTask.getStatus(), currentTask.getProgress());

        if (taskDatabase.updateTask(updatedTask)) {
            showToast("Task updated successfully!");
            loadTasksIntoTable();
            clearFields();
        } else {
            showToast("Failed to update task.");
        }
    }

    private void removeTask() {
        if (selectedTaskId == -1) {
            showToast("Please select a task to remove.");
            return;
        }

        if (taskDatabase.deleteTask(selectedTaskId)) {
            showToast("Task removed successfully!");
            loadTasksIntoTable();
            clearFields();
        } else {
            showToast("Failed to remove task.");
        }
    }

    private void loadProjects() {
        List<Project> projectList = projectDatabase.getAllProjects();
        projectNames.clear();
        projectMap.clear();

        for (Project project : projectList) {
            projectNames.add(project.getName());
            projectMap.put(project.getName(), project.getId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projectNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectProjectSpinner.setAdapter(adapter);
    }

    private void loadEmployees() {
        List<Employee> employeeList = employeeDatabase.getAllEmployees();
        employeeNames.clear();
        employeeMap.clear();

        for (Employee employee : employeeList) {
            employeeNames.add(employee.getName());
            employeeMap.put(employee.getName(), employee.getId());
        }
    }

    private void showEmployeeSelectionDialog() {
        String[] employeeArray = employeeNames.toArray(new String[0]);
        boolean[] checkedItems = new boolean[employeeNames.size()];

        for (int i = 0; i < employeeArray.length; i++) {
            checkedItems[i] = selectedEmployeeIds.contains(employeeMap.get(employeeArray[i]));
        }

        List<Integer> tempSelectedEmployeeIds = new ArrayList<>(selectedEmployeeIds);

        new AlertDialog.Builder(this)
                .setTitle("Select Employees")
                .setMultiChoiceItems(employeeArray, checkedItems, (dialog, which, isChecked) -> {
                    int employeeId = employeeMap.get(employeeArray[which]);
                    if (isChecked) tempSelectedEmployeeIds.add(employeeId);
                    else tempSelectedEmployeeIds.remove(Integer.valueOf(employeeId));
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    selectedEmployeeIds.clear();
                    selectedEmployeeIds.addAll(tempSelectedEmployeeIds);
                    updateSelectedEmployees();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateSelectedEmployees() {
        List<String> selectedEmployees = new ArrayList<>();
        for (Integer id : selectedEmployeeIds) {
            for (Map.Entry<String, Integer> entry : employeeMap.entrySet()) {
                if (entry.getValue().equals(id)) {
                    selectedEmployees.add(entry.getKey());
                }
            }
        }
        selectEmployeesTextView.setText(String.join(", ", selectedEmployees));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) ->
                taskDeadline.setText(String.format("%d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void loadTasksIntoTable() {
        taskTableLayout.removeViews(1,
        taskTableLayout.getChildCount() - 1);

        List<Task> tasks = taskDatabase.getAllTasks();

        for (Task task : tasks) {
            TableRow row = new TableRow(this);

            TextView idText = new TextView(this);
            idText.setText(String.valueOf(task.getId()));
            idText.setPadding(8, 8, 8, 8);

            TextView titleText = new TextView(this);
            titleText.setText(task.getTaskName());
            titleText.setPadding(8, 8, 8, 8);

            TextView deadlineText = new TextView(this);
            deadlineText.setText(task.getDeadline());
            deadlineText.setPadding(8, 8, 8, 8);

            LinearLayout actionLayout = new LinearLayout(this);
            actionLayout.setOrientation(LinearLayout.HORIZONTAL);

            Button updateBtn = new Button(this);
            updateBtn.setText("Update");
            updateBtn.setTextSize(12);
            updateBtn.setPadding(8, 8, 8, 8);
            updateBtn.setOnClickListener(v -> showUpdateDialog(task));

            Button deleteBtn = new Button(this);
            deleteBtn.setText("Delete");
            deleteBtn.setTextSize(12);
            deleteBtn.setPadding(8, 8, 8, 8);
            deleteBtn.setOnClickListener(v -> deleteTask(task.getId()));

            actionLayout.addView(updateBtn);
            actionLayout.addView(deleteBtn);

            row.addView(idText);
            row.addView(titleText);
            row.addView(deadlineText);
            row.addView(actionLayout);

            taskTableLayout.addView(row);
        }
    }

    private void showUpdateDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Task");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        final EditText titleInput = new EditText(this);
        titleInput.setText(task.getTaskName());
        layout.addView(titleInput);

        final EditText deadlineInput = new EditText(this);
        deadlineInput.setText(task.getDeadline());
        layout.addView(deadlineInput);

        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newTitle = titleInput.getText().toString().trim();
            String newDeadline = deadlineInput.getText().toString().trim();

            if (newTitle.isEmpty() || newDeadline.isEmpty()) {
                showToast("Title and deadline cannot be empty.");
                return;
            }

            task.setTaskName(newTitle);
            task.setDeadline(newDeadline);
            taskDatabase.updateTask(task);
            loadTasksIntoTable();
            showToast("Task updated successfully!");
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteTask(int taskId) {
        taskDatabase.deleteTask(taskId);
        loadTasksIntoTable();
    }
}