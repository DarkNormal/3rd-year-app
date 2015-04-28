package project.b_ourguest.bourguest;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;

import java.util.ArrayList;
import java.util.List;

import project.b_ourguest.bourguest.DB.DatabaseOperations;
import project.b_ourguest.bourguest.Model.Bookings;
import project.b_ourguest.bourguest.Model.Floorplan;
import project.b_ourguest.bourguest.Model.Restaurant;
import project.b_ourguest.bourguest.Model.Reviews;
import project.b_ourguest.bourguest.Model.UserReviews;
import project.b_ourguest.bourguest.Model.UsersTable;
import project.b_ourguest.bourguest.Model.tableObject;


public class StartActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    
    private Handler h = new Handler();
    private static MobileServiceClient mClient;
    private static MobileServiceTable<Restaurant> restaurantsTable;
    private static MobileServiceTable<UsersTable> usersTable;
    private static MobileServiceTable<Reviews> reviewsTable;
    private static MobileServiceTable<UserReviews> userReviewsTable;
    private static MobileServiceTable<Bookings> bookingsTable;
    private static MobileServiceTable<Floorplan> floorplanTable;
    private static MobileServiceTable<tableObject> tableObjectsTable;
    private GoogleApiClient mGoogleApiClient;
    private final static int REQUEST_RESOLVE_ERROR = 1001;
    private LocationRequest mLocationRequest;
    private static List<Restaurant> restaurants;
    private static ArrayList<Reviews> reviews;
    private Location loc;

    public static double getLon() {
        return lon;
    }

    public static double getLat() {
        return lat;
    }

    private static double lat,lon;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /***************************************************************************************
         *    Title: Give Android users an immersive experience by using KitKat's full screen decor flags
         *    Author: William J. Francis
         *    Date: 20/12/2015
         *    Code version: 1
         *    Availability: http://www.techrepublic.com/article/give-android-users-an-immersive-experience-by-using-kitkats-full-screen-decor-flags/
         *
         ***************************************************************************************/
        initiateClient();
        getWindow().getDecorView().setSystemUiVisibility(
                                                         View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                         | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                         | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                         | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                                         | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                                         );
        
        setContentView(R.layout.activity_main);
        
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //create an instance of Google API Client using GoogleApiClient.Builder. Use the builder to add the LocationServices API

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        h.postDelayed(new Runnable() {
            public void run() {
                try {
                    DatabaseOperations db = new DatabaseOperations(loc.getLatitude(), loc.getLongitude());
                    reviews = db.getRating();
                    restaurants = db.getRestaurants(loc.getLatitude(), loc.getLongitude());
                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Please enable location services",
                            Toast.LENGTH_LONG).show();
                    DatabaseOperations db = new DatabaseOperations();
                    reviews = db.getRating();
                }
            }
        }, 1500);
        h.postDelayed(new Runnable() {
            public void run() {
                finish(); //this prevents users going back to this screen
                Intent intent = new Intent(StartActivity.this, SignInActivity.class);
                try {
                    lat = loc.getLatitude();
                    lon = loc.getLongitude();
                }catch(Exception e){
                    lat = 0;
                    lon = 0;
                }
                startActivity(intent);
            }
        }, 5000);

    }
    public static MobileServiceClient getMobileServiceClient()
    {
        return mClient;
    }
    @Override
     protected void onStart() {
     super.onStart();
     
     mGoogleApiClient.connect();
     }
     
     @Override
     protected void onStop() {
     mGoogleApiClient.disconnect();
     super.onStop();
     }
    
    private void initiateClient() {
        try {
            mClient = new MobileServiceClient( "https://bourguestmob.azure-mobile.net/", "jgeHXHepyaTFHjINloskvXXzhueGbG47", this );
            restaurantsTable = mClient.getTable(Restaurant.class);
            usersTable = mClient.getTable(UsersTable.class);
            reviewsTable = mClient.getTable(Reviews.class);
            userReviewsTable = mClient.getTable(UserReviews.class);
            floorplanTable = mClient.getTable(Floorplan.class);
            tableObjectsTable = mClient.getTable(tableObject.class);
            bookingsTable = mClient.getTable(Bookings.class);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static MobileServiceTable<Restaurant> getRestaurantsTable() {
        return restaurantsTable;
    }
    public static MobileServiceTable<Reviews> getReviewsTable() {
        return reviewsTable;
    }
    public static MobileServiceTable<UserReviews> getUserReviewsTable() {
        return userReviewsTable;
    }
    public static List<Restaurant> getRestaurants() {
        return restaurants;
    }
    public static MobileServiceTable<UsersTable> getUsersTable() {
        return usersTable;
    }
    public static MobileServiceTable<Floorplan> getFloorplanTable() {
        return floorplanTable;
    }
    public static MobileServiceTable<tableObject> getTableObjectsTable() {
        return tableObjectsTable;
    }
    public static MobileServiceTable<Bookings> getBookingsTable(){
        return bookingsTable;
    }

    public static ArrayList<Reviews> getReviews() {return reviews;}

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


}
