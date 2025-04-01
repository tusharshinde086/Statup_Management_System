package com.example.startupmanagementsystem;

import android.os.Parcel;
import android.os.Parcelable;

public class Employee implements Parcelable {
    private int id;
    private String name;
    private String role;
    private String department;
    private String email;
    private String phone;
    private String address;
    private String dob;
    private String joiningDate;
    private double salary;

    // Constructor
    public Employee(int id, String name, String role, String department, String email,
                    String phone, String address, String dob, String joiningDate, double salary) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.department = department;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.joiningDate = joiningDate;
        this.salary = salary;
    }

    // Parcelable Constructor
    protected Employee(Parcel in) {
        id = in.readInt();
        name = in.readString();
        role = in.readString();
        department = in.readString();
        email = in.readString();
        phone = in.readString();
        address = in.readString();
        dob = in.readString();
        joiningDate = in.readString();
        salary = in.readDouble();
    }

    // Parcelable Methods
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(role);
        dest.writeString(department);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(dob);
        dest.writeString(joiningDate);
        dest.writeDouble(salary);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public Employee createFromParcel(Parcel in) {
            return new Employee(in);
        }

        @Override
        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getDepartment() { return department; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getDob() { return dob; }
    public String getJoiningDate() { return joiningDate; }
    public double getSalary() { return salary; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setDepartment(String department) { this.department = department; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setDob(String dob) { this.dob = dob; }
    public void setJoiningDate(String joiningDate) { this.joiningDate = joiningDate; }
    public void setSalary(double salary) { this.salary = salary; }
}
