package com.img.mysure11.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mysure11.GetSet.notificationGetSet;
import com.img.mysure11.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class NotificationListAdapter extends BaseAdapter {

    private Context context;
    ArrayList<notificationGetSet> notificationList = new ArrayList <>();

    public NotificationListAdapter(Context context, ArrayList<notificationGetSet> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View v;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.card_view_notification_list_item, null);
        } else
            v = (View) view;

        ImageView imageViewLogo = (ImageView) v.findViewById(R.id.image_view_logo);
        TextView textViewTitle = (TextView) v.findViewById(R.id.text_view_title);
        TextView textViewDate = (TextView) v.findViewById(R.id.text_view_date);

        textViewTitle.setText("" + notificationList.get(position).getMessage());
        textViewDate.setText("" + getFormattedDate(notificationList.get(position).getPrevious()));

        return v;
    }

    public String getFormattedDate(String date) {

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int mYear1 = c.get(Calendar.YEAR);
        int mMonth1 = c.get(Calendar.MONTH);
        int mDay1 = c.get(Calendar.DAY_OF_MONTH);

        String sDate = mYear1 + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec;

        Log.e("TODAY_DATE", "" + sDate);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

        Date ddate = new Date();
        try {
            ddate = sdf.parse(date);
            System.out.println("Date in milli :: "+ddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar smsTime = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));

        long smsTimeInMilis = 0;
        smsTimeInMilis = ddate.getTime();
        smsTime.setTimeInMillis(smsTimeInMilis);
        smsTime.add(Calendar.MINUTE,330);

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "dd MMM";
        if (c.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return ""+DateFormat.format(timeFormatString, smsTime);//Today
        } else if (c.get(Calendar.DATE)-smsTime.get(Calendar.DATE) == 1) {
            return ""+DateFormat.format(dateTimeFormatString, smsTime);//Yesterday
        } else if (c.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        }
    }

}
