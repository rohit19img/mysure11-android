package com.img.mysure11.Static;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.img.mysure11.Fragment.FantasyODIFragment;
import com.img.mysure11.Fragment.FantasyT10Fragment;
import com.img.mysure11.Fragment.FantasyT20Fragment;
import com.img.mysure11.Fragment.FantasyTESTFragment;
import com.img.mysure11.R;

public class FantasyPointSystemActivity extends AppCompatActivity {

    TabLayout tab;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fantasy_point_system);

        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView title =(TextView)findViewById(R.id.title);
        title.setText("Fantasy Point System");

        tab=(TabLayout)findViewById(R.id.tab);
        viewPager=(ViewPager) findViewById(R.id.viewPager);
        tab.setupWithViewPager(viewPager);
        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));

    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FantasyT20Fragment();
                case 1:
                    return new FantasyODIFragment();
                case 2:
                    return new FantasyTESTFragment();
                default:
                    return new FantasyT10Fragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "T20";
                case 1:
                    return "ODI";
                case 2:
                    return "TEST";
                default:
                    return "T10";
            }
        }
    }

}
