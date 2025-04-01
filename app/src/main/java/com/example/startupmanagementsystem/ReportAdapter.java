package com.example.startupmanagementsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends ArrayAdapter<Report> {

    public ReportAdapter(Context context, List<Report> reports) {
        super(context, 0, reports != null ? reports : new ArrayList<>());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.report_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Report report = getItem(position);
        if (report != null) {
            holder.bind(report);
        }

        return convertView;
    }

    private static class ViewHolder {
        private final TextView titleTextView;
        private final TextView dateTextView;
        private final TextView contentTextView;
        private final TextView employeeTextView;
        private final TextView progressTextView;

        ViewHolder(View view) {
            titleTextView = view.findViewById(R.id.reportTitle);
            dateTextView = view.findViewById(R.id.reportDate);
            contentTextView = view.findViewById(R.id.reportContent);
            employeeTextView = view.findViewById(R.id.reportEmployees);
            progressTextView = view.findViewById(R.id.reportProgressText);
        }

        void bind(Report report) {
            titleTextView.setText(report.getTitle());
            dateTextView.setText(report.getDate().toString());
            contentTextView.setText(report.getContent());

            // Handle assigned employees safely
            List<Employee> employees = report.getAssignedEmployees();
            if (employees == null || employees.isEmpty()) {
                employeeTextView.setText("No employees assigned");
            } else {
                StringBuilder employeeNames = new StringBuilder("Employees: ");
                for (int i = 0; i < employees.size(); i++) {
                    employeeNames.append(employees.get(i).getName());
                    if (i < employees.size() - 1) {
                        employeeNames.append(", ");
                    }
                }
                employeeTextView.setText(employeeNames.toString());
            }

            // Display progress percentage
            progressTextView.setText(String.format("Progress: %d%%", report.getProgress()));
        }
    }
}
