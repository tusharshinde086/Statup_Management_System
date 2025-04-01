package com.example.startupmanagementsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TaskDatabase {
    private final DBHelper dbHelper;
    private static final String TAG = "TaskDatabase";

    public TaskDatabase(Context context) {
        dbHelper = new DBHelper(context);
    }

    /** -------------------- ADD TASK -------------------- */
    public long addTask(Task task) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = createTaskValues(task);
            long taskId = db.insertOrThrow(DBHelper.TABLE_TASKS, null, values);

            if (taskId == -1) {
                Log.e(TAG, "Task insertion failed: " + task.getTaskName());
            } else {
                Log.d(TAG, "Task added successfully: " + task.getTaskName() + " (ID: " + taskId + ")");
            }
            return taskId;
        } catch (SQLException e) {
            Log.e(TAG, "Error adding task: " + task.getTaskName(), e);
            return -1;
        }
    }

    /** -------------------- FETCH TASKS -------------------- */
    public List<Task> getAllTasks() {
        return fetchTasks(null, null);
    }

    public List<Task> getTasksByProjectId(int projectId) {
        return fetchTasks(DBHelper.COLUMN_TASK_PROJECT_ID + " = ?", new String[]{String.valueOf(projectId)});
    }

    public Task getTaskById(int taskId) {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query(DBHelper.TABLE_TASKS, null,
                     DBHelper.COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)},
                     null, null, null)) {
            if (cursor.moveToFirst()) {
                return createTaskFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching task by ID: " + taskId, e);
        }
        return null;
    }

    /** -------------------- UPDATE TASK -------------------- */
    public boolean updateTask(Task task) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = createTaskValues(task);
            int rowsAffected = db.update(DBHelper.TABLE_TASKS, values,
                    DBHelper.COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(task.getId())});

            if (rowsAffected > 0) {
                Log.d(TAG, "Task updated successfully: " + task.getTaskName() + " (ID: " + task.getId() + ")");
            } else {
                Log.e(TAG, "No task found to update: " + task.getTaskName());
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating task: " + task.getTaskName(), e);
            return false;
        }
    }

    /** -------------------- DELETE TASK -------------------- */
    public boolean deleteTask(int taskId) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            int rowsAffected = db.delete(DBHelper.TABLE_TASKS, DBHelper.COLUMN_TASK_ID + " = ?",
                    new String[]{String.valueOf(taskId)});

            if (rowsAffected > 0) {
                Log.d(TAG, "Task deleted successfully (ID: " + taskId + ")");
            } else {
                Log.e(TAG, "No task found to delete (ID: " + taskId + ")");
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting task with ID: " + taskId, e);
            return false;
        }
    }

    /** -------------------- FETCH TASKS WITH FILTERS -------------------- */
    private List<Task> fetchTasks(String selection, String[] selectionArgs) {
        List<Task> tasks = new ArrayList<>();
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query(DBHelper.TABLE_TASKS, null, selection, selectionArgs,
                     null, null, DBHelper.COLUMN_TASK_DEADLINE + " ASC")) {

            while (cursor.moveToNext()) {
                Task task = createTaskFromCursor(cursor);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching tasks", e);
        }
        Log.d(TAG, "Total tasks retrieved: " + tasks.size());
        return tasks;
    }

    /** -------------------- TASK OBJECT CREATION -------------------- */
    private Task createTaskFromCursor(Cursor cursor) {
        try {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TASK_ID));
            String taskName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TASK_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TASK_DESCRIPTION));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TASK_DEADLINE));
            int projectId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TASK_PROJECT_ID));
            int progress = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TASK_PROGRESS));

            List<Integer> assignedEmployeeIds = parseAssignedEmployeeIds(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TASK_ASSIGNED_EMPLOYEES)));

            // Safe parsing of Enum values
            Task.Priority priority = safeParsePriority(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TASK_PRIORITY)));
            Task.Status status = safeParseStatus(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TASK_STATUS)));

            return new Task(id, taskName, description, deadline, projectId, assignedEmployeeIds, priority, status, progress);
        } catch (Exception e) {
            Log.e(TAG, "Error creating task from cursor", e);
        }
        return null;
    }

    /** -------------------- ENUM SAFE PARSING -------------------- */
    private Task.Priority safeParsePriority(String priorityStr) {
        try {
            return Task.Priority.valueOf(priorityStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            Log.e(TAG, "Invalid priority value: " + priorityStr, e);
            return Task.Priority.LOW; // Default value
        }
    }

    private Task.Status safeParseStatus(String statusStr) {
        try {
            return Task.Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            Log.e(TAG, "Invalid status value: " + statusStr, e);
            return Task.Status.PENDING; // Default value
        }
    }

    /** -------------------- TASK TO CONTENTVALUES -------------------- */
    private ContentValues createTaskValues(Task task) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TASK_NAME, task.getTaskName());
        values.put(DBHelper.COLUMN_TASK_DESCRIPTION, task.getDescription());
        values.put(DBHelper.COLUMN_TASK_DEADLINE, task.getDeadline());
        values.put(DBHelper.COLUMN_TASK_PROJECT_ID, task.getProjectId());
        values.put(DBHelper.COLUMN_TASK_PRIORITY, task.getPriority().name());
        values.put(DBHelper.COLUMN_TASK_STATUS, task.getStatus().name());
        values.put(DBHelper.COLUMN_TASK_PROGRESS, task.getProgress());

        values.put(DBHelper.COLUMN_TASK_ASSIGNED_EMPLOYEES, task.getAssignedEmployeeIds().isEmpty() ?
                "" : TextUtils.join(",", task.getAssignedEmployeeIds()));

        return values;
    }

    /** -------------------- PARSE ASSIGNED EMPLOYEE IDS -------------------- */
    public List<Integer> parseAssignedEmployeeIds(String ids) {
        if (TextUtils.isEmpty(ids)) return new ArrayList<>();
        try {
            return Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.e(TAG, "Error parsing employee IDs: " + ids, e);
            return new ArrayList<>();
        }
    }
}
