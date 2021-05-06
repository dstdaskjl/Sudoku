package edu.psu.sxa5362.sudoku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

@SuppressLint("UseSparseArrays")
public class RankingExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private Map<String, List<Result>> resultMap;
    private Map<String, String> uidMap;

    private ChildViewHolder childViewHolder;
    private ParentViewHolder parentViewHolder;

    private String parentText;
    private String childText;

    public RankingExpandableListViewAdapter(Context context, Map<String, List<Result>> resultMap, Map<String, String> uidMap){
        this.context = context;
        this.resultMap = resultMap;
        this.uidMap = uidMap;
    }

    @Override
    public int getGroupCount() {
        return resultMap.size();
    }

    @Override
    public String getGroup(int groupPosition) {
        if (groupPosition == 0){
            return "Easy";
        }
        else if (groupPosition == 1){
            return "Medium";
        }
        return "Hard";
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        parentText = getGroup(groupPosition);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ranking_parent, null);

            // Initialize the ParentViewHolder defined at the bottom of this document
            parentViewHolder = new ParentViewHolder();

            parentViewHolder.parentTextView = (TextView) convertView.findViewById(R.id.ranking_parent_text);

            convertView.setTag(parentViewHolder);
        } else {

            parentViewHolder = (ParentViewHolder) convertView.getTag();
        }

        parentViewHolder.parentTextView.setText(parentText);
        parentViewHolder.parentTextView.setTypeface(null, Typeface.BOLD);
        parentViewHolder.parentTextView.setAlpha(0.9f);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return resultMap.get(getGroup(groupPosition)).size();
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        Result result = resultMap.get(getGroup(groupPosition)).get(childPosition);
        return uidMap.get(result.getUid()) + "!" + convertTime(result.getTime());
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        childText = getChild(groupPosition, childPosition);
        String[] textArray = childText.split("!");

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ranking_child, null);

            childViewHolder = new ChildViewHolder();
            childViewHolder.childNameTextView = (TextView) convertView.findViewById(R.id.ranking_child_text_nickname);
            childViewHolder.childTimeTextView = (TextView) convertView.findViewById(R.id.ranking_child_text_time);

            convertView.setTag(R.layout.ranking_child, childViewHolder);

        } else {

            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.ranking_child);
        }

        childViewHolder.childNameTextView.setText((childPosition + 1) + ". " + textArray[0]);
        childViewHolder.childTimeTextView.setText(textArray[1]);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public final class ParentViewHolder {
        TextView parentTextView;
    }

    public final class ChildViewHolder {
        TextView childNameTextView;
        TextView childTimeTextView;
    }

    private String convertTime(long time){
        int seconds = (int) (time / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }
}