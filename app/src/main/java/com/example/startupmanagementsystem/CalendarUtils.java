package com.example.startupmanagementsystem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarUtils {

    // Returns a list of all days in the current month
    public static List<Integer> getCurrentMonthDays() {
        List<Integer> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= maxDay; day++) {
            days.add(day);
        }
        return days;
    }
    // Get all days for a given month
    public static List<Integer> getMonthDays(Calendar calendar) {
        List<Integer> daysOfMonth = new ArrayList<>();

        // Clone the calendar to avoid modifying the original
        Calendar tempCalendar = (Calendar) calendar.clone();

        // Set day to 1 and get max days in the month
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int maxDays = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add days to list
        for (int i = 1; i <= maxDays; i++) {
            daysOfMonth.add(i);
        }

        return daysOfMonth;
    }
    public static List<Integer> getMonthDays(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        List<Integer> days = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(i);
        }
        return days;
    }

    // Get current month and year as a formatted string (YYYY-MM)
    public static String getCurrentMonthYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "-" + String.format("%02d", (calendar.get(Calendar.MONTH) + 1));
    }
}
