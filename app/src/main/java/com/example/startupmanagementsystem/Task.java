package com.example.startupmanagementsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Task {

    public enum Priority { LOW, NORMAL, HIGH }
    public enum Status { PENDING, IN_PROGRESS, COMPLETED }

    private int id;
    private String taskName;
    private String description;
    private String deadline;
    private int projectId;
    private List<Integer> assignedEmployeeIds;
    private Priority priority;
    private Status status;
    private int progress;

    // Default constructor
    public Task() {
        this(-1, "Untitled Task", "No Description", "", 0, new ArrayList<>(), Priority.LOW, Status.PENDING, 0);
    }

    // Constructor with required parameters
    public Task(String taskName, String description, String deadline, int projectId, List<Integer> assignedEmployeeIds) {
        this(-1, taskName, description, deadline, projectId, assignedEmployeeIds, Priority.LOW, Status.PENDING, 0);
    }

    // Constructor with all parameters
    public Task(int id, String taskName, String description, String deadline, int projectId, List<Integer> assignedEmployeeIds, Priority priority, Status status, int progress) {
        this.id = id;  // Allow -1 for new tasks
        this.taskName = validateTaskName(taskName);
        this.description = validateDescription(description);
        this.deadline = validateDeadline(deadline);
        this.projectId = projectId;
        this.assignedEmployeeIds = assignedEmployeeIds != null ? new ArrayList<>(assignedEmployeeIds) : new ArrayList<>();
        this.priority = validatePriority(priority);
        this.status = validateStatus(status);
        this.progress = validateProgress(progress);
    }

    // Fetch assigned employees as Employee objects
    public List<Employee> getAssignedEmployees(EmployeeDatabase employeeDatabase) {
        List<Employee> employees = new ArrayList<>();
        if (employeeDatabase != null) {
            for (int employeeId : assignedEmployeeIds) {
                Employee employee = employeeDatabase.getEmployeeById(employeeId);
                if (employee != null) {
                    employees.add(employee);
                }
            }
        }
        return employees;
    }

    // Validation methods
    private String validateTaskName(String taskName) {
        return (taskName != null && !taskName.trim().isEmpty()) ? taskName.trim() : "Untitled Task";
    }

    private String validateDescription(String description) {
        return (description != null) ? description.trim() : "No Description";
    }

    private String validateDeadline(String deadline) {
        if (deadline == null || deadline.trim().isEmpty()) return "";
        if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", deadline)) {
            return deadline.trim(); // Valid YYYY-MM-DD
        } else if (Pattern.matches("\\d{2}-\\d{2}-\\d{4}", deadline)) {
            // Convert from DD-MM-YYYY to YYYY-MM-DD
            String[] parts = deadline.split("-");
            return parts[2] + "-" + parts[1] + "-" + parts[0];
        } else {
            throw new IllegalArgumentException("Invalid deadline format. Use YYYY-MM-DD.");
        }
    }

    private Priority validatePriority(Priority priority) {
        return (priority != null) ? priority : Priority.NORMAL;
    }

    private Status validateStatus(Status status) {
        return (status != null) ? status : Status.IN_PROGRESS;
    }

    private int validateProgress(int progress) {
        return Math.max(0, Math.min(progress, 100)); // Clamp between 0-100
    }

    // Getters
    public int getId() { return id; }
    public String getTaskName() { return taskName; }
    public String getDescription() { return description; }
    public String getDeadline() { return deadline; }
    public int getProjectId() { return projectId; }
    public List<Integer> getAssignedEmployeeIds() { return new ArrayList<>(assignedEmployeeIds); } // Prevent modification
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public int getProgress() { return progress; }

    // Setters
    public void setTaskName(String taskName) { this.taskName = validateTaskName(taskName); }
    public void setDescription(String description) { this.description = validateDescription(description); }
    public void setDeadline(String deadline) { this.deadline = validateDeadline(deadline); }
    public void setPriority(Priority priority) { this.priority = validatePriority(priority); }
    public void setStatus(Status status) { this.status = validateStatus(status); }
    public void setProgress(int progress) { this.progress = validateProgress(progress); }
    public void setAssignedEmployeeIds(List<Integer> employeeIds) {
        this.assignedEmployeeIds = (employeeIds != null) ? new ArrayList<>(employeeIds) : new ArrayList<>();
    }

    // Equals and hashCode for object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Task)) return false;
        Task task = (Task) obj;
        return id == task.id &&
                projectId == task.projectId &&
                taskName.equals(task.taskName) &&
                Objects.equals(description, task.description) &&
                Objects.equals(deadline, task.deadline) &&
                assignedEmployeeIds.equals(task.assignedEmployeeIds) &&
                priority == task.priority &&
                status == task.status &&
                progress == task.progress;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, description, deadline, projectId, assignedEmployeeIds, priority, status, progress);
    }

    // Improved toString() for debugging
    @Override
    public String toString() {
        return String.format("Task { ID: %d, Task Name: '%s', Description: '%s', Deadline: '%s', Project ID: %d, Assigned Employees: %s, Priority: '%s', Status: '%s', Progress: %d }",
                id, taskName, description, deadline, projectId,
                assignedEmployeeIds.isEmpty() ? "None" : assignedEmployeeIds, priority, status, progress);
    }
}
