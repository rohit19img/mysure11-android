package com.img.mysure11.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.img.mysure11.GetSet.bannersGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class SlideBannerAdapter extends PagerAdapter {

    LayoutInflater inflater;
    Context context;
    ArrayList<bannersGetSet> list;

    public SlideBannerAdapter(Context context, ArrayList<bannersGetSet> list )
    {
        this.context=context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        ImageView imgflag;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.banner_item,null);
        imgflag = (ImageView) itemView.findViewById(R.id.imageView);

        Picasso.with(context).load(list.get(position).getImage()).into(imgflag);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);
    }
}
