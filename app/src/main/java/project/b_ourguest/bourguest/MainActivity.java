package project.b_ourguest.bourguest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.squareup.picasso.Picasso;

import project.b_ourguest.bourguest.DB.DatabaseOperations;
import project.b_ourguest.bourguest.Model.Restaurant;
import project.b_ourguest.bourguest.Model.Reviews;


/**
 * Created by Robbie on 16/12/2014.
 */
public class MainActivity extends ActionBarActivity {

    //azure services
    DatabaseOperations db = new DatabaseOperations();
    private Handler h = new Handler();
    private ProgressDialog pd;
    private TextView rat;
    private List<Restaurant> restaurants = StartActivity.getRestaurants();
    private ArrayList<Reviews> reviews = StartActivity.getReviews();
    private TextView searchedRestaurantsText;
    private String message;
    private String userID;
    private SwipeRefreshLayout swipeContainer;
    private static Restaurant restaurantToPass;
    String[] type = {"American", "BBQ", "Chinese", "Family Friendly", "Healthy Option", "Indian", "Italian",
            "Portuguese", "Seafood", "Something Different", "Steakhouse", "Thai", "Traditional"};
    int pos = 0;
    String name = "";
    int tryAgain = 0;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    //private Location usersLocation;
    public final String PREFS_NAME = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isNetworkAvailable())
            setContentView(R.layout.no_network_available);
        else {

            displayRestaurants("Nearest Restaurants");
        }
        SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
        userID = settings.getString("email", "").toString();
    }

    private boolean isNetworkAvailable() {
        //http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void displayRestaurants(String message) {
        if (restaurants.size() > 0) {
            setContentView(R.layout.activity_main_display);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerList = (ListView) findViewById(R.id.navList);
            mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
            searchedRestaurantsText = (TextView) findViewById(R.id.searchedRestaurants);
            searchedRestaurantsText.setText(message);
            //populates the list view
            ArrayAdapter<Restaurant> adapter = new RestaurantsAdapter();
            ListView list = (ListView) findViewById(R.id.restaurantListView);
            list.setAdapter(adapter);
            addDrawerItems();
            handleClicks();
            setUpDrawer();

            //https://github.com/codepath/android_guides/wiki/Implementing-Pull-to-Refresh-Guide
             swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    //fetchTimelineAsync(0);
                    System.out.println("REFRESHING----------------------------");
                    refresh();
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        } else {
            setContentView(R.layout.no_restaurants_to_display_layout);
        }

    }

    private void setUpDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {

            @Override
            public void run() {

                mDrawerToggle.syncState();

            }

        });


    }

    public void addDrawerItems() {
        //http://blog.teamtreehouse.com/add-navigation-drawer-android
        String[] osArray = {"My Bookings","Log out"};
        mAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    Intent intent = new Intent(MainActivity.this, User_Bookings_Activity.class);
                    intent.putExtra("fromBooking", false);
                    startActivity(intent);
                }
                else
                {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.remove("loggedIn");
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }




    public void handleClicks() {
        ListView list = (ListView) findViewById(R.id.restaurantListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                restaurantToPass = restaurants.get(position);
                db.getReview(userID, restaurantToPass.getId());
                Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!isNetworkAvailable())
            getMenuInflater().inflate(R.menu.empty_menu, menu);
        else
            getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        mDrawerToggle.syncState();
//    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //Creating the instance of PopupMenu
            View v = (View) findViewById(R.id.action_search);
            PopupMenu popup = new PopupMenu(getApplicationContext(), v);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.search_popup_menu, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.search_by_wheelchair_access) {
                        tryAgain = 1;
                        //query the database for restaurants that have wheelchair access
                        restaurants = db.searchDatabaseForWheelchairFriendlyRestaurants();
                        pd = ProgressDialog.show(MainActivity.this, "Loading", "Searching for wheelchair accessible restaurants");

                        h.postDelayed(new Runnable() {
                            public void run() {
                                System.out.println("Size when result is returned is " + restaurants.size());
                                determineActionBasedOnRestaurantsSize("Could not find any wheelchair accessible restaurants",
                                        "Wheelchair Accessible Restaurants");
                                pd.dismiss();
                                // To dismiss the dialog
                            }
                        }, 3000);

                    } else if (item.getItemId() == R.id.search_by_type) {
                        System.out.println("TYPE---------------------------");
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle("Please choose from the types of restaurants");

                        alert.setItems(type, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int position) {
                                // The 'position' argument contains the index position
                                // of the selected item
                                pos = position;
                                restaurants.clear();
                                restaurants = db.searchByType(type[position]);
                                tryAgain = 3;
                                pd = ProgressDialog.show(MainActivity.this, "Loading", "Searching for " + type[position]
                                        + " Restaurant");

                                h.postDelayed(new Runnable() {
                                    public void run() {
                                        System.out.println("Size when result is returned is " + restaurants.size());
                                        determineActionBasedOnRestaurantsSize("No restaurants matched that type",
                                                type[pos] + " Restaurant");
                                        pd.dismiss();
                                        // To dismiss the dialog
                                    }
                                }, 3000);
                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });

                        alert.show();
                    } else if (item.getItemId() == R.id.search_nearest) {
                        tryAgain = 0;
                        //query the database for restaurants that have wheelchair access
                        restaurants = db.getRestaurants();
                        pd = ProgressDialog.show(MainActivity.this, "Loading", "Searching for nearest restaurants");

                        h.postDelayed(new Runnable() {
                            public void run() {
                                System.out.println("Size when result is returned is " + restaurants.size());
                                determineActionBasedOnRestaurantsSize("Could not find any local restaurants",
                                        "Nearest Restaurants");
                                pd.dismiss();
                                // To dismiss the dialog
                            }
                        }, 3000);
                    } else {
                        searchNameDialog(); //creates a dialog for a user to enter the name
                        //of the restaurant they are looking for
                        //the name they type is then sent to the database to retrieve
                        //the restaurants with that name spread across different
                        //locations if there is more than one
                    }
                    return true;
                }
            });

            popup.show();//showing popup menu

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void determineActionBasedOnRestaurantsSize(String message, String title) {
        System.out.println("Size in mainActivity is " + restaurants.size());
        if (restaurants.size() == 0) //meaning the type was not found
        {
            Toast.makeText(MainActivity.this, message,
                    Toast.LENGTH_LONG).show();
            setContentView(R.layout.no_restaurants_to_display_layout);
        } else {
            displayRestaurants(title);
        }
    }

    public void tryAgain(View v) {
        String message = "";

        if (tryAgain == 0) {
            message = "Nearest Restaurant";
            restaurants = db.getRestaurants();
        } else if (tryAgain == 1) {
            message = "Wheelchair Accessible Restaurant";
            restaurants = db.searchDatabaseForWheelchairFriendlyRestaurants();
        } else if (tryAgain == 2) {
            message = "Restaurant called " + name;
            restaurants = db.searchByName(name);
        } else {
            message = type[pos] + " Restaurant";
            restaurants = db.searchByType(type[pos]);
        }
        pd = ProgressDialog.show(MainActivity.this, "Loading", "Wait while loading...");

        h.postDelayed(new Runnable() {
            public void run() {
                System.out.println("Size when result is returned is " + restaurants.size());
                pd.dismiss();
                // To dismiss the dialog
            }
        }, 3000);
        displayRestaurants(message);
    }
    public void refresh()
    {
         message = "";
        reviews = db.getRating();
        if (tryAgain == 0) {
            message = "Nearest Restaurant";
            restaurants = db.getRestaurants();
        } else if (tryAgain == 1) {
            message = "Wheelchair Accessible Restaurant";
            restaurants = db.searchDatabaseForWheelchairFriendlyRestaurants();
        } else if (tryAgain == 2) {
            message = "Restaurant called " + name;
            restaurants = db.searchByName(name);
        } else {
            message = type[pos] + " Restaurant";
            restaurants = db.searchByType(type[pos]);
        }

        h.postDelayed(new Runnable() {
            public void run() {
                displayRestaurants(message);
                swipeContainer.setRefreshing(false);
            }
        }, 3000);

    }

    public void searchNameDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        alert.setTitle("Search for restaurant by name");
        alert.setMessage("Enter the name of the restaurant");

        // Set an EditText view to get user input
        final EditText input = new EditText(MainActivity.this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                name = input.getText().toString();
                restaurants.clear();
                restaurants = db.searchByName(name);
                tryAgain = 2;
                pd = ProgressDialog.show(MainActivity.this, "Loading", "Searching for " + name);

                h.postDelayed(new Runnable() {
                    public void run() {
                        System.out.println("Size when result is returned is " + restaurants.size());
                        determineActionBasedOnRestaurantsSize("No restaurants matched that name",
                                "Restaurant called " + name);
                        pd.dismiss();
                        // To dismiss the dialog
                    }
                }, 3500);

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public static Restaurant getRestaurantToPass() {
        return restaurantToPass;
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


    private class RestaurantsAdapter extends ArrayAdapter<Restaurant> {

        public RestaurantsAdapter() {
            super(MainActivity.this, R.layout.restaurants_listview_layout, restaurants);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //this makes sure we have a view to work with
            View v = convertView;
            if (v == null) //create new view
            {
                v = getLayoutInflater().inflate(R.layout.restaurants_listview_layout, parent, false);
            }
            //populate the list

            Restaurant r = restaurants.get(position);
            rat = (TextView) v.findViewById(R.id.restaurantRating);
            rat.setText("No reviews");

            for (int i = 0; i < reviews.size(); i++) {
                if (reviews.get(i).getRestID().equals(r.getId()))
                    rat.setText(reviews.get(i).getRating() + " stars");
            }

            TextView restName = (TextView) v.findViewById(R.id.restaurantName);
            restName.setText(convertToTitleCase(r.getName()));


            TextView dist = (TextView) v.findViewById(R.id.restaurantDistance);
            if (r.getDistance() > 2)
                dist.setText("less than 3 km from current location");
            else if (r.getDistance() > 1 && r.getDistance() < 2)
                dist.setText("less than 2 km from current location");
            else if (r.getDistance() < 1)
                dist.setText("less than 1 km from current location");
            else
                dist.setText("greater than 3 km from current location");


            ImageView im = (ImageView) v.findViewById(R.id.restaurantImage);
            Picasso.with(MainActivity.this)
                    .load(r.getAppImage())
                    .placeholder(R.drawable.ic_launcher)
                    .resize(100,100)
                    .into(im);

            return v;
        }
    }
}
