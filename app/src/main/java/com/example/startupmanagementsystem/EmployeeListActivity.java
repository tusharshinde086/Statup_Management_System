package com.example.startupmanagementsystem;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class EmployeeListActivity extends AppCompatActivity {

    private EditText idEditText, nameEditText, roleEditText, departmentEditText, emailEditText, phoneEditText, addressEditText, dobEditText, joiningDateEditText, salaryEditText;
    private Button addButton, updateButton, removeButton, viewButton;
    private TableLayout employeeTableLayout;
    private EmployeeDatabase employeeDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        // Initialize UI Components
        initializeViews();

        // Initialize Database
        employeeDatabase = new EmployeeDatabase(this);

        // Set button listeners
        addButton.setOnClickListener(v -> addEmployee());
        updateButton.setOnClickListener(v -> updateEmployee());
        removeButton.setOnClickListener(v -> removeEmployee());
        viewButton.setOnClickListener(v -> refreshTable());

        // Load initial employee data
        refreshTable();
    }

    private void initializeViews() {
        idEditText = findViewById(R.id.idEditText);
        nameEditText = findViewById(R.id.nameEditText);
        roleEditText = findViewById(R.id.roleEditText);
        departmentEditText = findViewById(R.id.departmentEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        dobEditText = findViewById(R.id.dobEditText);
        joiningDateEditText = findViewById(R.id.joiningDateEditText);
        salaryEditText = findViewById(R.id.salaryEditText);

        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        removeButton = findViewById(R.id.removeButton);
        viewButton = findViewById(R.id.viewButton);
        employeeTableLayout = findViewById(R.id.employeeTableLayout);
    }

    private void addEmployee() {
        if (!isValidInput()) return;

        employeeDatabase.addEmployee(
                getTextFromEditText(nameEditText),
                getTextFromEditText(roleEditText),
                getTextFromEditText(departmentEditText),
                getTextFromEditText(emailEditText),
                getTextFromEditText(phoneEditText),
                getTextFromEditText(addressEditText),
                getTextFromEditText(dobEditText),
                getTextFromEditText(joiningDateEditText),
                Double.parseDouble(getTextFromEditText(salaryEditText))
        );

        clearFields();
        showToast("Employee added successfully");
        refreshTable();
    }

    private void updateEmployee() {
        if (!isValidInput()) return;

        int id;
        try {
            id = Integer.parseInt(idEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            showToast("Invalid ID format");
            return;
        }

        employeeDatabase.updateEmployee(
                id,
                getTextFromEditText(nameEditText),
                getTextFromEditText(roleEditText),
                getTextFromEditText(departmentEditText),
                getTextFromEditText(emailEditText),
                getTextFromEditText(phoneEditText),
                getTextFromEditText(addressEditText),
                getTextFromEditText(dobEditText),
                getTextFromEditText(joiningDateEditText),
                Double.parseDouble(getTextFromEditText(salaryEditText))
        );

        clearFields();
        showToast("Employee updated successfully");
        refreshTable();
    }

    private void removeEmployee() {
        String idStr = idEditText.getText().toString().trim();
        if (idStr.isEmpty()) {
            showToast("Please enter Employee ID");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            employeeDatabase.deleteEmployee(id);
            clearFields();
            showToast("Employee removed successfully");
            refreshTable();
        } catch (NumberFormatException e) {
            showToast("Invalid ID format");
        }
    }

    private void refreshTable() {
        employeeTableLayout.removeAllViews();

        // Create table header
        TableRow headerRow = new TableRow(this);
        String[] headers = {"ID", "Name", "Role", "Dept.", "Email", "Phone", "Salary", "Actions"};
        for (String header : headers) {
            headerRow.addView(createTextView(header, true));
        }
        employeeTableLayout.addView(headerRow);

        // Populate employee data
        List<Employee> employees = employeeDatabase.getAllEmployees();
        for (Employee employee : employees) {
            employeeTableLayout.addView(createEmployeeRow(employee));
        }
    }

    private TableRow createEmployeeRow(Employee employee) {
        TableRow row = new TableRow(this);

        row.addView(createTextView(String.valueOf(employee.getId()), false));
        row.addView(createTextView(employee.getName(), false));
        row.addView(createTextView(employee.getRole(), false));
        row.addView(createTextView(employee.getDepartment(), false));
        row.addView(createTextView(employee.getEmail(), false));
        row.addView(createTextView(employee.getPhone(), false));
        row.addView(createTextView("$" + employee.getSalary(), false));

        // Update button
        Button updateBtn = new Button(this);
        updateBtn.setText("Update");
        updateBtn.setOnClickListener(v -> fillFieldsForUpdate(employee));
        row.addView(updateBtn);

        // Delete button
        Button deleteBtn = new Button(this);
        deleteBtn.setText("Delete");
        deleteBtn.setOnClickListener(v -> {
            employeeDatabase.deleteEmployee(employee.getId());
            showToast("Employee deleted");
            refreshTable();
        });
        row.addView(deleteBtn);

        return row;
    }

    private void fillFieldsForUpdate(Employee employee) {
        idEditText.setText(String.valueOf(employee.getId()));
        nameEditText.setText(employee.getName());
        roleEditText.setText(employee.getRole());
        departmentEditText.setText(employee.getDepartment());
        emailEditText.setText(employee.getEmail());
        phoneEditText.setText(employee.getPhone());
        addressEditText.setText(employee.getAddress());
        dobEditText.setText(employee.getDob());
        joiningDateEditText.setText(employee.getJoiningDate());
        salaryEditText.setText(String.valueOf(employee.getSalary()));
    }

    private boolean isValidInput() {
        if (getTextFromEditText(nameEditText).isEmpty() ||
                getTextFromEditText(roleEditText).isEmpty() ||
                getTextFromEditText(departmentEditText).isEmpty() ||
                getTextFromEditText(emailEditText).isEmpty() ||
                getTextFromEditText(phoneEditText).isEmpty() ||
                getTextFromEditText(salaryEditText).isEmpty()) {
            showToast("Please fill all fields");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(getTextFromEditText(emailEditText)).matches()) {
            showToast("Invalid email format");
            return false;
        }

        return true;
    }

    private String getTextFromEditText(EditText editText) {
        return editText.getText().toString().trim();
    }

    private TextView createTextView(String text, boolean isHeader) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    private void clearFields() {
        idEditText.setText("");
        nameEditText.setText("");
        roleEditText.setText("");
        departmentEditText.setText("");
        emailEditText.setText("");
        phoneEditText.setText("");
        addressEditText.setText("");
        dobEditText.setText("");
        joiningDateEditText.setText("");
        salaryEditText.setText("");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
