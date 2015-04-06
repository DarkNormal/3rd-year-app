package project.b_ourguest.bourguest.ui;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

import project.b_ourguest.bourguest.MainActivity;
import project.b_ourguest.bourguest.R;

/**
 * Created by Robbie on 03/04/2015.
 */
public class ConfirmBookingFrag extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        View v = inflater.inflate(R.layout.booking_confirmation_layout,container,false);
        int time = getArguments().getInt("time");
        int day = getArguments().getInt("day");
        int month = getArguments().getInt("month");
        int year = getArguments().getInt("year");
        long timeInMillis = getArguments().getLong("timeInMillis");
        TextView t = (TextView) v.findViewById(R.id.confirmation);
        t.setText("Booking Confirmed For\nTime: " + time + "\nDate: " + day + "/" + (month + 1) + "/" + year);
        System.out.println("TIME: " + time);
        System.out.println("TIME: " + time * 1000);
        timeInMillis += (time * 10) * 60 * 60;
        long endDate = timeInMillis + 2000 * 60 * 60;
        addReminder("Your reservation for " + MainActivity.getRestaurantToPass().getName(),"Reservation for "
                + MainActivity.getRestaurantToPass().getName() + " starting at " + time
                ,MainActivity.getRestaurantToPass().getName(),timeInMillis,endDate);
        return v;
    }

    public  void addReminder(String title,String description,String location,long startTime,long endTime) {

        ContentResolver cr = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startTime);
        values.put(CalendarContract.Events.DTEND, endTime);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.EVENT_LOCATION, location);
        values.put(CalendarContract.Events.ALL_DAY, false);
        values.put(CalendarContract.Events.HAS_ALARM, true);
        //Get current timezone
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());

        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminders.put(CalendarContract.Reminders.MINUTES, 30);

        Uri uri2 = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
    }
}