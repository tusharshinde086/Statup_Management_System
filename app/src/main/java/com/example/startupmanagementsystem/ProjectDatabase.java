package com.example.startupmanagementsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjectDatabase {
    private final DBHelper dbHelper;

    public ProjectDatabase(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    // ================== CRUD Operations ================== //

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT * FROM " + DBHelper.TABLE_PROJECTS;

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, null)) {

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    projects.add(createProjectFromCursor(cursor));
                }
            }
        } catch (SQLException e) {
            handleDatabaseError("Error fetching projects", e);
        }
        return projects;
    }

    public Project getProjectById(int id) {
        String query = "SELECT * FROM " + DBHelper.TABLE_PROJECTS +
                " WHERE " + DBHelper.COLUMN_PROJECT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, selectionArgs)) {

            if (cursor != null && cursor.moveToFirst()) {
                return createProjectFromCursor(cursor);
            }
        } catch (SQLException e) {
            handleDatabaseError("Error fetching project ID: " + id, e);
        }
        return null;
    }

    public long addProject(Project project) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_PROJECT_NAME, project.getName());
            values.put(DBHelper.COLUMN_PROJECT_START_TIME, project.getStartDate().toString()); // Convert LocalDate to String
            values.put(DBHelper.COLUMN_PROJECT_FINISH_TIME, project.getEndDate().toString()); // Convert LocalDate to String
            values.put(DBHelper.COLUMN_PROJECT_EMPLOYEES_NEEDED, project.getEmployeesNeeded());
            values.put(DBHelper.COLUMN_PROJECT_DESCRIPTION, project.getDescription());

            return db.insert(DBHelper.TABLE_PROJECTS, null, values);
        } catch (SQLException e) {
            handleDatabaseError("Error adding project", e);
            return -1;
        }
    }

    public int updateProject(Project project) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_PROJECT_NAME, project.getName());
            values.put(DBHelper.COLUMN_PROJECT_START_TIME, project.getStartDate().toString()); // Convert LocalDate to String
            values.put(DBHelper.COLUMN_PROJECT_FINISH_TIME, project.getEndDate().toString()); // Convert LocalDate to String
            values.put(DBHelper.COLUMN_PROJECT_EMPLOYEES_NEEDED, project.getEmployeesNeeded());
            values.put(DBHelper.COLUMN_PROJECT_DESCRIPTION, project.getDescription());

            String whereClause = DBHelper.COLUMN_PROJECT_ID + " = ?";
            String[] whereArgs = {String.valueOf(project.getId())};

            return db.update(DBHelper.TABLE_PROJECTS, values, whereClause, whereArgs);
        } catch (SQLException e) {
            handleDatabaseError("Error updating project ID: " + project.getId(), e);
            return 0;
        }
    }

    public int deleteProject(int id) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            String whereClause = DBHelper.COLUMN_PROJECT_ID + " = ?";
            String[] whereArgs = {String.valueOf(id)};
            return db.delete(DBHelper.TABLE_PROJECTS, whereClause, whereArgs);
        } catch (SQLException e) {
            handleDatabaseError("Error deleting project ID: " + id, e);
            return 0;
        }
    }

    // ================== Helper Methods ================== //

    private Project createProjectFromCursor(Cursor cursor) {
        return new Project(
                cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PROJECT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PROJECT_NAME)),
                LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PROJECT_START_TIME))), // Convert String to LocalDate
                LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PROJECT_FINISH_TIME))), // Convert String to LocalDate
                cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PROJECT_EMPLOYEES_NEEDED)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PROJECT_DESCRIPTION))
        );
    }
    public String getProjectNameById(int projectId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String projectName = null;

        String query = "SELECT name FROM projects WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(projectId)});

        if (cursor.moveToFirst()) {
            projectName = cursor.getString(0);
        }
        cursor.close();
        db.close();

        return projectName != null ? projectName : "Unknown Project";
    }

    private void handleDatabaseError(String message, Exception e) {
        e.printStackTrace();
    }
}
