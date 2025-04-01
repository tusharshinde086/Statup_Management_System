package com.example.startupmanagementsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private final Context context;
    private List<Employee> employeeList;

    public EmployeeAdapter(Context context, List<Employee> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.bind(employee);
    }

    @Override
    public int getItemCount() {
        return (employeeList != null) ? employeeList.size() : 0;
    }

    public void updateList(List<Employee> newList) {
        this.employeeList = newList;
        notifyDataSetChanged();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvRole, tvDepartment, tvEmail, tvPhone,
                tvAddress, tvDob, tvJoiningDate, tvSalary;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);

            // Matching TextView IDs with item_employee.xml
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDob = itemView.findViewById(R.id.tvDob);
            tvJoiningDate = itemView.findViewById(R.id.tvJoiningDate);
            tvSalary = itemView.findViewById(R.id.tvSalary);
        }

        public void bind(Employee employee) {
            tvName.setText(employee.getName());
            tvRole.setText("Role: " + employee.getRole());
            tvDepartment.setText("Department: " + employee.getDepartment());
            tvEmail.setText("Email: " + employee.getEmail());
            tvPhone.setText("Phone: " + employee.getPhone());
            tvAddress.setText("Address: " + employee.getAddress());
            tvDob.setText("DOB: " + employee.getDob());
            tvJoiningDate.setText("Joined: " + employee.getJoiningDate());
            tvSalary.setText("Salary: $" + employee.getSalary());
        }
    }
}
