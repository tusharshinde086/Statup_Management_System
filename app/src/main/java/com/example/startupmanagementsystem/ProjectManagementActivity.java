package com.example.startupmanagementsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class ProjectManagementActivity extends AppCompatActivity {
    private EditText idEditText, nameEditText, descEditText, startDateEditText, endDateEditText, employeesEditText;
    private EditText updateIdEditText, updateNameEditText, updateDescEditText, updateStartDateEditText, updateEndDateEditText, updateEmployeesEditText;
    private Button addButton, updateButton, removeButton, viewButton;
    private TableLayout projectTableLayout;
    private ProjectDatabase projectDatabase;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_management);

        initializeViews();
        projectDatabase = new ProjectDatabase(this);
        setButtonListeners();
        refreshTable();
    }

    private void initializeViews() {
        idEditText = findViewById(R.id.updateProjectIdEditText);
        nameEditText = findViewById(R.id.projectNameEditText);
        descEditText = findViewById(R.id.projectDescriptionEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        employeesEditText = findViewById(R.id.employeesEditText);
        updateIdEditText = findViewById(R.id.updateProjectIdEditText);
        updateNameEditText = findViewById(R.id.updateProjectNameEditText);
        updateDescEditText = findViewById(R.id.updateProjectDescriptionEditText);
        updateStartDateEditText = findViewById(R.id.updateStartDateEditText);
        updateEndDateEditText = findViewById(R.id.updateEndDateEditText);
        updateEmployeesEditText = findViewById(R.id.updateEmployeesEditText);
        addButton = findViewById(R.id.addProjectButton);
        updateButton = findViewById(R.id.updateProjectButton);
        removeButton = findViewById(R.id.removeProjectButton);
        viewButton = findViewById(R.id.viewProjectsButton);
        projectTableLayout = findViewById(R.id.projectTableLayout);

        // Set up date pickers
        setupDatePicker(startDateEditText);
        setupDatePicker(endDateEditText);
        setupDatePicker(updateStartDateEditText);
        setupDatePicker(updateEndDateEditText);
    }

    private void setupDatePicker(EditText editText) {
        editText.setOnClickListener(v -> showDatePicker(editText));
    }

    private void setButtonListeners() {
        addButton.setOnClickListener(v -> addProject());
        updateButton.setOnClickListener(v -> updateProject());
        removeButton.setOnClickListener(v -> removeProject());
        viewButton.setOnClickListener(v -> refreshTable());
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
            String selectedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
            editText.setText(selectedDate);
        }, year, month, day).show();
    }
    private boolean validateInputs(String name, String startDate, String endDate, String employees) {
        return !name.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty() && !employees.isEmpty();
    }

    private void addProject() {
        try {
            String name = nameEditText.getText().toString().trim();
            String desc = descEditText.getText().toString().trim();
            String startDateStr = startDateEditText.getText().toString().trim();
            String endDateStr = endDateEditText.getText().toString().trim();
            String employeesStr = employeesEditText.getText().toString().trim();

            if (!validateInputs(name, startDateStr, endDateStr, employeesStr)) return;

            LocalDate startDate = LocalDate.parse(startDateStr, dateFormatter);
            LocalDate endDate = LocalDate.parse(endDateStr, dateFormatter);
            int employees = Integer.parseInt(employeesStr);

            Project project = new Project(0, name, startDate, endDate, employees, desc);
            projectDatabase.addProject(project);

            clearFields();
            Toast.makeText(this, "Project added successfully", Toast.LENGTH_SHORT).show();
            refreshTable();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateProject() {
        try {
            String idStr = updateIdEditText.getText().toString().trim();
            String name = updateNameEditText.getText().toString().trim();
            String desc = updateDescEditText.getText().toString().trim();
            String startDateStr = updateStartDateEditText.getText().toString().trim();
            String endDateStr = updateEndDateEditText.getText().toString().trim();
            String employeesStr = updateEmployeesEditText.getText().toString().trim();

            if (!validateInputs(name, startDateStr, endDateStr, employeesStr) || idStr.isEmpty()) return;

            int id = Integer.parseInt(idStr);
            LocalDate startDate = LocalDate.parse(startDateStr, dateFormatter);
            LocalDate endDate = LocalDate.parse(endDateStr, dateFormatter);
            int employees = Integer.parseInt(employeesStr);

            Project project = new Project(id, name, startDate, endDate, employees, desc);
            projectDatabase.updateProject(project);

            clearFields();
            Toast.makeText(this, "Project updated successfully", Toast.LENGTH_SHORT).show();
            refreshTable();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void removeProject() {
        try {
            String idStr = idEditText.getText().toString().trim();
            if (idStr.isEmpty()) {
                Toast.makeText(this, "Please enter a valid ID", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = Integer.parseInt(idStr);
            projectDatabase.deleteProject(id);

            clearFields();
            Toast.makeText(this, "Project removed successfully", Toast.LENGTH_SHORT).show();
            refreshTable();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void refreshTable() {
        projectTableLayout.removeAllViews();
        TableRow headerRow = new TableRow(this);
        addTextViewToRow(headerRow, "ID");
        addTextViewToRow(headerRow, "Name");
        addTextViewToRow(headerRow, "Start Date");
        addTextViewToRow(headerRow, "End Date");
        addTextViewToRow(headerRow, "Employees");
        addTextViewToRow(headerRow, "Description");
        projectTableLayout.addView(headerRow);

        for (Project project : projectDatabase.getAllProjects()) {
            TableRow row = new TableRow(this);
            addTextViewToRow(row, String.valueOf(project.getId()));
            addTextViewToRow(row, project.getName());
            addTextViewToRow(row, project.getStartDate().toString());
            addTextViewToRow(row, project.getEndDate().toString());
            addTextViewToRow(row, String.valueOf(project.getEmployeesNeeded()));
            addTextViewToRow(row, project.getDescription());
            projectTableLayout.addView(row);
        }
    }

    private void addTextViewToRow(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        row.addView(textView);
    }

    private void clearFields() {
        idEditText.setText("");
        nameEditText.setText("");
        descEditText.setText("");
        startDateEditText.setText("");
        endDateEditText.setText("");
        employeesEditText.setText("");
    }

    private void showError(Exception e) {
        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
