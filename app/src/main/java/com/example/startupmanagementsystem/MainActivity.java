package com.example.startupmanagementsystem;

import static com.example.startupmanagementsystem.R.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
/**
 * MainActivity serves as the entry point to the app, allowing navigation
 * to different management sections like Employees, Projects, Tasks, and Reports.
 */
public class MainActivity extends AppCompatActivity {

    private Button manageEmployeesButton;
    private Button manageProjectsButton;
    private Button manageTasksButton;
    private Button viewReportsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize the UI components
        initializeUI();
        // Attach click listeners to buttons
        configureButtonListeners();
    }
    /**
     * Initializes the buttons by linking them to their XML definitions.
     */
    private void initializeUI() {
        manageEmployeesButton = findViewById(R.id.manageEmployeesButton);
        manageProjectsButton = findViewById(R.id.manageProjectsButton);
        manageTasksButton = findViewById(R.id.manageTasksButton);
        viewReportsButton = findViewById(R.id.viewReportsButton);

        // Apply custom drawable background programmatically
        manageEmployeesButton.setBackgroundResource(R.drawable.menu_button_background);
        manageProjectsButton.setBackgroundResource(R.drawable.menu_button_background);
        manageTasksButton.setBackgroundResource(R.drawable.menu_button_background);
        viewReportsButton.setBackgroundResource(R.drawable.menu_button_background);
    }
    /**
     * Configures button click listeners to navigate to appropriate activities.
     */
    private void configureButtonListeners() {
        manageEmployeesButton.setOnClickListener(v -> {
            showToast(getString(R.string.navigate_employee_management));
            navigateTo(EmployeeManagementActivity.class);
        });

        manageProjectsButton.setOnClickListener(v -> {
            showToast(getString(R.string.navigate_project_management));
            navigateTo(ProjectManagementActivity.class);
        });

        manageTasksButton.setOnClickListener(v -> {
            showToast(getString(R.string.navigate_task_management));
            navigateTo(TaskManagementActivity.class);
        });

        viewReportsButton.setOnClickListener(v -> {
            showToast(getString(R.string.navigate_reports));
            navigateTo(ReportsActivity.class);
        });
    }

    /**
     * Navigates to the specified activity.
     *
     * @param targetActivity The target activity class.
     */
    private void navigateTo(Class<?> targetActivity) {
        try {
            Intent intent = new Intent(this, targetActivity);
            startActivity(intent);
        } catch (Exception e) {
            // Log the error and show a user-friendly message
            e.printStackTrace();
            showToast(getString(R.string.error_navigation));
        }
    }
    /**
     * Displays a short Toast message.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handling the refresh action
        if (item.getItemId() == R.id.action_refresh) {
            handleRefreshAction();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Handles the refresh menu action.
     */
    private void handleRefreshAction() {
        // Placeholder for refresh logic (e.g., reload data or sync with a server)
        showToast(getString(R.string.data_refreshed));
    }
}
