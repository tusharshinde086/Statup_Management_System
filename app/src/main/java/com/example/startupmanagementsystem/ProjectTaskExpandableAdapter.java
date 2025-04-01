package com.example.startupmanagementsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ProjectTaskExpandableAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final List<String> projectList;
    private final Map<String, List<String>> taskMap;

    public ProjectTaskExpandableAdapter(Context context, List<String> projectList, Map<String, List<String>> taskMap) {
        this.context = context;
        this.projectList = projectList;
        this.taskMap = taskMap;
    }

    @Override
    public int getGroupCount() {
        return projectList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return taskMap.get(projectList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return projectList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return taskMap.get(projectList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText((String) getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText((String) getChild(groupPosition, childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
