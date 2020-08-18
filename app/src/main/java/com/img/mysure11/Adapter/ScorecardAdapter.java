package com.img.mysure11.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mysure11.GetSet.FantasyScoreGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class ScorecardAdapter extends BaseExpandableListAdapter {

    Context context;
    ArrayList<FantasyScoreGetSet> list,list1;

    String groupAr[];
    int icons[];
    String groupSubTitle[];

    public ScorecardAdapter(Context context, ArrayList<FantasyScoreGetSet> list, String groupAr[], int icons[], String groupSubTitle[]){
        this.context = context;
        this.list = list;
        this.groupAr = groupAr;
        this.icons = icons;
        this.groupSubTitle = groupSubTitle;
    }

    @Override
    public int getGroupCount() {
        return groupAr.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String role="";
        if(groupPosition == 0){
            role = "bat";
        }
        else if(groupPosition == 1){
            role = "ball";
        }
        else if(groupPosition == 2){
            role = "fielding";
        }
        else if(groupPosition == 3){
            role = "others";
        }
        else if(groupPosition == 4){
            role = "economy";
        }
        else if(groupPosition == 5){
            role = "strike";
        }

        list1 = new ArrayList<>();
        for(FantasyScoreGetSet zz:list){
            if(zz.getRole().equals(role))
                list1.add(zz);
        }

        return list1.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v;
        ImageView icon;
        TextView title,subtitle;

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.scorecard_group,null);

        icon = (ImageView) v.findViewById(R.id.icon);
        title =(TextView)v.findViewById(R.id.title);
        subtitle =(TextView)v.findViewById(R.id.subtitle);

        title.setText(groupAr[groupPosition]);
        if(groupSubTitle[groupPosition].equals(""))
            subtitle.setVisibility(View.GONE);
        else
            subtitle.setText(groupSubTitle[groupPosition]);
        icon.setImageResource(icons[groupPosition]);

        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v;
        TextView title,points,typeSubText;

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.scorecard_child,null);

        title = (TextView)v.findViewById(R.id.type);
        points = (TextView)v.findViewById(R.id.points);
        typeSubText = (TextView) v.findViewById(R.id.type_sub_text);

        title.setText(list1.get(childPosition).getType());
        points.setText(list1.get(childPosition).getPoints());

        if(!TextUtils.isEmpty(list1.get(childPosition).getTypeSubText())) {
            typeSubText.setVisibility(View.VISIBLE);
            typeSubText.setText(list1.get(childPosition).getTypeSubText());
        } else {
            typeSubText.setVisibility( View.GONE);
        }

        if(list1.get(childPosition).getPoints().contains("-"))
            points.setTextColor(context.getResources().getColor(R.color.colorPrimary));

        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
