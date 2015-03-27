package project.b_ourguest.bourguest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

/**
 * Created by Robbie on 17/12/2014.
 */
public class RestaurantActivity extends ActionBarActivity {
    //http://stackoverflow.com/questions/3496269/how-to-put-a-border-around-an-android-textview
    //link that helped with borders on textviews
    Restaurant r = MainActivity.getRestaurantToPass();
    DatabaseOperations db = new DatabaseOperations();
    private GoogleMap map;
    private long date;
    private Reviews review;
    TableLayout t;
    private UserReviews userReview;
    private static String time;
    private Calendar calendar = Calendar.getInstance();
    private CalendarView cal;
    private boolean first = true,second = true,third = true; //these booleans indicate whether or not one of the tabs
    //has been pressed for the first time. These are used to hide the layouts depending on if it is their first time or not
    private RelativeLayout infoLayout,reviewLayout,bookingLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_activity_display);
        SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
        String userID = settings.getString("email", "").toString();
        setTitle(convertToTitleCase(r.getName()));
        db.getReview(userID,r.getId());
    }
    
    @Override
    public void onBackPressed() {
        finish(); //this will finish the activity when the back button is pressed
        super.onBackPressed();
    }
    
    public String convertToTitleCase(String name) {
        String[] partOfName = name.split(" ");
        char upperCaseLetter;
        name = "";
        String sub;
        for(int i = 0; i < partOfName.length; i++)
        {
            upperCaseLetter = Character.toUpperCase(partOfName[i].charAt(0));
            sub = partOfName[i].substring(1,partOfName[i].length());
            name = name + (upperCaseLetter + sub) + " ";
        }
        return name;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }
    
    public void changeTabs(View v)
    {
        RelativeLayout tabbedLayout = (RelativeLayout)findViewById(R.id.tabsLayout);
        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        
        if(v == v.findViewById(R.id.infoTab))
        {
            if(first)
            {
                tabbedLayout.addView(layoutInflater.inflate(R.layout.info_tab, null));
                infoLayout = (RelativeLayout)findViewById(R.id.infoLayout);
                try {
                    // Loading map
                    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
                    
                    Marker loc = map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(MainActivity.getRestaurantToPass().getLatitude()),
                                                                                       Double.parseDouble(MainActivity.getRestaurantToPass().getLongitude())))
                                               .title(r.getName()));
                    
                    // Move the camera instantly to RESTAURANTS_LOCATION with a zoom of 15.
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(MainActivity.getRestaurantToPass().getLatitude()),
                                                                                Double.parseDouble(MainActivity.getRestaurantToPass().getLongitude())), 15));
                    
                    // Zoom in, animating the camera.
                    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                TextView bio = (TextView) findViewById(R.id.restaurantBio);
                bio.setText(r.getBio());
                
                
                TextView bio2 = (TextView) findViewById(R.id.test);
                bio2.setText(r.getBio());
                first = false;
            }
            
            if(!second && third)
                bookingLayout.setVisibility(View.GONE);
            
            else if(second && !third)
                reviewLayout.setVisibility(View.GONE);
            else if(!second && !third)
            {
                reviewLayout.setVisibility(View.GONE);
                bookingLayout.setVisibility(View.GONE);
            }
            
            //reviewLayout.setVisibility(View.GONE);
            //bookingLayout
            // .setVisibility(View.GONE);
            infoLayout.setVisibility(View.VISIBLE);
        }
        else if(v == v.findViewById(R.id.bookingTab)) {
            
            if(second)
            {
                
                tabbedLayout.addView(layoutInflater.inflate(R.layout.booking_tab, null));
                bookingLayout = (RelativeLayout)findViewById(R.id.bookingsTab);
                
                cal = (CalendarView) findViewById(R.id.calendar);
                //The background color for the selected week.
                cal.setSelectedWeekBackgroundColor(Color.parseColor("#ff2b8bff"));
                
                
                date = cal.getDate();
                cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                        //http://stackoverflow.com/questions/12641250/android-calendarview-ondatechangelistener?rq=1
                        
                        if(cal.getDate() != date){
                            date = cal.getDate();
                            
                            
                            System.out.println("day is :" + calendar.getTime().getDay() + " " + dayOfMonth);
                            System.out.println("day is :" + calendar.getTime());
                            
                            if(month < calendar.getTime().getMonth())
                                Toast.makeText(RestaurantActivity.this,"Cannot pick a date in the past",Toast.LENGTH_SHORT).show();
                            else if(dayOfMonth < calendar.getTime().getDay() + 1 && month == calendar.getTime().getMonth())
                                Toast.makeText(RestaurantActivity.this,"Cannot pick a date in the past",Toast.LENGTH_SHORT).show();
                            else {
                                //Creating the instance of PopupMenu
                                View v = (View) findViewById(R.id.passedRestaurantName);
                                PopupMenu popup = new PopupMenu(getApplicationContext(), v);
                                //Inflating the Popup using xml file
                                popup.getMenuInflater().inflate(R.menu.times_menu, popup.getMenu());
                                
                                //registering popup with OnMenuItemClickListener
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    public boolean onMenuItemClick(MenuItem item) {
                                        
                                        time = item.getTitle().toString();
                                        Intent intent = new Intent(RestaurantActivity.this, BookingActivity.class);
                                        startActivity(intent);
                                        
                                        
                                        return true;
                                    }
                                });
                                
                                popup.show();//showing popup menu
                            }
                        }
                    }
                });
                
                
                second = false;
            }
            
            if(!first && third)
                infoLayout.setVisibility(View.GONE);
            
            else if(first && !third)
                reviewLayout.setVisibility(View.GONE);
            else if(!first && !third)
            {
                reviewLayout.setVisibility(View.GONE);
                infoLayout.setVisibility(View.GONE);
            }
            
            bookingLayout.setVisibility(View.VISIBLE);
        }
        else {
            if(third)
            {
                tabbedLayout.addView(layoutInflater.inflate(R.layout.reviews_tab, null));
                t = (TableLayout) findViewById(R.id.tableLayout);
                
                reviewLayout = (RelativeLayout)findViewById(R.id.reviewTab);
                if(DatabaseOperations.isReviewExists() == true)
                    t.setVisibility(View.INVISIBLE);
                third = false;
            }
            
            if(!first && second)
                infoLayout.setVisibility(View.GONE);
            
            else if(first && !second)
                bookingLayout.setVisibility(View.GONE);
            else if(!first && !second)
            {
                bookingLayout.setVisibility(View.GONE);
                infoLayout.setVisibility(View.GONE);
            }
            
            reviewLayout.setVisibility(View.VISIBLE);
        }
        
    }
    
    public void submitReview(View v)
    {
        String id = r.getId();
        double rating = 0;
        
        if (v.getId() == R.id.onestar) {
            rating = 1;
        }
        if (v.getId() == R.id.onehalfstar) {
            rating = 1.5;
        }
        if (v.getId() == R.id.twostar) {
            rating = 2;
        }
        if (v.getId() == R.id.twohalfstar) {
            rating = 2.5;
        }
        if (v.getId() == R.id.threestar) {
            rating = 3;
        }
        if (v.getId() == R.id.threehalfstar) {
            rating = 3.5;
        }
        if (v.getId() == R.id.fourstar) {
            rating = 4;
        }
        if (v.getId() == R.id.fourhalfstar) {
            rating = 4.5;
        }
        if (v.getId() == R.id.fivestar) {
            rating = 5;
        }
        SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
        String userID = settings.getString("email", "").toString();
        userReview = new UserReviews(userID,id);
        review = new Reviews(id,rating);
        reviewDialog(rating);
    }
    
    public void reviewDialog(double rating)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(RestaurantActivity.this);
        
        alert.setTitle("Submit review");
        alert.setMessage("Submit " + rating + " star rating of " + convertToTitleCase(r.getName()));
        
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                
                db.sendReview(review,userReview);
                System.out.println("SENT to DBOPERATIONS-------------------");
                t.setVisibility(View.INVISIBLE);
            }
        });
        
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        
        alert.show();
    }
    
    public static String getTime() {
        return time;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        
        //noinspection SimplifiableIfStatement
        
        
        return super.onOptionsItemSelected(item);
    }
}
