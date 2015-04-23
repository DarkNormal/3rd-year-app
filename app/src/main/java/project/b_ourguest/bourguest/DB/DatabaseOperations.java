package project.b_ourguest.bourguest.DB;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Pair;

import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import java.util.ArrayList;
import java.util.List;

import project.b_ourguest.bourguest.MainActivity;
import project.b_ourguest.bourguest.Model.Bookings;
import project.b_ourguest.bourguest.Model.Floorplan;
import project.b_ourguest.bourguest.Model.Restaurant;
import project.b_ourguest.bourguest.Model.Reviews;
import project.b_ourguest.bourguest.Model.UsersTable;
import project.b_ourguest.bourguest.Model.tableObject;
import project.b_ourguest.bourguest.StartActivity;
import project.b_ourguest.bourguest.Model.UserReviews;

/**
 * Created by Robbie on 06/02/2015.
 */
public class DatabaseOperations {
    private static MobileServiceClient mClient = StartActivity.getMobileServiceClient();
    private static MobileServiceTable<Restaurant> restaurantsTable = StartActivity.getRestaurantsTable();
    private static MobileServiceTable<Floorplan> floorplanTable = StartActivity.getFloorplanTable();
    private static MobileServiceTable<UsersTable> usersTable = StartActivity.getUsersTable();
    private static MobileServiceTable<Reviews> reviewsTable = StartActivity.getReviewsTable();
    private static MobileServiceTable<UserReviews> userReviewsTable = StartActivity.getUserReviewsTable();
    private static MobileServiceTable<Bookings> bookingsTable = StartActivity.getBookingsTable();
    private static MobileServiceTable<tableObject> tableObjectsTable = StartActivity.getTableObjectsTable();
    private ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
    private ArrayList<Bookings> bookings = new ArrayList<Bookings>();
    private static ArrayList<Floorplan> floorplans = new ArrayList<Floorplan>();
    private static ArrayList<tableObject> tables = new ArrayList<tableObject>();
    private static boolean reviewExists;
    private boolean signIn;
    private static int signUpCode;
    private double distance = 0;
    private String selectedArray = "";
    private ArrayList<Reviews> rev = new ArrayList<Reviews>();
    private Context c;
    public DatabaseOperations() {
    }
    public DatabaseOperations(Context ctx) {
        c = ctx;
    }
    //http://azure.microsoft.com/en-us/documentation/articles/mobile-services-android-how-to-use-client-library/
    //https://msdn.microsoft.com/library/azure/jj554212.aspx
    private int day, month, year, intTime, i = 0;
    String u;

    public ArrayList<Restaurant> getRestaurants(double longitude,double latitude) //should get nearest restaurants
    {
        ArrayList<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
        parameters.add(new Pair<>("longitude",Double.toString(longitude)));
        parameters.add(new Pair<>("latitude",Double.toString(latitude)));
        mClient.invokeApi("getnearestrestaurants", null, "GET", parameters, new ApiJsonOperationCallback() {
            @Override
            public void onCompleted(JsonElement result, Exception exception, ServiceFilterResponse response) {
                try {
                    restaurants.clear();
                    Restaurant r;
                    for (int j = 0; j < result.getAsJsonArray().size(); j++) {
                        try {
                            r = new Restaurant(result.getAsJsonArray().get(j).getAsJsonObject().get("id").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("restaurantName").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("bio").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("longitude").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("latitude").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("type1").toString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("wheelchair").getAsBoolean(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("appImage").toString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("type2").toString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("type3").toString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("phoneNum").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("openClose").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("Email").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("wifi").getAsBoolean(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("vegan").getAsBoolean(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("Distance").getAsDouble()
                            );

                            restaurants.add(r);

                        }catch(Exception e){
                            System.out.println("IN THIS CATCH");
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        });

        return restaurants;
    }

    public void getDistanceBasedOnUsersLocation(Restaurant item) {
        distance = 6371 * Math.acos(Math.cos(Math.toRadians(Double.parseDouble(item.getLongitude())))
                * Math.cos(Math.toRadians(-6.391252))
                * Math.cos(Math.toRadians(53.284412)
                - Math.toRadians(Double.parseDouble(item.getLatitude())))
                + Math.sin(Math.toRadians(Double.parseDouble(item.getLongitude())))
                * Math.sin(Math.toRadians(-6.391252)));
    }

    public boolean validateSignIn(String email, String password) {
        usersTable.where().field("id").eq(email)
                .and().field("password").eq(password)
                .execute(new TableQueryCallback<UsersTable>() {

                    public void onCompleted(List<UsersTable> result, int count,
                                            Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            if (result.size() == 0) //was not found
                            {
                                signIn = false;
                            } else {
                                SharedPreferences settings = c.getSharedPreferences("LoginPrefs", 0);

                                if(result.get(0).isAccountVerified() == true)
                                {
                                    settings.edit().putBoolean("accountVerified", true).apply();
                                }
                                else
                                    settings.edit().putBoolean("accountVerified", false).apply();
                                signIn = true;

                            }
                        }
                    }
                });
        return signIn;
    }

    public ArrayList<Bookings> getBookingsForIndividualUser(String userID)
    {
        u = userID;
        bookings.clear();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                bookingsTable.where().field("userID").eq(u)

                        .execute(new TableQueryCallback<Bookings>() {

                            public void onCompleted(List<Bookings> result, int count,
                                                    Exception exception, ServiceFilterResponse response) {
                                if (exception == null) {
                                    for (Bookings item : result) {
                                        bookings.add(item);

                                    }
                                } else {
                                    System.out.println("ERROR GETTING BOOKINGS");
                                    exception.printStackTrace();
                                }
                            }
                        });
                return null;
            }
        }.execute();
        return bookings;
    }

    public ArrayList<Floorplan> getFloorplans(int t, int d, int m, int y, String restID) {
        intTime = t;
        day = d;
        month = m;
        year = y;
        floorplanTable.where().field("restID").eq(restID).execute(new TableQueryCallback<Floorplan>() {
            public void onCompleted(List<Floorplan> result, int count,
                                    Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    floorplans.clear();
                    tables.clear();
                    for (Floorplan item : result) {
                        floorplans.add(item);
                        tableObjectsTable.where().field("floorplanID").eq(item.getId()).execute(new TableQueryCallback<tableObject>() {
                            public void onCompleted(List<tableObject> result, int count,
                                                    Exception exception, ServiceFilterResponse response) {

                                if (exception == null) {

                                    for (tableObject table : result) {
                                        table.setColor(1);
                                        tables.add(table);

                                    }
                                } else {
                                    System.out.println("ERROR SEARCHING TABLEOBJECT TABLE");
                                    exception.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    System.out.println("ERROR SEARCHING FLOORPLAN TABLE");
                    exception.printStackTrace();
                }
            }
        });
        return floorplans;
    }

    public void postBooking(ArrayList<tableObject> selected, String userID, int day, int month, int year, int time,int numPeople) {
            ArrayList<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
            parameters.add(new Pair<>("day", Integer.toString(day)));
            parameters.add(new Pair<>("month", Integer.toString(month)));
            parameters.add(new Pair<>("year", Integer.toString(year)));
            parameters.add(new Pair<>("time", Integer.toString(time)));
            parameters.add(new Pair<>("depart", Integer.toString(time + 200)));
            parameters.add(new Pair<>("userID",userID));

        for(int i = 0; i < selected.size(); i++)
        {
            selectedArray += selected.get(i).getId() + "_";
        }
        parameters.add(new Pair<>("id",selectedArray));
            mClient.invokeApi("insertintotablebookings", null, "POST", parameters, new ApiJsonOperationCallback() {
                @Override
                public void onCompleted(JsonElement result, Exception exception, ServiceFilterResponse response) {
                    try {
                        System.out.println(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        Bookings b = new Bookings(selected.size(), userID,numPeople,day,month,year,time, MainActivity.getRestaurantToPass().getId());
        bookingsTable.insert(b, new TableOperationCallback<Bookings>() {
            public void onCompleted(Bookings entity,
                                    Exception exception,
                                    ServiceFilterResponse response) {

                if (exception == null) {
                } else {
                    System.out.println("didnt insert booking to booking tables");
                    exception.printStackTrace();
                }
            }
        });

    }


    public void getObjBookings() {
        //System.out.println(tables.size() + " is the tables array size and i is " + i);

        for (i = 0; i < tables.size(); i++) {
            ArrayList<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
            parameters.add(new Pair<>("tabob", Integer.toString(tables.get(i).getId())));
            parameters.add(new Pair<>("dayTime", Integer.toString(day)));
            parameters.add(new Pair<>("monthTime", Integer.toString(month)));
            parameters.add(new Pair<>("yearTime", Integer.toString(year)));
            parameters.add(new Pair<>("arrival", Integer.toString(intTime)));
            parameters.add(new Pair<>("leaving", Integer.toString(intTime + 200)));
            mClient.invokeApi("tablebookings", null, "GET", parameters, new ApiJsonOperationCallback() {
                @Override
                public void onCompleted(JsonElement result, Exception e, ServiceFilterResponse response) {
                    //System.out.println(result);
                    int tableIDJSON;
                    for (int j = 0; j < result.getAsJsonArray().size(); j++) {
                        try {
                            tableIDJSON = result.getAsJsonArray().get(j).getAsJsonObject().get("tabObjID").getAsInt();
                            //System.out.println(tableIDJSON + " is the json table id i got ya bish");
                            for (int k = 0; k < tables.size(); k++) {
                                if (tables.get(k).getId() == tableIDJSON) {
                                    //System.out.println("got a match with JSON ya bish");
                                    tables.get(k).setColor(2);
                                }
                            }

                        } catch (Exception error) {
                            error.printStackTrace();
                        }
                    }

                }
            });
        }

    }

    public ArrayList<Reviews> getRating() {
        reviewsTable.execute(new TableQueryCallback<Reviews>() {
            public void onCompleted(List<Reviews> result, int count,
                                    Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    for (Reviews item : result) {
                        rev.add(item);

                    }
                } else {
                    System.out.println("No Reviews found");
                    exception.printStackTrace();
                }
            }
        });
        return rev;
    }

    public void signUserUp(String email, String password) {
        UsersTable u = new UsersTable(email, password,false);
        usersTable.insert(u, new TableOperationCallback<UsersTable>() {
            public void onCompleted(UsersTable entity,
                                    Exception exception,
                                    ServiceFilterResponse response) {

                if (exception == null) {
                    signUpCode = 1; //meaning this sign up was legit
                    SharedPreferences settings = c.getSharedPreferences("LoginPrefs", 0);
                    settings.edit().putBoolean("accountVerified", false).apply();
                } else {
                    if (exception.getCause().toString().contains("400"))
                        signUpCode = 400;
                    else if (exception.getCause().toString().contains("409"))
                        signUpCode = 409;
                    exception.printStackTrace();
                }
            }
        });
    }

    public void sendReview(Reviews r, UserReviews u) {
        reviewsTable.insert(r, new TableOperationCallback<Reviews>() {
            public void onCompleted(Reviews entity,
                                    Exception exception,
                                    ServiceFilterResponse response) {

                if (exception == null) {
                    System.out.println("REVIEW WAS SENT TO DB");
                } else {
                    System.out.println("REVIEW WASNT SENT TO DB");
                    exception.printStackTrace();
                }
            }
        });

        userReviewsTable.insert(u, new TableOperationCallback<UserReviews>() {
            public void onCompleted(UserReviews entity,
                                    Exception exception,
                                    ServiceFilterResponse response) {

                if (exception == null) {
                    System.out.println("USERREVIEW WAS SENT TO DB");
                } else {
                    System.out.println("USERREVIEW WASNT SENT TO DB");
                    exception.printStackTrace();
                }
            }
        });
    }


    public ArrayList<Restaurant> searchByType(String type) {
        restaurantsTable.where().field("type1").eq(type).or().field("type2").eq(type).or().field("type3").eq(type)
                .execute(new TableQueryCallback<Restaurant>() {

                    public void onCompleted(List<Restaurant> result, int count,
                                            Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            if (result.size() == 0) //meaning the name typed was not found
                            {
                            } else {
                                restaurants.clear();
                                for (Restaurant item : result) {
                                    getDistanceBasedOnUsersLocation(item);
                                    item.setDistance(distance);
                                    restaurants.add(item);
                                }
                            }
                        } else {
                            System.out.println("Error searching for restaurant type");
                        }
                    }
                });
        return restaurants;
    }

    public void getReview(String email, String restaurant) {
        userReviewsTable.where().field("userID").eq(email).and().field("restaurantID").eq(restaurant)
                .execute(new TableQueryCallback<UserReviews>() {

                    public void onCompleted(List<UserReviews> result, int count,
                                            Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            if (result.size() == 0) //meaning the name typed was not found
                            {
                                reviewExists = false;
                                System.out.println(reviewExists + " no review existed");
                            } else {

                                reviewExists = true;
                                System.out.println(reviewExists + " review existed");
                            }
                        }
                    }
                });
        System.out.println(reviewExists);

    }

    public ArrayList<Restaurant> searchDatabaseForWheelchairFriendlyRestaurants() {
        restaurantsTable.where().field("wheelchair").eq(true)
                .execute(new TableQueryCallback<Restaurant>() {

                    public void onCompleted(List<Restaurant> result, int count,
                                            Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            if (result.size() == 0) //meaning wheelchair accessible restaurants couldn't be found
                            {
                            } else {
                                restaurants.clear();
                                for (Restaurant item : result) {
                                    getDistanceBasedOnUsersLocation(item);
                                    item.setDistance(distance);
                                    restaurants.add(item);
                                }
                            }
                        }
                    }
                });
        return restaurants;
    }

    public static int getSignUpCode() {
        return signUpCode;
    }

    public ArrayList<Restaurant> searchByName(String name) {
        name.toLowerCase();

        ArrayList<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
        parameters.add(new Pair<>("name",name));

        mClient.invokeApi("searchname", null, "GET", parameters, new ApiJsonOperationCallback() {
            @Override
            public void onCompleted(JsonElement result, Exception exception, ServiceFilterResponse response) {
                try {
                    restaurants.clear();
                    Restaurant r;
                    for (int j = 0; j < result.getAsJsonArray().size(); j++) {
                        try {
                            r = new Restaurant(result.getAsJsonArray().get(j).getAsJsonObject().get("id").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("restaurantName").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("bio").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("longitude").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("latitude").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("type1").toString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("wheelchair").getAsBoolean(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("appImage").toString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("type2").toString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("type3").toString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("phoneNum").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("openClose").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("Email").getAsString(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("wifi").getAsBoolean(),
                                    result.getAsJsonArray().get(j).getAsJsonObject().get("vegan").getAsBoolean()
                                    );
                            getDistanceBasedOnUsersLocation(r);
                            r.setDistance(distance);
                            restaurants.add(r);

                        }catch(Exception e){
                            System.out.println("IN THIS CATCH");
                            e.printStackTrace();
                            }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return restaurants;
    }

    public static boolean isReviewExists() {
        return reviewExists;
    }

    public static ArrayList<tableObject> getTables() {
        return tables;
    }

    public static ArrayList<Floorplan> getFloorplans() {
        return floorplans;
    }
}
