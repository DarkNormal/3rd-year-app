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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


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
    private Handler h = new Handler();
    private Reviews review;
    private  double rating;
    TableLayout tableLayout;
    private UserReviews userReview;
    private  Dialog dialog;

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

    public void rate()
    {
        ImageView im = (ImageView) dialog.findViewById(R.id.onestar);
        im.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rating = 1;
                submitReviewToDB();

            }
        });

        ImageView im2 = (ImageView) dialog.findViewById(R.id.onehalfstar);
        im2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rating = 1.5;
                submitReviewToDB();

            }
        });
        ImageView im3 = (ImageView) dialog.findViewById(R.id.twostar);
        im3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rating = 2;
                submitReviewToDB();

            }
        });
        ImageView im4 = (ImageView) dialog.findViewById(R.id.twohalfstar);
        im4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rating = 2.5;
                submitReviewToDB();

            }
        });
        ImageView im5 = (ImageView) dialog.findViewById(R.id.threestar);
        im5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rating = 3;
                submitReviewToDB();

            }
        });
        ImageView im6 = (ImageView) dialog.findViewById(R.id.threehalfstar);
        im6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rating = 3.5;
                submitReviewToDB();

            }
        });
        ImageView im7 = (ImageView) dialog.findViewById(R.id.fourstar);
        im7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rating = 4;
                submitReviewToDB();

            }
        });
        ImageView im8 = (ImageView) dialog.findViewById(R.id.fourhalfstar);
        im8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rating = 4.5;
                submitReviewToDB();

            }
        });
        ImageView im9 = (ImageView) dialog.findViewById(R.id.fivestar);
        im9.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rating = 5;
                submitReviewToDB();

            }
        });
    }

    public void submitReview(View v)
    {
        if(DatabaseOperations.isReviewExists())
        {
            TextView t = (TextView) findViewById(R.id.reviewText);
            t.setText("You have already submitted your review for this restaurant");
        }
        else {
            //set up dialog
            dialog = new Dialog(RestaurantActivity.this);
            dialog.setContentView(R.layout.rating_dialog);
            dialog.setTitle("Choose a rating");
            rate();
            dialog.setCancelable(true);
            dialog.show();
        }

    }

    public void submitReviewToDB()
    {
        SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
        String userID = settings.getString("email", "").toString();
        userReview = new UserReviews(userID,r.getId());
        review = new Reviews(r.getId(),rating);

        dialog.setTitle("Submit " + rating + " star rating");
        TableRow t = (TableRow) dialog.findViewById(R.id.tableRow);
        t.setVisibility(View.INVISIBLE);
        Button b = (Button) dialog.findViewById(R.id.cancel);
        b.setVisibility(View.VISIBLE);

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button bu = (Button) dialog.findViewById(R.id.ok);
        bu.setVisibility(View.VISIBLE);

        bu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                db.sendReview(review,userReview);
                System.out.println("SENT to DBOPERATIONS-------------------");
                dialog.dismiss();
                TextView t = (TextView) findViewById(R.id.reviewText);
                t.setText("You're review of " + rating + " stars for " + convertToTitleCase(r.getName()) + " was submitted");
                t.setClickable(false);
            }
        });
    }
}