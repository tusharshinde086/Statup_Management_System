package com.example.startupmanagementsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDatabase extends SQLiteOpenHelper {
    private static final String TAG = "EmployeeDatabase";
    private static final String DATABASE_NAME = "EmployeeDB";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_EMPLOYEES = "employees";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ROLE = "role";
    private static final String COLUMN_DEPARTMENT = "department";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_DOB = "dob";
    private static final String COLUMN_JOINING_DATE = "joining_date";
    private static final String COLUMN_SALARY = "salary";

    public EmployeeDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_EMPLOYEES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_ROLE + " TEXT NOT NULL, "
                + COLUMN_DEPARTMENT + " TEXT, "
                + COLUMN_EMAIL + " TEXT UNIQUE, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_ADDRESS + " TEXT, "
                + COLUMN_DOB + " TEXT, "
                + COLUMN_JOINING_DATE + " TEXT, "
                + COLUMN_SALARY + " REAL CHECK(" + COLUMN_SALARY + " >= 0))";

        try {
            db.execSQL(CREATE_TABLE);
            Log.d(TAG, "Table created successfully");
        } catch (SQLException e) {
            Log.e(TAG, "Error creating table: ", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEES);
        onCreate(db);
    }

    // Insert Employee
    public boolean addEmployee(String name, String role, String department, String email, String phone,
                               String address, String dob, String joiningDate, double salary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ROLE, role);
        values.put(COLUMN_DEPARTMENT, department);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_JOINING_DATE, joiningDate);
        values.put(COLUMN_SALARY, salary);

        try {
            long result = db.insertOrThrow(TABLE_EMPLOYEES, null, values);
            return result != -1;
        } catch (SQLException e) {
            Log.e(TAG, "Error adding employee: ", e);
            return false;
        } finally {
            db.close();
        }
    }

    // Update Employee
    public boolean updateEmployee(int id, String name, String role, String department, String email,
                                  String phone, String address, String dob, String joiningDate, double salary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ROLE, role);
        values.put(COLUMN_DEPARTMENT, department);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_JOINING_DATE, joiningDate);
        values.put(COLUMN_SALARY, salary);

        try {
            int rowsAffected = db.update(TABLE_EMPLOYEES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating employee: ", e);
            return false;
        } finally {
            db.close();
        }
    }

    // Delete Employee
    public boolean deleteEmployee(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int rowsDeleted = db.delete(TABLE_EMPLOYEES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            return rowsDeleted > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting employee: ", e);
            return false;
        } finally {
            db.close();
        }
    }

    // Fetch All Employees
    public List<Employee> getAllEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EMPLOYEES;

        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(selectQuery, null)) {
            while (cursor.moveToNext()) {
                Employee employee = new Employee(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEPARTMENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOB)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOINING_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SALARY))
                );
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error fetching employees: ", e);
        } finally {
            db.close();
        }
        return employeeList;
    }

    // Get Employee by ID
    public Employee getEmployeeById(int id) {
        Employee employee = null;
        String selectQuery = "SELECT * FROM " + TABLE_EMPLOYEES + " WHERE " + COLUMN_ID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)})) {
            if (cursor.moveToFirst()) {
                employee = new Employee(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEPARTMENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOB)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOINING_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SALARY))
                );
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error fetching employee by ID: ", e);
        } finally {
            db.close();
        }
        return employee;
    }
}
