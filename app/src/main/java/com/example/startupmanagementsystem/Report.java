package com.example.startupmanagementsystem;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Report {
    private final int id;
    private final String title;
    private final LocalDate date;
    private final String content;
    private List<Task> tasks;
    private List<Employee> assignedEmployees;

    // Constructor
    public Report(int id, String title, String date, String content, List<Task> tasks, EmployeeDatabase employeeDatabase) {
        this.id = validateId(id);
        this.title = validateTitle(title);
        this.date = validateDate(date);
        this.content = validateContent(content);
        setTasks(tasks, employeeDatabase);
    }

    // Validation methods
    private int validateId(int id) {
        if (id < 0) throw new IllegalArgumentException("Report ID must be non-negative.");
        return id;
    }

    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Title cannot be empty.");
        return title.trim();
    }

    private LocalDate validateDate(String date) {
        if (date == null || date.trim().isEmpty())
            throw new IllegalArgumentException("Date cannot be empty.");
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: YYYY-MM-DD");
        }
    }

    private String validateContent(String content) {
        return (content != null && !content.trim().isEmpty()) ? content.trim() : "No Content";
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public LocalDate getDate() { return date; }
    public String getContent() { return content; }
    public List<Task> getTasks() { return Collections.unmodifiableList(tasks); }
    public List<Employee> getAssignedEmployees() { return Collections.unmodifiableList(assignedEmployees); }

    // Setter for tasks (ensures tasks list is never null and updates assigned employees)
    public void setTasks(List<Task> tasks, EmployeeDatabase employeeDatabase) {
        this.tasks = (tasks != null) ? new ArrayList<>(tasks) : new ArrayList<>();
        this.assignedEmployees = extractEmployeesFromTasks(employeeDatabase);
    }

    // Extract employees from tasks (avoiding duplicates)
    private List<Employee> extractEmployeesFromTasks(EmployeeDatabase employeeDatabase) {
        if (employeeDatabase == null) return new ArrayList<>();

        return tasks.stream()
                .flatMap(task -> task.getAssignedEmployees(employeeDatabase).stream()) // Fixed error
                .distinct()
                .collect(Collectors.toList());
    }

    // Assign employees manually (ensures employees list is never null)
    public void setAssignedEmployees(List<Employee> employees) {
        this.assignedEmployees = (employees != null) ? new ArrayList<>(employees) : new ArrayList<>();
    }

    // Calculate average progress of tasks
    public int getProgress() {
        if (tasks.isEmpty()) return 0;
        return (int) tasks.stream()
                .mapToInt(Task::getProgress)
                .average()
                .orElse(0);
    }

    // Generate a text-based progress bar
    private String getProgressBar() {
        int progress = getProgress();
        int barLength = 20; // Length of the progress bar
        int filledBars = (progress * barLength) / 100; // Calculate filled parts
        return "[" + "=".repeat(filledBars) + " ".repeat(barLength - filledBars) + "] " + progress + "%";
    }

    // String representation for debugging
    @Override
    public String toString() {
        return String.format(
                "Report { ID: %d, Title: '%s', Date: '%s', Progress: %s, Assigned Employees: %d, Tasks: %d }",
                id, title, date, getProgressBar(), assignedEmployees.size(), tasks.size());
    }
}
