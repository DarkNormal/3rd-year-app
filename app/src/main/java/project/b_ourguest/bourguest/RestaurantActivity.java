package project.b_ourguest.bourguest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.TableLayout;


import project.b_ourguest.bourguest.ui.SlidingTabLayout;
import project.b_ourguest.bourguest.ui.ViewPagerAdapter;


public class RestaurantActivity extends ActionBarActivity {
    //http://stackoverflow.com/questions/3496269/how-to-put-a-border-around-an-android-textview
    // link that helped with borders on textviews
    // Declaring Your View and Variables
    Restaurant r = MainActivity.getRestaurantToPass();
    DatabaseOperations db = new DatabaseOperations();

    private Reviews review;
    TableLayout tableLayout;
    private UserReviews userReview;

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Info", "Reviews", "Book"};
    int Numboftabs = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_activity_display);
        getSupportActionBar().setElevation(0);
        SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
        String userID = settings.getString("email", "").toString();
        setTitle(convertToTitleCase(r.getName()));
        db.getReview(userID, r.getId());


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);


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
        for (int i = 0; i < partOfName.length; i++) {
            upperCaseLetter = Character.toUpperCase(partOfName[i].charAt(0));
            sub = partOfName[i].substring(1, partOfName[i].length());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
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
                tableLayout = (TableLayout) findViewById(R.id.tableLayout);
                tableLayout.setVisibility(View.INVISIBLE);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
}