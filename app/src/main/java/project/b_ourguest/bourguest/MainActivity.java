package project.b_ourguest.bourguest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
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
import java.util.List;

import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.squareup.picasso.Picasso;



/**
 * Created by Robbie on 16/12/2014.
 */
public class MainActivity extends ActionBarActivity {

    //azure services
    DatabaseOperations db = new DatabaseOperations();
    private Handler h = new Handler();
    DecimalFormat df = new DecimalFormat("##.###");
    private ProgressDialog pd;
    private List<Restaurants> restaurants = StartActivity.getRestaurants();
    TextView searchedRestaurantsText;
    private static Restaurants restaurantToPass;
    String[] type = {"Indian","Italian","American","Chinese","Portuguese","Family Friendly"
            ,"Traditional","Something different","Pizza","Healthy Option"};
    int pos = 0;
    String name = "";
    int tryAgain = 0;
    //private Location usersLocation;
    public final String PREFS_NAME = "LoginPrefs";
    private static MobileServiceTable<Restaurants> restaurantsTable = StartActivity.getRestaurantsTable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isNetworkAvailable())
            setContentView(R.layout.no_network_available);
        else
        {
            displayRestaurants("Nearest Restaurants");
        }

    }

    private boolean isNetworkAvailable() {
        //http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void displayRestaurants(String message) {
        if(restaurants.size() > 0) {
                    setContentView(R.layout.activity_main_display);
                    searchedRestaurantsText = (TextView) findViewById(R.id.searchedRestaurants);
                    searchedRestaurantsText.setText(message);
                    //populates the list view
                    ArrayAdapter<Restaurants> adapter = new RestaurantsAdapter();
                    ListView list = (ListView) findViewById(R.id.restaurantListView);
                    list.setAdapter(adapter);

                    handleClicks();
        }
        else {
            setContentView(R.layout.no_restaurants_to_display_layout);
        }

    }

    private void handleClicks() {
        ListView list = (ListView) findViewById(R.id.restaurantListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                restaurantToPass = restaurants.get(position);
                Intent intent =  new Intent(getApplicationContext(),RestaurantActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
      public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(!isNetworkAvailable())
            getMenuInflater().inflate(R.menu.empty_menu,menu);
        else
            getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
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
            View v = (View) findViewById(R.id.action_search);
            PopupMenu popup = new PopupMenu(getApplicationContext(), v);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.search_popup_menu, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.search_by_wheelchair_access)
                    {
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

                    }
                    else if(item.getItemId() == R.id.search_by_type)
                    {
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
                            tryAgain = 2;
                            pd = ProgressDialog.show(MainActivity.this, "Loading", "Searching for " + type[position]
                            + " Restaurants");

                            h.postDelayed(new Runnable() {
                                public void run() {
                                    System.out.println("Size when result is returned is " + restaurants.size());
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
                    }
                    else
                    {
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
        else if(id == R.id.action_options)
        {
            View v = (View) findViewById(R.id.action_options);
            PopupMenu popup = new PopupMenu(getApplicationContext(), v);
            popup.getMenuInflater().inflate(R.menu.options_popup_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.logoutOption)
                    {
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove("loggedIn");
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    return true;
                }
            });

            popup.show();//showing popup menu

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void determineActionBasedOnRestaurantsSize(String message,String title) {
        System.out.println("Size in mainActivity is " + restaurants.size());
        if(restaurants.size() == 0) //meaning the type was not found
        {
            Toast.makeText(MainActivity.this, message,
                    Toast.LENGTH_LONG).show();
            setContentView(R.layout.no_restaurants_to_display_layout);
        }
        else
        {
            displayRestaurants(title);
        }
    }
    public void tryAgain(View v)
    {
        String message = "";

        if(tryAgain == 0) {
            message = "Nearest Restaurants";
            restaurants = db.getRestaurants();
        }
        else if(tryAgain == 1) {
            message = "Wheelchair Accessible Restaurants";
            restaurants = db.searchDatabaseForWheelchairFriendlyRestaurants();
        }
        else if(tryAgain == 2)
        {
            message = "Restaurants called " + name;
            restaurants = db.searchByName(name);
        }
        else {
            message = type[pos] + " Restaurants";
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

    public void searchNameDialog()
    {
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
                tryAgain = 3;
                pd = ProgressDialog.show(MainActivity.this, "Loading", "Searching for " + name);

                h.postDelayed(new Runnable() {
                    public void run() {
                        System.out.println("Size when result is returned is " + restaurants.size());
                        determineActionBasedOnRestaurantsSize("No restaurants matched that name",
                                "Restaurants called " + name);
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
    }

    public static Restaurants getRestaurantToPass() {
        return restaurantToPass;
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


    private class RestaurantsAdapter extends ArrayAdapter<Restaurants> {

        public RestaurantsAdapter() {
            super(MainActivity.this,R.layout.restaurants_listview_layout,restaurants);

        }

        @Override
        public View getView(int position, View convertView,ViewGroup parent){
            //this makes sure we have a view to work with
            View v = convertView;
            if(v == null) //create new view
            {
                v = getLayoutInflater().inflate(R.layout.restaurants_listview_layout, parent, false);
            }
            String nameDisplayed;
            //populate the list
            Restaurants r = restaurants.get(position);

            TextView restName = (TextView) v.findViewById(R.id.restaurantName);
            restName.setText(convertToTitleCase(r.getName()));

            if(r.getWheelchairAccessible() == 'Y')
                System.out.println(r.getName() + "  " + r.getWheelchairAccessible() + "--------------------------");

            TextView dist = (TextView) v.findViewById(R.id.restaurantDistance);
            dist.setText(df.format(r.getDistance()) + "km from current location");

            ImageView im = (ImageView) v.findViewById(R.id.restaurantImage);
            Picasso.with(MainActivity.this)
                    .load(r.getAppImage())
                    .resize(100, 100).into(im);

            return v;
        }
    }
}
