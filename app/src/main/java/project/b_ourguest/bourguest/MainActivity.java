package project.b_ourguest.bourguest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.LruCache;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import project.b_ourguest.bourguest.DB.DatabaseOperations;
import project.b_ourguest.bourguest.Model.Restaurant;
import project.b_ourguest.bourguest.Model.Reviews;


/**
 * Created by Robbie on 16/12/2014.
 */
public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private final static int REQUEST_RESOLVE_ERROR = 1001;
    private LocationRequest mLocationRequest;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static MainActivity mInstance;
    private static Context mAppContext;
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
    String[] type = {"American", "BBQ", "Chinese", "Family Friendly", "Healthy Option", "Indian", "Italian","Pizza",
            "Portuguese", "Seafood", "Something Different", "Steakhouse", "Thai", "Traditional"};
    int pos = 0;
    String name = "";
    int tryAgain = 0;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    //private Location usersLocation;
    public final String PREFS_NAME = "LoginPrefs";
    private Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isNetworkAvailable())
            setContentView(R.layout.no_network_available);
        else {
            mInstance = this;
            this.setAppContext(getApplicationContext());
            mRequestQueue = Volley.newRequestQueue(MainActivity.this);
            mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }
                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });
            displayRestaurants("Nearest Restaurants");
        }
        SharedPreferences settings = getSharedPreferences("LoginPrefs", 0);
        userID = settings.getString("email", "").toString();
    }

    private boolean isNetworkAvailable() {

        /***************************************************************************************
         *    Title: Detect whether there is an Internet connection available on Android
         *    Author: Alexandre Jasmin
         *    Date: 28/2/2015
         *    Code version: 2
         *    Availability: http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
         *
         ***************************************************************************************/
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void displayRestaurants(String message) {
        try {
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
                /***************************************************************************************
                 *    Title: Implementing Pull to Refresh Guide
                 *    Author: Nathan Esquenazi
                 *    Date: 14/4/2015
                 *    Code version: 20
                 *    Availability: https://github.com/codepath/android_guides/wiki/Implementing-Pull-to-Refresh-Guide
                 *
                 ***************************************************************************************/
                swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
                // Setup refresh listener which triggers new data loading
                swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
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
        }catch(Exception e)
        {
            setContentView(R.layout.no_location);
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
        /***************************************************************************************
         *    Title: How to Add a Navigation Drawer in Android
         *    Author: Ben Jakuben
         *    Date: 1/4/2015
         *    Code version: 1
         *    Availability: http://blog.teamtreehouse.com/add-navigation-drawer-android
         *
         ***************************************************************************************/
        String[] options = {"My Bookings","Log out","FAQs"};
        mAdapter=new ArrayAdapter<String>(this,R.layout.navdrawerlayout,R.id.listItem,options);
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
                else if(position == 1)
                {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.remove("loggedIn");
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, FAQActivity.class);
                    startActivity(intent);
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
                    Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
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



        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //Creating the instance of PopupMenu
            Context wrapper = new ContextThemeWrapper(MainActivity.this, R.style.MyPopupMenu);
            View v = (View) findViewById(R.id.action_search);
            PopupMenu popup = new PopupMenu(wrapper, v);
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
                                determineActionBasedOnRestaurantsSize("Could not find any wheelchair accessible restaurants",
                                        "Wheelchair Accessible Restaurants");
                                pd.dismiss();
                                // To dismiss the dialog
                            }
                        }, 3000);

                    } else if (item.getItemId() == R.id.search_by_type) {
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
                                        determineActionBasedOnRestaurantsSize("No restaurants matched that type",
                                                type[pos] + " Restaurants");
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
                        getNearest();
                        pd = ProgressDialog.show(MainActivity.this, "Loading", "Searching for nearest restaurants");

                        h.postDelayed(new Runnable() {
                            public void run() {
                                determineActionBasedOnRestaurantsSize("Could not find any local restaurants",
                                        "Nearest Restaurants");
                                pd.dismiss();
                                // To dismiss the dialog
                            }
                        }, 3000);
                    }
                    else if(item.getItemId() == R.id.top_rated){
                        tryAgain = 4;
                        try {
                            restaurants.clear();
                        }catch(Exception e)
                        {
                            setContentView(R.layout.no_restaurants_to_display_layout);
                        }

                        restaurants = db.getTopRated();

                        pd = ProgressDialog.show(MainActivity.this, "Loading", "Searching for top rated restaurants");

                        h.postDelayed(new Runnable() {
                            public void run() {
                                determineActionBasedOnRestaurantsSize("No five star restaurants",
                                        "Top rated restaurants");
                                pd.dismiss();
                                // To dismiss the dialog
                            }
                        }, 3500);
                    }
                    else {
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void getNearest()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        //create an instance of Google API Client using GoogleApiClient.Builder. Use the builder to add the LocationServices API

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        h.postDelayed(new Runnable() {
            public void run() {
                try {
                    DatabaseOperations db = new DatabaseOperations(loc.getLatitude(), loc.getLongitude());
                    restaurants = db.getRestaurants(loc.getLatitude(), loc.getLongitude());
                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Please enable location services",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, 1500);
    }


    @Override
    protected void onStop() {
        if(mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void determineActionBasedOnRestaurantsSize(String message, String title) {
        if(restaurants == null)
            setContentView(R.layout.no_restaurants_to_display_layout);
        else if (restaurants.size() == 0)
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
            message = "Nearest Restaurants";
            getNearest();
        } else if (tryAgain == 1) {
            message = "Wheelchair Accessible Restaurants";
            restaurants = db.searchDatabaseForWheelchairFriendlyRestaurants();
        } else if (tryAgain == 2) {
            message = "Restaurants called " + name;
            restaurants = db.searchByName(name);
        }
        else if(tryAgain == 4) {
            message = "Top rated restaurants";
            restaurants = db.getTopRated();

        }else
         {
            message = type[pos] + " Restaurants";
            restaurants = db.searchByType(type[pos]);
        }
        pd = ProgressDialog.show(MainActivity.this, "Loading", "Wait while loading...");

        h.postDelayed(new Runnable() {
            public void run() {
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
            message = "Nearest Restaurants";
            getNearest();
        } else if (tryAgain == 1) {
            message = "Wheelchair Accessible Restaurants";
            restaurants = db.searchDatabaseForWheelchairFriendlyRestaurants();
        } else if (tryAgain == 2) {
            message = "Restaurants called " + name;
            restaurants = db.searchByName(name);
        } else if(tryAgain == 4) {
            message = "Top rated restaurants";
            restaurants = db.getTopRated();
        }else
         {
            message = type[pos] + " Restaurants";
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
                        determineActionBasedOnRestaurantsSize("No restaurants matched that name",
                                "Restaurants called " + name);
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
    public static MainActivity getInstance(){
        return mInstance;
    }
    public static Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
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
    public void onConnected(Bundle bundle) {
        loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(loc != null)
        {

        }
        else
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        }

    }

    @Override
    public void onLocationChanged(Location l) {
        loc = l;

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
            if(mImageLoader == null)
                mImageLoader = VolleySingleton.getInstance().getImageLoader();

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

            NetworkImageView im = (NetworkImageView) v.findViewById(R.id.restaurantImage);
            if(r.getAppImage() != null)
                im.setImageUrl(r.getAppImage(),mImageLoader);

            im.setErrorImageResId(R.drawable.ic_launcher);
            return v;
        }
    }
}
