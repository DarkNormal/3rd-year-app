package project.b_ourguest.bourguest;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import project.b_ourguest.bourguest.Model.Bookings;
import project.b_ourguest.bourguest.ui.ConfirmBookingFrag;

/**
 * Created by Robbie on 02/04/2015.
 */
public class User_Bookings_Activity extends ActionBarActivity {
    private List<Bookings> bookings = SignInActivity.getBookings();
    boolean fromBooking;
    private int time,day,month,year,numPeople;
    private long timeInMillis;
    private FragmentManager fragmentManager;
    private ConfirmBookingFrag frag;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Your bookings");
        setContentView(R.layout.user_bookings_layout);

        Bundle extras = getIntent().getExtras();
        userID = extras.getString("userID");
        fromBooking = extras.getBoolean("fromBooking");
        time = extras.getInt("time");
        day = extras.getInt("day");
        month = extras.getInt("month");
        year = extras.getInt("year");
        int numTables = extras.getInt("numTables");
        numPeople = extras.getInt("numPeople");
        timeInMillis = extras.getLong("timeInMillis");

        if(fromBooking == true)
        {
            Bundle bundle = new Bundle();
            bundle.putString("userID",userID);
            bundle.putInt("time", time);
            bundle.putInt("day", day);
            bundle.putInt("month", month);
            bundle.putInt("numTables",numTables);
            bundle.putInt("numPeople", numPeople);
            bundle.putInt("year", year);
            bundle.putLong("timeInMillis",timeInMillis);
            frag = (ConfirmBookingFrag) getFragmentManager().findFragmentByTag("frag");
            if(frag == null)
            {
                fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                frag = new ConfirmBookingFrag();
                frag.setArguments(bundle);
                fragmentTransaction.add(R.id.fragmentContainer, frag, "frag");
                fragmentTransaction.commit();
            }
            else
            {
                fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.fragmentContainer, frag);
                ft.commit();
            }

        }
        else {
            if (bookings.size() == 0) {
                setContentView(R.layout.no_bookings_layout);
            } else {
                ArrayAdapter<Bookings> adapter = new BookingsAdapter();
                ListView list = (ListView) findViewById(R.id.userBookingsListView);
                list.setAdapter(adapter);
            }
        }
    }

    public void confirm(View v)
    {
        fragmentManager.beginTransaction().remove(frag).commit();
        if (bookings.size() == 0) {
            setContentView(R.layout.no_bookings_layout);
        } else {
            ArrayAdapter<Bookings> adapter = new BookingsAdapter();
            ListView list = (ListView) findViewById(R.id.userBookingsListView);
            list.setAdapter(adapter);
        }
    }

    private class BookingsAdapter extends ArrayAdapter<Bookings> {

        public BookingsAdapter() {
            super(User_Bookings_Activity.this,R.layout.bookings_listview_layout,bookings);

        }

        @Override
        public View getView(int position, View convertView,ViewGroup parent){
            //this makes sure we have a view to work with
            View v = convertView;
            if(v == null) //create new view
            {
                v = getLayoutInflater().inflate(R.layout.bookings_listview_layout, parent, false);
            }
            TextView user = (TextView) v.findViewById(R.id.usersID);
            user.setText(bookings.get(position).getUserID());

            TextView numTables = (TextView) v.findViewById(R.id.numTables);
            numTables.setText("Number of tables booked: " + bookings.get(position).getNumTables());

            TextView numPeople = (TextView) v.findViewById(R.id.numPeople);
            numPeople.setText("Number of guests: " + bookings.get(position).getNumPeople());

            String ti = "" + bookings.get(position).getTime();
            String string = "";
            for(int i = 0; i < ti.length(); i++)
            {
                string += ti.charAt(i);
                if(i == 1)
                    string += ":";
            }

            TextView dateAndTime = (TextView) v.findViewById(R.id.dateAndTime);
            dateAndTime.setText("Date: " + bookings.get(position).getDay() + "/" + (bookings.get(position).getMonth() + 1) + "/" +
                    bookings.get(position).getYear() + "\tTime: " + string);
            return v;
        }
    }

}
