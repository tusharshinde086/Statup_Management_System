package com.example.startupmanagementsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {

    // Database Configuration
    private static final String DATABASE_NAME = "startup_management.db";
    private static final int DATABASE_VERSION =8 ;


    // Employee Table
    public static final String TABLE_EMPLOYEES = "employees";
    public static final String COLUMN_EMPLOYEE_ID = "id";
    public static final String COLUMN_EMPLOYEE_NAME = "name";
    public static final String COLUMN_EMPLOYEE_ROLE = "role";
    public static final String COLUMN_EMPLOYEE_EMAIL = "email";
    public static final String COLUMN_EMPLOYEE_DEPARTMENT = "department";
    public static final String COLUMN_EMPLOYEE_PHONE = "phone_number";
    public static final String COLUMN_EMPLOYEE_ADDRESS = "address";
    public static final String COLUMN_EMPLOYEE_DOB = "date_of_birth";
    public static final String COLUMN_EMPLOYEE_JOINING_DATE = "joining_date";
    public static final String COLUMN_EMPLOYEE_SALARY = "salary";
    // Project Table
    public static final String TABLE_PROJECTS = "projects";
    public static final String COLUMN_PROJECT_ID = "id";
    public static final String COLUMN_PROJECT_NAME = "name";
    public static final String COLUMN_PROJECT_START_TIME = "start_time";
    public static final String COLUMN_PROJECT_FINISH_TIME = "finish_time";
    public static final String COLUMN_PROJECT_EMPLOYEES_NEEDED = "employees_needed";
    public static final String COLUMN_PROJECT_DESCRIPTION = "description";

    // User Table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Task Table
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_TASK_ID = "id";
    public static final String COLUMN_TASK_NAME = "task_name";
    public static final String COLUMN_TASK_DESCRIPTION = "description"; // Added description
    public static final String COLUMN_TASK_DEADLINE = "deadline";
    public static final String COLUMN_TASK_PROJECT_ID = "project_id";
    public static final String COLUMN_TASK_ASSIGNED_EMPLOYEES = "assigned_employees";
    public static final String COLUMN_TASK_STATUS = "status";
    public static final String COLUMN_TASK_PROGRESS = "progress";
    public static final String COLUMN_TASK_PRIORITY = "priority";
    // Table Creation Queries
    private static final String CREATE_TABLE_EMPLOYEES =
            "CREATE TABLE " + TABLE_EMPLOYEES + " (" +
                    COLUMN_EMPLOYEE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EMPLOYEE_NAME + " TEXT NOT NULL, " +
                    COLUMN_EMPLOYEE_ROLE + " TEXT NOT NULL, " +
                    COLUMN_EMPLOYEE_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_EMPLOYEE_DEPARTMENT + " TEXT, " +
                    COLUMN_EMPLOYEE_PHONE + " TEXT, " +
                    COLUMN_EMPLOYEE_ADDRESS + " TEXT, " +
                    COLUMN_EMPLOYEE_DOB + " TEXT, " +
                    COLUMN_EMPLOYEE_JOINING_DATE + " TEXT, " +
                    COLUMN_EMPLOYEE_SALARY + " REAL)";  // Using REAL for salary to support decimals

    private static final String CREATE_TABLE_PROJECTS =
            "CREATE TABLE " + TABLE_PROJECTS + " (" +
                    COLUMN_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PROJECT_NAME + " TEXT NOT NULL, " +
                    COLUMN_PROJECT_START_TIME + " TEXT NOT NULL, " +
                    COLUMN_PROJECT_FINISH_TIME + " TEXT NOT NULL, " +
                    COLUMN_PROJECT_EMPLOYEES_NEEDED + " INTEGER NOT NULL, " +
                    COLUMN_PROJECT_DESCRIPTION + " TEXT NOT NULL)";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_USER_EMAIL + " TEXT NOT NULL, " +
                    COLUMN_USER_PASSWORD + " TEXT NOT NULL)";

    // Task Table Creation Query (New Addition)
    private static final String CREATE_TABLE_TASKS =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TASK_NAME + " TEXT NOT NULL, " + // Changed to task_name -> title
                    COLUMN_TASK_DESCRIPTION + " TEXT, " +
                    COLUMN_TASK_DEADLINE + " TEXT NOT NULL, " +
                    COLUMN_TASK_PROJECT_ID + " INTEGER NOT NULL, " +
                    COLUMN_TASK_ASSIGNED_EMPLOYEES + " TEXT NOT NULL, " +
                    COLUMN_TASK_STATUS + " TEXT, " +
                    COLUMN_TASK_PROGRESS + " INTEGER, " +
                    COLUMN_TASK_PRIORITY + " TEXT, " +  // Fixed missing space between priority and TEXT
                    "FOREIGN KEY(" + COLUMN_TASK_PROJECT_ID + ") REFERENCES projects(id))";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EMPLOYEES);
        db.execSQL(CREATE_TABLE_PROJECTS);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_TASKS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEES);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
                    } catch (android.database.sqlite.SQLiteException e) {
                e.printStackTrace();
            }

            onCreate(db);
        }
    }



    // ================== Employee Methods ================== //
    public long addEmployee(String name, String role, String email, String department, String phone, String address, String dob, String joiningDate, double salary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_EMPLOYEE_NAME, name);
        values.put(COLUMN_EMPLOYEE_ROLE, role);
        values.put(COLUMN_EMPLOYEE_EMAIL, email);
        values.put(COLUMN_EMPLOYEE_DEPARTMENT, department);
        values.put(COLUMN_EMPLOYEE_PHONE, phone);
        values.put(COLUMN_EMPLOYEE_ADDRESS, address);
        values.put(COLUMN_EMPLOYEE_DOB, dob);
        values.put(COLUMN_EMPLOYEE_JOINING_DATE, joiningDate);
        values.put(COLUMN_EMPLOYEE_SALARY, salary);

        long employeeId = db.insert(TABLE_EMPLOYEES, null, values);
        db.close();
        return employeeId;
    }



    public boolean updateEmployee(int id, String name, String role, String email, String department, String phone, String address, String dob, String joiningDate, double salary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_EMPLOYEE_NAME, name);
        values.put(COLUMN_EMPLOYEE_ROLE, role);
        values.put(COLUMN_EMPLOYEE_EMAIL, email);
        values.put(COLUMN_EMPLOYEE_DEPARTMENT, department);
        values.put(COLUMN_EMPLOYEE_PHONE, phone);
        values.put(COLUMN_EMPLOYEE_ADDRESS, address);
        values.put(COLUMN_EMPLOYEE_DOB, dob);
        values.put(COLUMN_EMPLOYEE_JOINING_DATE, joiningDate);
        values.put(COLUMN_EMPLOYEE_SALARY, salary);

        int rowsAffected = db.update(TABLE_EMPLOYEES, values, COLUMN_EMPLOYEE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public Employee getEmployeeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EMPLOYEES + " WHERE " + COLUMN_EMPLOYEE_ID + " = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            Employee employee = new Employee(
                    id,
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getDouble(9)
            );
            cursor.close();
            return employee;
        }
        return null;
    }

    public boolean deleteEmployee(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_EMPLOYEES, COLUMN_EMPLOYEE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }
    // ================== Get All Employees ================== //
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EMPLOYEES, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                employees.add(new Employee(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_EMPLOYEE_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMPLOYEE_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMPLOYEE_ROLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMPLOYEE_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMPLOYEE_DEPARTMENT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMPLOYEE_PHONE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMPLOYEE_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMPLOYEE_DOB)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMPLOYEE_JOINING_DATE)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_EMPLOYEE_SALARY))
                ));
            }
            cursor.close();
        }
        db.close();
        return employees;
    }


    // ================== Project Methods ================== //
    public long addProject(String name, String startTime, String finishTime,
                           int employeesNeeded, String description) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PROJECT_NAME, name);
            values.put(COLUMN_PROJECT_START_TIME, startTime);
            values.put(COLUMN_PROJECT_FINISH_TIME, finishTime);
            values.put(COLUMN_PROJECT_EMPLOYEES_NEEDED, employeesNeeded);
            values.put(COLUMN_PROJECT_DESCRIPTION, description);
            return db.insert(TABLE_PROJECTS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean deleteProject(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_PROJECTS,
                COLUMN_PROJECT_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean updateProject(int id, String name, String startTime,
                                 String finishTime, int employeesNeeded,
                                 String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECT_NAME, name);
        values.put(COLUMN_PROJECT_START_TIME, startTime);
        values.put(COLUMN_PROJECT_FINISH_TIME, finishTime);
        values.put(COLUMN_PROJECT_EMPLOYEES_NEEDED, employeesNeeded);
        values.put(COLUMN_PROJECT_DESCRIPTION, description);

        int rowsAffected = db.update(TABLE_PROJECTS, values,
                COLUMN_PROJECT_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    // ================== User Methods ================== //
    public long addUser(String username, String email, String password) {
        String hashedPassword = hashPassword(password); // Use hashing function
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_USER_EMAIL, email);
            values.put(COLUMN_USER_PASSWORD, hashedPassword);
            return db.insert(TABLE_USERS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isValidUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_PASSWORD},
                COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);

        if (cursor.moveToFirst()) {
            String storedHashedPassword = cursor.getString(0);
            cursor.close();
            db.close();
            return verifyPassword(password, storedHashedPassword);
        }

        cursor.close();
        db.close();
        return false;
    }

    private boolean verifyPassword(String password, String storedHashedPassword) {
        return hashPassword(password).equals(storedHashedPassword);
    }


    // ================== Task Methods ================== //

    // ================== Task Methods ================== //
    public long addTask(String taskName, String description, String deadline, int projectId, List<Integer> employeeIds, String priority, String status, int progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TASK_NAME, taskName);
        values.put(COLUMN_TASK_DESCRIPTION, description);
        values.put(COLUMN_TASK_DEADLINE, deadline);
        values.put(COLUMN_TASK_PROJECT_ID, projectId);
        values.put(COLUMN_TASK_PRIORITY, priority);
        values.put(COLUMN_TASK_STATUS, status);
        values.put(COLUMN_TASK_PROGRESS, progress);

        String assignedEmployees = (employeeIds != null && !employeeIds.isEmpty()) ?
                TextUtils.join(",", employeeIds) : "";
        values.put(COLUMN_TASK_ASSIGNED_EMPLOYEES, assignedEmployees);

        long taskId = -1;
        try {
            taskId = db.insertOrThrow(TABLE_TASKS, null, values);
        } catch (Exception e) {
            Log.e("DBHelper", "Error adding task", e);
        } finally {
            db.close();
        }
        return taskId;
    }

    public boolean updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TASK_NAME, task.getTaskName());
        values.put(COLUMN_TASK_DESCRIPTION, task.getDescription());
        values.put(COLUMN_TASK_DEADLINE, task.getDeadline());
        values.put(COLUMN_TASK_PROJECT_ID, task.getProjectId());
        values.put(COLUMN_TASK_PRIORITY, task.getPriority().name());
        values.put(COLUMN_TASK_STATUS, task.getStatus().name());
        values.put(COLUMN_TASK_PROGRESS, task.getProgress());

        // Convert assigned employee list to a comma-separated string
        String assignedEmployees = (task.getAssignedEmployeeIds() != null && !task.getAssignedEmployeeIds().isEmpty()) ?
                TextUtils.join(",", task.getAssignedEmployeeIds()) : "";
        values.put(COLUMN_TASK_ASSIGNED_EMPLOYEES, assignedEmployees);

        int rowsAffected = db.update(TABLE_TASKS, values, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();

        return rowsAffected > 0; // Return true if update was successful
    }

    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;

        try {
            int rowsAffected = db.delete("tasks", "id = ?", new String[]{String.valueOf(taskId)});
            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DBHelper", "Error deleting task", e);
        } finally {
            db.close();
        }

        return success;
    }
    public List<Task> getTasksByProjectId(int projectId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM tasks WHERE project_id = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(projectId)})) {
            while (cursor.moveToNext()) {
                // Extract employee IDs safely
                List<Integer> employeeIds = new ArrayList<>();
                String assignedEmployees = cursor.getString(cursor.getColumnIndexOrThrow("assigned_employees"));

                if (assignedEmployees != null && !assignedEmployees.isEmpty()) {
                    try {
                        for (String id : assignedEmployees.split(",")) {
                            employeeIds.add(Integer.parseInt(id.trim()));
                        }
                    } catch (NumberFormatException e) {
                        Log.e("DBHelper", "Error parsing employee IDs", e);
                    }
                }

                String priorityString = cursor.getString(cursor.getColumnIndexOrThrow("priority"));
                Task.Priority priority = Task.Priority.NORMAL; // Default value

                try {
                    priority = Task.Priority.valueOf(priorityString.toUpperCase()); // Convert to enum
                } catch (IllegalArgumentException e) {
                    Log.e("DBHelper", "Invalid priority value: " + priorityString, e);
                    // Use default value or handle the error appropriately
                }

                String statusString = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                Task.Status status = Task.Status.IN_PROGRESS;

                try {
                    status = Task.Status.valueOf(statusString.toUpperCase());
                } catch (IllegalArgumentException e) {
                    Log.e("DBHelper", "Invalid status value: " + statusString, e);
                }

                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("task_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("deadline")),
                        projectId,
                        employeeIds,
                        priority, // Now passing the enum value
                        status,
                        cursor.getInt(cursor.getColumnIndexOrThrow("progress"))
                );
                tasks.add(task);
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error retrieving tasks", e);
        } finally {
            db.close();
        }

        return tasks;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID));
                String taskName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DESCRIPTION));
                String deadline = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DEADLINE));
                int projectId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROJECT_ID));
                String assignedEmployeesString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_ASSIGNED_EMPLOYEES));

                String priorityString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_PRIORITY));
                Task.Priority priority = Task.Priority.NORMAL; // Default value

                try {
                    priority = Task.Priority.valueOf(priorityString.toUpperCase()); // Convert to enum
                } catch (IllegalArgumentException e) {
                    Log.e("DBHelper", "Invalid priority value: " + priorityString, e);
                    // Use default value or handle the error appropriately
                }

                String statusString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_STATUS));
                Task.Status status = Task.Status.IN_PROGRESS;

                try {
                    status = Task.Status.valueOf(statusString.toUpperCase());
                } catch (IllegalArgumentException e) {
                    Log.e("DBHelper", "Invalid status value: " + statusString, e);
                }

                int progress = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_PROGRESS));

                List<Integer> assignedEmployeeIds = new ArrayList<>();
                if (assignedEmployeesString != null && !assignedEmployeesString.isEmpty()) {
                    String[] ids = assignedEmployeesString.split(",");
                    for (String idStr : ids) {
                        try {
                            assignedEmployeeIds.add(Integer.parseInt(idStr.trim()));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }

                Task task = new Task(id, taskName, description, deadline, projectId, assignedEmployeeIds, priority, status, progress); // Include progress in constructor
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }
    // ================== Utility Methods ================== //
    public List<String> getAllProjectNames() {
        List<String> projectNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PROJECT_NAME + " FROM " + TABLE_PROJECTS,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                projectNames.add(cursor.getString(0));
            }
            cursor.close();
        }
        db.close();
        return projectNames;
    }
}