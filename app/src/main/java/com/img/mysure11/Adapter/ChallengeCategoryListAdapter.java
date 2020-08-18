package com.img.mysure11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mysure11.Activity.AllChallengesActivity;
import com.img.mysure11.GetSet.contestCategoriesGetSet;
import com.img.mysure11.GetSet.contestGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChallengeCategoryListAdapter extends RecyclerView.Adapter<ChallengeCategoryListAdapter.MyViewHolder>{

    Context context;
    ArrayList<contestCategoriesGetSet> list;

    public ChallengeCategoryListAdapter(Context context, ArrayList<contestCategoriesGetSet> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.challenge_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int i) {

        ArrayList<contestGetSet> list1 = list.get(i).getContest();

        holder.title.setText(list.get(i).getCatname());
        holder.subtitle.setText(list.get(i).getSub_title());
        Picasso.with(context).load(list.get(i).getImage()).into(holder.logo);

        if (list1.size() > 3) {
            holder.viewMore.setVisibility(View.VISIBLE);
            holder.viewMore.setText((list1.size() - 3) + " more contests");
        } else
            holder.viewMore.setVisibility(View.GONE);

        holder.viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(context, AllChallengesActivity.class);
                ii.putExtra("catid",String.valueOf(list.get(i).getCatid()));
                context.startActivity(ii);
            }
        });

        holder.contestsList.setLayoutManager(new LinearLayoutManager(context));
        holder.contestsList.setAdapter(new contests3Adapter(context,list1));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title, subtitle, viewMore;
        ImageView logo;
        RecyclerView contestsList;

        public MyViewHolder(View v){
            super(v);

            title = (TextView) v.findViewById(R.id.title);
            subtitle = (TextView) v.findViewById(R.id.subtitle);
            viewMore = (TextView) v.findViewById(R.id.viewMore);
            logo = (ImageView) v.findViewById(R.id.logo);
            contestsList = v.findViewById(R.id.contestsList);
        }
    }
}
