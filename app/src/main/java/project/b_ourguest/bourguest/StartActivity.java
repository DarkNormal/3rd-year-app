package project.b_ourguest.bourguest;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;

import java.util.List;


public class StartActivity extends ActionBarActivity{

    private Handler h = new Handler();
    private static MobileServiceClient mClient;
    private static MobileServiceTable<Restaurants> restaurantsTable;
    private static MobileServiceTable<Users> usersTable;
    private static MobileServiceTable<Reviews> reviewsTable;
    private static MobileServiceTable<UserReviews> userReviewsTable;
    //private GoogleApiClient mGoogleApiClient;
    //private final static int REQUEST_RESOLVE_ERROR = 1001;
    //private LocationRequest mLocationRequest;
    //private boolean loggedIn = true;
    private static List<Restaurants> restaurants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            // code for making the start activity full screen
            // http://www.techrepublic.com/article/give-android-users-an-immersive-experience-by-using-kitkats-full-screen-decor-flags/
            initiateClient();
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            );

            setContentView(R.layout.activity_main);

        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //create an instance of Google API Client using GoogleApiClient.Builder. Use the builder to add the LocationServices API

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds*/


            //BackgroundOperations populateRestaurantArray = new BackgroundOperations();
            //populateRestaurantArray.execute(restaurants);

        DatabaseOperations db = new DatabaseOperations();
        restaurants = db.getRestaurants();

            h.postDelayed(new Runnable() {
                public void run() {
                    finish(); //this prevents users going back to this screen
                    Intent intent = new Intent(StartActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
            }, 5000);
    }
   public static MobileServiceClient getMobileServiceClient()
    {
        return mClient;
    }
    /*@Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }*/

    private void initiateClient() {
        try {
            mClient = new MobileServiceClient(
                    "https://bourguest.azure-mobile.net/",
                    "tsAfQHCRADQqWwFAYuMgUqHBVqburZ80",
                    this
            );
            restaurantsTable = mClient.getTable(Restaurants.class);
            usersTable = mClient.getTable(Users.class);
            reviewsTable = mClient.getTable(Reviews.class);
            userReviewsTable = mClient.getTable(UserReviews.class);
        }catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), "Client in StartActivity.java could not be initiated",
                    Toast.LENGTH_LONG).show();
            System.out.println("START ACTIVITY ERROR");
            e.printStackTrace();
        }
    }

    public static MobileServiceTable<Restaurants> getRestaurantsTable() {
        return restaurantsTable;
    }
    public static MobileServiceTable<Reviews> getReviewsTable() {
        return reviewsTable;
    }
    public static MobileServiceTable<UserReviews> getUserReviewsTable() {
        return userReviewsTable;
    }
    public static List<Restaurants> getRestaurants() {
        return restaurants;
    }
    public static MobileServiceTable<Users> getUsersTable() {
        return usersTable;
    }

    /*@Override
    public void onConnected(Bundle bundle) {
        System.out.println("OnConnected---------------------");
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(loc != null)
        {
            Toast.makeText(MainActivity.this,"LONGITUDE: " + loc.getLongitude() + "\n" +
                    "LATITUDE: " + loc.getLatitude(),Toast.LENGTH_LONG).show();
        }
        else
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("OnConnectionSuspended---------------------");
        Toast.makeText(MainActivity.this, "Google location service suspended",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("ERROR---------------------" + connectionResult.getErrorCode());
        Toast.makeText(MainActivity.this,"Location services connection failed ",Toast.LENGTH_LONG).show();
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
    public void onLocationChanged(Location loc) {
        Toast.makeText(MainActivity.this,"LONGITUDE: " + loc.getLongitude() + "\n" +
                "LATITUDE: " + loc.getLatitude(),Toast.LENGTH_LONG).show();
    }*/
}
