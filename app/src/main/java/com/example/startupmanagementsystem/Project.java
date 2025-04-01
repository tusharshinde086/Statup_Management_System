package com.example.startupmanagementsystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a project with details including ID, name, dates, staffing needs, and description.
 */
public class Project {
    private int id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private int employeesNeeded;
    private String description;
    private List<Task> tasks;  // Added a field to store tasks

    /**
     * Constructs a Project with validated parameters.
     *
     * @param id               Unique identifier (must be non-negative)
     * @param name             Project name (cannot be empty)
     * @param startDate        Project start date (must not be null)
     * @param endDate          Project end date (must be after startDate)
     * @param employeesNeeded  Required staff count (non-negative)
     * @param description      Project description
     * @throws IllegalArgumentException if any validation fails
     */
    public Project(int id, String name, LocalDate startDate, LocalDate endDate,
                   int employeesNeeded, String description) {
        this.id = validateId(id);
        this.name = validateName(name);
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null");
        this.endDate = Objects.requireNonNull(endDate, "End date cannot be null");
        validateDates(startDate, endDate);
        this.employeesNeeded = validateEmployeesNeeded(employeesNeeded);
        this.description = description != null ? description.trim() : "";
        this.tasks = new ArrayList<>(); // Initialize tasks list
    }

    // Validation helper methods
    private int validateId(int id) {
        if (id < 0) throw new IllegalArgumentException("ID must be non-negative");
        return id;
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        return name.trim();
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
    }

    private int validateEmployeesNeeded(int employees) {
        if (employees < 0) throw new IllegalArgumentException("Employees needed cannot be negative");
        return employees;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = validateId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = validateName(name);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null");
        validateDates(this.startDate, this.endDate);
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = Objects.requireNonNull(endDate, "End date cannot be null");
        validateDates(this.startDate, this.endDate);
    }

    public int getEmployeesNeeded() {
        return employeesNeeded;
    }

    public void setEmployeesNeeded(int employeesNeeded) {
        this.employeesNeeded = validateEmployeesNeeded(employeesNeeded);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }

    // Added getter and setter for tasks
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", employeesNeeded=" + employeesNeeded +
                ", description='" + description + '\'' +
                ", tasks=" + (tasks != null ? tasks.size() + " tasks" : "No tasks") +
                '}';
    }

}
