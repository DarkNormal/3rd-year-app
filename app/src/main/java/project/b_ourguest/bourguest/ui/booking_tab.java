package project.b_ourguest.bourguest.ui;
/**
 * Created by Mark on 3/28/2015.
 */
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.Calendar;

import project.b_ourguest.bourguest.BookingActivity;
import project.b_ourguest.bourguest.R;

public class booking_tab extends Fragment {
    private Calendar calendar = Calendar.getInstance();
    private CalendarView cal;
    private long date;
    private static String time;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.booking_tab,container,false);

        cal = (CalendarView) v.findViewById(R.id.calendar);
        //The background color for the selected week.
        cal.setSelectedWeekBackgroundColor(Color.parseColor("#ff2b8bff"));


        date = cal.getDate();
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //http://stackoverflow.com/questions/12641250/android-calendarview-ondatechangelistener?rq=1

                if(cal.getDate() != date){
                    date = cal.getDate();

                    if(month < calendar.get(Calendar.MONTH))
                        Toast.makeText(getActivity().getApplicationContext(), "Cannot pick a date in the past", Toast.LENGTH_SHORT).show();
                    else if(year < calendar.get(Calendar.YEAR))
                        Toast.makeText(getActivity().getApplicationContext(),"Cannot pick a date in the past",Toast.LENGTH_SHORT).show();
                    else if(dayOfMonth < calendar.get(Calendar.DAY_OF_MONTH) && month == calendar.get(Calendar.MONTH))
                        Toast.makeText(getActivity().getApplicationContext(),"Cannot pick a date in the past",Toast.LENGTH_SHORT).show();
                    else if(month > calendar.get(Calendar.MONTH) + 3)
                        Toast.makeText(getActivity().getApplicationContext(),"Cannot pick a date too far in advance",Toast.LENGTH_SHORT).show();
                    else if(year > calendar.get(Calendar.YEAR))
                    {
                        if(calendar.get(Calendar.MONTH) >= 10 && month < month - 10)
                            createPopUp();
                        else
                            Toast.makeText(getActivity().getApplicationContext(),"Cannot pick a date too far in advance",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        createPopUp();
                    }
                }
            }
        });

    return v;
    }

    private void createPopUp() {
        //Creating the instance of PopupMenu
        View v =  getView().findViewById(R.id.anchor);
        PopupMenu popup = new PopupMenu(getActivity(), v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.times_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                time = item.getTitle().toString();
                Intent intent = new Intent(getActivity().getApplicationContext(), BookingActivity.class);
                startActivity(intent);


                return true;
            }
        });

        popup.show();//showing popup menu
    }
}