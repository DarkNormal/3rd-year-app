package project.b_ourguest.bourguest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import project.b_ourguest.bourguest.DB.DatabaseOperations;
import project.b_ourguest.bourguest.Model.Restaurant;
import project.b_ourguest.bourguest.Model.Reviews;
import project.b_ourguest.bourguest.Model.UserReviews;
import project.b_ourguest.bourguest.ui.SlidingTabLayout;
import project.b_ourguest.bourguest.ui.ViewPagerAdapter;


public class RestaurantActivity extends ActionBarActivity {
    //http://stackoverflow.com/questions/3496269/how-to-put-a-border-around-an-android-textview
    // link that helped with borders on textviews
    // Declaring Your View and Variables
    Restaurant r = MainActivity.getRestaurantToPass();
    DatabaseOperations db = new DatabaseOperations();
    private Reviews review;
    private  double rating;
    private UserReviews userReview;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Info", "Reviews", "Book"};
    int Numboftabs = 3;
    private RatingBar ratingBar;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_activity_display);
        getSupportActionBar().setElevation(0);
        setTitle(convertToTitleCase(r.getName()));


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
        if(DatabaseOperations.isReviewExists())
        {
            TextView t = (TextView) findViewById(R.id.reviewText);
            t.setText("You have already submitted your review for this restaurant");
        }
        else {
            TextView t = (TextView) findViewById(R.id.reviewText);
            t.setVisibility(View.INVISIBLE);
            RelativeLayout rel = (RelativeLayout) findViewById(R.id.ratingBarLayout);
            rel.setVisibility(View.VISIBLE);
            addListenerOnButton();
        }

    }

    public void addListenerOnButton() {

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        //if click on me, then display the current rating value.
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = ratingBar.getRating();
                submitReviewToDB();
            }
        });

    }

    public void submitReviewToDB()
    {
        SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
        String userID = settings.getString("email", "").toString();
        userReview = new UserReviews(userID,r.getId());
        review = new Reviews(r.getId(),rating);
        db.sendReview(review,userReview);
        System.out.println("SENT to DBOPERATIONS-------------------");
        TextView t = (TextView) findViewById(R.id.reviewText);
        t.setVisibility(View.VISIBLE);
        t.setText("You're review of " + rating + " stars for " + convertToTitleCase(r.getName()) + " was submitted");
        t.setClickable(false);
        RelativeLayout rel = (RelativeLayout) findViewById(R.id.ratingBarLayout);
        rel.setVisibility(View.INVISIBLE);

    }
}