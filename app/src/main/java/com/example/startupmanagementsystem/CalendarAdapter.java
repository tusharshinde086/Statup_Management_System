package com.example.startupmanagementsystem;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CalendarAdapter extends BaseAdapter {

    private final Context context;
    private final List<Integer> daysOfMonth;
    private final Map<String, Integer> taskFrequency;
    private final Calendar currentCalendar;

    // Constructor using Calendar object
    public CalendarAdapter(Context context, Map<String, Integer> taskFrequency, Calendar calendar) {
        this.context = context;
        this.taskFrequency = taskFrequency;
        this.currentCalendar = (Calendar) calendar.clone();
        this.daysOfMonth = getMonthDays(calendar);
    }

    // Constructor using selected month and year
    public CalendarAdapter(Context context, int selectedMonth, int selectedYear, Map<String, Integer> taskFrequency) {
        this.context = context;
        this.taskFrequency = taskFrequency;
        this.currentCalendar = Calendar.getInstance();
        this.currentCalendar.set(Calendar.YEAR, selectedYear);
        this.currentCalendar.set(Calendar.MONTH, selectedMonth);
        this.daysOfMonth = getMonthDays(this.currentCalendar);
    }

    @Override
    public int getCount() {
        return daysOfMonth.size();
    }

    @Override
    public Integer getItem(int position) {
        return daysOfMonth.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int day = getItem(position);
        holder.dayTextView.setText(String.valueOf(day));

        // Generate date key in YYYY-MM-DD format
        String dateKey = getDateKey(day);
        int taskCount = taskFrequency.getOrDefault(dateKey, 0);

        // Set background color based on task count
        int color = getTaskDensityColor(taskCount);
        holder.dayTextView.setBackgroundColor(color);

        return convertView;
    }

    private static class ViewHolder {
        private final TextView dayTextView;

        ViewHolder(View view) {
            dayTextView = view.findViewById(R.id.dayTextView);
        }
    }

    // Generate date key in YYYY-MM-DD format
    private String getDateKey(int day) {
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) + 1; // Months are 0-based

        return String.format("%04d-%02d-%02d", year, month, day);
    }

    // Determine color based on task density
    private int getTaskDensityColor(int taskCount) {
        if (taskCount == 0) return Color.LTGRAY;  // No tasks
        else if (taskCount == 1) return Color.parseColor("#D6E685"); // Light Green
        else if (taskCount <= 3) return Color.parseColor("#8CC665"); // Medium Green
        else if (taskCount <= 6) return Color.parseColor("#44A340"); // Dark Green
        else return Color.parseColor("#1E6823"); // Very Dark Green
    }

    // Get days of the selected month
    private List<Integer> getMonthDays(Calendar calendar) {
        List<Integer> days = new ArrayList<>();
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= maxDay; i++) {
            days.add(i);
        }
        return days;
    }
}
