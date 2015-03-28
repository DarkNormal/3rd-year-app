package project.b_ourguest.bourguest;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TableLayout;


/**
 * Created by Robbie on 28/03/2015.
 */
public class TabTestActivity extends FragmentActivity implements
        ActionBar.TabListener {
    private DatabaseOperations db = new DatabaseOperations();
    private Restaurant r = MainActivity.getRestaurantToPass();
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private Reviews review;
    private UserReviews userReview;
    private TableLayout t;
    // Tab titles
    private String[] tabs = { "Info", "Reviews", "Booking" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurnant_activity_frag);

        SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
        String userID = settings.getString("email", "").toString();
        setTitle(convertToTitleCase(r.getName()));
        db.getReview(userID,r.getId());
        t = (TableLayout) findViewById(R.id.tableLayout);
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        actionBar.setTitle(convertToTitleCase(r.getName()));

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
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
        AlertDialog.Builder alert = new AlertDialog.Builder(TabTestActivity.this);

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


    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}
