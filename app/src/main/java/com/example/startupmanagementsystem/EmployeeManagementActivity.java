package com.example.startupmanagementsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

public class EmployeeManagementActivity extends AppCompatActivity {

    private EditText idEditText, nameEditText, roleEditText, departmentEditText, emailEditText,
            phoneEditText, addressEditText, dobEditText, joiningDateEditText, salaryEditText;
    private Button addButton, updateButton, removeButton, viewButton;
    private TableLayout employeeTableLayout;
    private EmployeeDatabase employeeDatabase;
    private int selectedEmployeeId = -1;  // To store the selected employee ID for updates


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_management);

        // Initialize views
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

        // Initialize database
        employeeDatabase = new EmployeeDatabase(this);

        // Set button listeners
        addButton.setOnClickListener(v -> addEmployee());
        updateButton.setOnClickListener(v -> updateEmployee());
        removeButton.setOnClickListener(v -> removeEmployee());
        viewButton.setOnClickListener(v -> refreshTable());

        // Initial table refresh


        // Initialize views
        dobEditText = findViewById(R.id.dobEditText);
        joiningDateEditText = findViewById(R.id.joiningDateEditText);

        // Set DatePickerDialog for DOB and Joining Date fields
        dobEditText.setOnClickListener(v -> showDatePickerDialog(dobEditText));
        joiningDateEditText.setOnClickListener(v -> showDatePickerDialog(joiningDateEditText));
        refreshTable();
    }

    // Function to show DatePickerDialog and set selected date
    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format: YYYY-MM-DD
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    editText.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }
    private void addEmployee() {
        String name = nameEditText.getText().toString().trim();
        String role = roleEditText.getText().toString().trim();
        String department = departmentEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();
        String joiningDate = joiningDateEditText.getText().toString().trim();
        String salaryStr = salaryEditText.getText().toString().trim();

        if (name.isEmpty() || role.isEmpty() || department.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                address.isEmpty() || dob.isEmpty() || joiningDate.isEmpty() || salaryStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email) || !isValidPhone(phone) || !isValidSalary(salaryStr)) {
            Toast.makeText(this, "Invalid input format", Toast.LENGTH_SHORT).show();
            return;
        }

        double salary = Double.parseDouble(salaryStr);

        employeeDatabase.addEmployee(name, role, department, email, phone, address, dob, joiningDate, salary);
        clearFields();
        Toast.makeText(this, "Employee added successfully", Toast.LENGTH_SHORT).show();
        refreshTable();
    }

    private void updateEmployee() {
        if (selectedEmployeeId == -1) {
            Toast.makeText(this, "Select an employee to update", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = nameEditText.getText().toString().trim();
        String role = roleEditText.getText().toString().trim();
        String department = departmentEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();
        String joiningDate = joiningDateEditText.getText().toString().trim();
        String salaryStr = salaryEditText.getText().toString().trim();

        if (name.isEmpty() || role.isEmpty() || department.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                address.isEmpty() || dob.isEmpty() || joiningDate.isEmpty() || salaryStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email) || !isValidPhone(phone) || !isValidSalary(salaryStr)) {
            Toast.makeText(this, "Invalid input format", Toast.LENGTH_SHORT).show();
            return;
        }

        double salary = Double.parseDouble(salaryStr);

        employeeDatabase.updateEmployee(selectedEmployeeId, name, role, department, email, phone, address, dob, joiningDate, salary);
        clearFields();
        selectedEmployeeId = -1;
        Toast.makeText(this, "Employee updated successfully", Toast.LENGTH_SHORT).show();
        refreshTable();
    }

    private void removeEmployee() {
        if (selectedEmployeeId == -1) {
            Toast.makeText(this, "Select an employee to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        employeeDatabase.deleteEmployee(selectedEmployeeId);
        clearFields();
        selectedEmployeeId = -1;
        Toast.makeText(this, "Employee removed successfully", Toast.LENGTH_SHORT).show();
        refreshTable();
    }

    private void refreshTable() {
        employeeTableLayout.removeAllViews();

        TableRow headerRow = new TableRow(this);
        addTextViewToRow(headerRow, "ID");
        addTextViewToRow(headerRow, "Name");
        addTextViewToRow(headerRow, "Role");
        addTextViewToRow(headerRow, "Department");
        addTextViewToRow(headerRow, "Email");
        addTextViewToRow(headerRow, "Phone");
        addTextViewToRow(headerRow, "Address");
        addTextViewToRow(headerRow, "DOB");
        addTextViewToRow(headerRow, "Joining Date");
        addTextViewToRow(headerRow, "Salary");
        employeeTableLayout.addView(headerRow);

        List<Employee> employees = employeeDatabase.getAllEmployees();
        for (Employee employee : employees) {
            TableRow row = new TableRow(this);
            addTextViewToRow(row, String.valueOf(employee.getId()));
            addTextViewToRow(row, employee.getName());
            addTextViewToRow(row, employee.getRole());
            addTextViewToRow(row, employee.getDepartment());
            addTextViewToRow(row, employee.getEmail());
            addTextViewToRow(row, employee.getPhone());
            addTextViewToRow(row, employee.getAddress());
            addTextViewToRow(row, employee.getDob());
            addTextViewToRow(row, employee.getJoiningDate());
            addTextViewToRow(row, String.valueOf(employee.getSalary()));

            employeeTableLayout.addView(row);
        }

        Log.d("EmployeeManagement", "Table refreshed with " + employees.size() + " employees.");
    }

    private void addTextViewToRow(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        row.addView(textView);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    private boolean isValidSalary(String salary) {
        return salary.matches("\\d+(\\.\\d{1,2})?");
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
}
