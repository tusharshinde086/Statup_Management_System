package com.example.startupmanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {

    private Spinner employeeSpinner, monthSpinner, yearSpinner;
    private GridView calendarView;
    private ListView taskListView;
    private TextView calendarHeader;

    private TaskAdapter taskAdapter;
    private CalendarAdapter calendarAdapter;
    private TaskDatabase taskDatabase;
    private EmployeeDatabase employeeDatabase;

    private List<Employee> employees;
    private List<Task> allTasks;
    private List<Task> filteredTasks;
    private Map<String, Integer> taskFrequency;

    private Calendar currentCalendar;
    private int selectedMonth, selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // Initialize UI elements
        employeeSpinner = findViewById(R.id.employeeSpinner);
        monthSpinner = findViewById(R.id.spinnerMonth);
        yearSpinner = findViewById(R.id.spinnerYear);
        calendarView = findViewById(R.id.calendarView);
        taskListView = findViewById(R.id.taskListView);
        calendarHeader = findViewById(R.id.calendarHeader);

        // Initialize Databases
        taskDatabase = new TaskDatabase(this);
        employeeDatabase = new EmployeeDatabase(this);

        // Load Employees
        employees = employeeDatabase.getAllEmployees();
        populateEmployeeSpinner();

        // Initialize Task List
        allTasks = taskDatabase.getAllTasks();
        filteredTasks = new ArrayList<>();
        taskFrequency = new HashMap<>();

        // Initialize Calendar
        currentCalendar = Calendar.getInstance();
        selectedMonth = currentCalendar.get(Calendar.MONTH) + 1;
        selectedYear = currentCalendar.get(Calendar.YEAR);

        updateCalendarHeader();
        populateMonthSpinner();
        populateYearSpinner();

        // Initialize Adapters
        taskAdapter = new TaskAdapter(this, filteredTasks, employeeDatabase);
        taskListView.setAdapter(taskAdapter);

        calendarAdapter = new CalendarAdapter(this, selectedMonth, selectedYear, taskFrequency);
        calendarView.setAdapter(calendarAdapter);

        // Employee Selection Listener
        employeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Employee selectedEmployee = employees.get(position);
                filterTasksByEmployee(selectedEmployee.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                resetTaskView();
            }
        });

        // Month Selection Listener
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = position + 1; // Months are 1-based
                updateCalendarHeader();
                updateCalendar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Year Selection Listener
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = Integer.parseInt(parent.getItemAtPosition(position).toString());
                updateCalendarHeader();
                updateCalendar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Update calendar header with Month and Year
    private void updateCalendarHeader() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        currentCalendar.set(Calendar.MONTH, selectedMonth - 1);
        currentCalendar.set(Calendar.YEAR, selectedYear);
        calendarHeader.setText(dateFormat.format(currentCalendar.getTime()));
    }

    // Populate Employee Spinner
    private void populateEmployeeSpinner() {
        List<String> employeeNames = new ArrayList<>();
        for (Employee employee : employees) {
            employeeNames.add(employee.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, employeeNames);
        employeeSpinner.setAdapter(adapter);
    }

    // Populate Month Spinner
    private void populateMonthSpinner() {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        monthSpinner.setAdapter(adapter);
        monthSpinner.setSelection(selectedMonth - 1);
    }

    // Populate Year Spinner
    private void populateYearSpinner() {
        List<String> years = new ArrayList<>();
        for (int i = 2020; i <= 2030; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        yearSpinner.setAdapter(adapter);
        yearSpinner.setSelection(years.indexOf(String.valueOf(selectedYear)));
    }

    // Filter tasks by selected employee
    private void filterTasksByEmployee(int employeeId) {
        filteredTasks.clear();
        taskFrequency.clear();

        for (Task task : allTasks) {
            if (task.getAssignedEmployeeIds().contains(employeeId) && isTaskInSelectedMonthYear(task.getDeadline())) {
                filteredTasks.add(task);
                updateTaskFrequency(task.getDeadline());
            }
        }

        taskAdapter.notifyDataSetChanged();
        calendarAdapter.notifyDataSetChanged();

        if (filteredTasks.isEmpty()) {
            Toast.makeText(this, "No tasks found for this employee in selected month", Toast.LENGTH_SHORT).show();
        }
    }

    // Check if task is within selected month and year
    private boolean isTaskInSelectedMonthYear(String date) {
        String[] parts = date.split("-");
        int taskYear = Integer.parseInt(parts[0]);
        int taskMonth = Integer.parseInt(parts[1]);
        return taskYear == selectedYear && taskMonth == selectedMonth;
    }

    // Update task count per date
    private void updateTaskFrequency(String date) {
        taskFrequency.put(date, taskFrequency.getOrDefault(date, 0) + 1);
    }

    // Reset task view when no employee is selected
    private void resetTaskView() {
        filteredTasks.clear();
        taskFrequency.clear();
        taskAdapter.notifyDataSetChanged();
        calendarAdapter.notifyDataSetChanged();
    }

    // Update calendar when month or year changes
    private void updateCalendar() {
        calendarAdapter = new CalendarAdapter(this, selectedMonth, selectedYear, taskFrequency);
        calendarView.setAdapter(calendarAdapter);
    }
}
