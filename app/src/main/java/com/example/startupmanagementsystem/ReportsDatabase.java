package com.example.startupmanagementsystem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsDatabase {
    private final DBHelper dbHelper;
    private final EmployeeDatabase employeeDatabase;

    public ReportsDatabase(Context context) {
        this.dbHelper = new DBHelper(context);
        this.employeeDatabase = new EmployeeDatabase(context);
    }

    /**
     * Fetch all reports along with their associated tasks.
     * @return List of Report objects with assigned tasks.
     */
    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();
        Map<Integer, Report> reportMap = new HashMap<>();

        String query = "SELECT r.id AS report_id, r.title, r.date, r.content, " +
                "t.id AS task_id, t.task_name, t.description, t.deadline, t.project_id, t.assigned_employee_ids, " +
                "t.priority, t.status, t.progress " +
                "FROM reports r " +
                "LEFT JOIN tasks t ON r.id = t.project_id";

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, null)) {

            while (cursor.moveToNext()) {
                int reportId = cursor.getInt(cursor.getColumnIndexOrThrow("report_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));

                // Get or create the Report instance
                Report report = reportMap.get(reportId);
                if (report == null) {
                    report = new Report(reportId, title, date, content, new ArrayList<>(), employeeDatabase);
                    reports.add(report);
                    reportMap.put(reportId, report);
                }

                // Fetch task details (if available)
                int taskId = cursor.getInt(cursor.getColumnIndexOrThrow("task_id"));
                if (taskId > 0) {
                    String taskName = cursor.getString(cursor.getColumnIndexOrThrow("task_name"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));
                    int projectId = cursor.getInt(cursor.getColumnIndexOrThrow("project_id"));
                    String assignedEmployeeIdsStr = cursor.getString(cursor.getColumnIndexOrThrow("assigned_employee_ids"));
                    String priorityStr = cursor.getString(cursor.getColumnIndexOrThrow("priority"));
                    String statusStr = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    int progress = cursor.getInt(cursor.getColumnIndexOrThrow("progress"));

                    // Convert employee IDs from a comma-separated string to a list
                    List<Integer> assignedEmployeeIds = parseEmployeeIds(assignedEmployeeIdsStr);

                    // Convert priority and status from string to enums
                    Task.Priority priority = Task.Priority.valueOf(priorityStr.toUpperCase());
                    Task.Status status = Task.Status.valueOf(statusStr.toUpperCase());

                    // Create Task object with correct parameters
                    Task task = new Task(taskId, taskName, description, deadline, projectId, assignedEmployeeIds, priority, status, progress);
                    report.getTasks().add(task);
                }
            }
        }
        return reports;
    }

    /**
     * Converts a comma-separated string of employee IDs into a List of Integers.
     */
    private List<Integer> parseEmployeeIds(String employeeIdsStr) {
        List<Integer> employeeIds = new ArrayList<>();
        if (employeeIdsStr != null && !employeeIdsStr.isEmpty()) {
            String[] ids = employeeIdsStr.split(",");
            for (String id : ids) {
                try {
                    employeeIds.add(Integer.parseInt(id.trim()));
                } catch (NumberFormatException e) {
                    // Ignore invalid numbers
                }
            }
        }
        return employeeIds;
    }
}
