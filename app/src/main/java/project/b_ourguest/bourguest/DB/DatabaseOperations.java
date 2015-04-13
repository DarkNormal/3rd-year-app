package project.b_ourguest.bourguest.DB;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Pair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import java.util.ArrayList;
import java.util.List;

import project.b_ourguest.bourguest.Model.Bookings;
import project.b_ourguest.bourguest.Model.Floorplan;
import project.b_ourguest.bourguest.Model.Restaurant;
import project.b_ourguest.bourguest.Model.Reviews;
import project.b_ourguest.bourguest.Model.UsersTable;
import project.b_ourguest.bourguest.Model.tableObject;
import project.b_ourguest.bourguest.Model.tableObjectBookings;
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
    private static MobileServiceTable<tableObjectBookings> tableObjectBookingsTable = StartActivity.getTableObjectBookingsTable();
    private ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
    private ArrayList<Bookings> bookings = new ArrayList<Bookings>();
    private static String returnString = "Tables ";
    private static ArrayList<Floorplan> floorplans = new ArrayList<Floorplan>();
    private static ArrayList<tableObject> tables = new ArrayList<tableObject>();
    private static boolean reviewExists;
    private boolean signIn;
    private static int signUpCode;
    private double distance = 0;
    private static boolean found = false;
    private ArrayList<Reviews> rev = new ArrayList<Reviews>();
    private Context c;
    public DatabaseOperations() {
    }
    public DatabaseOperations(Context ctx) {
        c = ctx;
    }


    //http://azure.microsoft.com/en-us/documentation/articles/mobile-services-android-how-to-use-client-library/
    //https://msdn.microsoft.com/library/azure/jj554212.aspx
    private String time;
    private int day, month, year, intTime, i = 0;
    String u;

    public ArrayList<Restaurant> getRestaurants() //should get nearest restaurants
    {
        restaurantsTable.execute(new TableQueryCallback<Restaurant>() {
            public void onCompleted(List<Restaurant> result, int count,
                                    Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    restaurants.clear();
                    for (Restaurant item : result) {
                        getDistanceBasedOnUsersLocation(item);
                        item.setDistance(distance);
                        if (distance < 3) {
                            restaurants.add(item);
                        }
                        System.out.println("Restaurant name: " + item.getName() + "\nRestaurant ID: " + item.getId());
                        System.out.println("Distance: " + distance);
                        System.out.println("SIZE OF RESTAURANT ARRAY " + restaurants.size());
                    }
                } else {
                    exception.printStackTrace();
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
                                System.out.println("HELOOOOOOOOOOOOOOOOO" + settings.getBoolean("accountVerified", true));
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
        System.out.println("USER ID IN DB CLASS : " + u);
        bookings.clear();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                bookingsTable.where().field("userID").eq(u)

                        .execute(new TableQueryCallback<Bookings>() {

                            public void onCompleted(List<Bookings> result, int count,
                                                    Exception exception, ServiceFilterResponse response) {
                                if (exception == null) {
                                    System.out.println("NO ERROR WHEN SEARCHING FOR BOOKINGS");

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
                System.out.println("SEARCHING FLOORPLAN TABLE");
                if (exception == null) {
                    floorplans.clear();
                    tables.clear();
                    for (Floorplan item : result) {
                        floorplans.add(item);
                        System.out.println("floorplan id: " + item.getId() + "\nHeight: " + item.getHeight() +
                                "\nWidth: " + item.getWidth() + "\nNumObjects: " + item.getNumObjects() +
                                "\nRestaurantID: " + item.getRestaurantID());
                        tableObjectsTable.where().field("floorplanID").eq(item.getId()).execute(new TableQueryCallback<tableObject>() {
                            public void onCompleted(List<tableObject> result, int count,
                                                    Exception exception, ServiceFilterResponse response) {
                                System.out.println("SEARCHING tableObjectsTable");
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
                        System.out.println("FINISHED GETTING TABLES");
                    }
                } else {
                    System.out.println("ERROR SEARCHING FLOORPLAN TABLE");
                    exception.printStackTrace();
                }
            }
        });
        System.out.println("day: " + day + " month " + month + " year " + year);
        return floorplans;
    }

    public void postBooking(ArrayList<tableObject> selected, String userID, int day, int month, int year, int time,int numPeople) {
        System.out.println("day: " + day + " month " + month + " year " + year);
        for (int i = 0; i < selected.size(); i++) {
            ArrayList<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
            parameters.add(new Pair<>("day", Integer.toString(day)));
            parameters.add(new Pair<>("month", Integer.toString(month)));
            parameters.add(new Pair<>("year", Integer.toString(year)));
            parameters.add(new Pair<>("tabObjID", Integer.toString(selected.get(i).getId())));
            parameters.add(new Pair<>("time", Integer.toString(time)));
            parameters.add(new Pair<>("depart", Integer.toString(time + 200)));
            parameters.add(new Pair<>("userID",userID));
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
        }
        Bookings b = new Bookings(selected.size(), userID,numPeople,day,month,year,time);
        bookingsTable.insert(b, new TableOperationCallback<Bookings>() {
            public void onCompleted(Bookings entity,
                                    Exception exception,
                                    ServiceFilterResponse response) {

                if (exception == null) {
                    System.out.println("Inserted booking to booking tables");
                } else {
                    System.out.println("didnt insert booking to booking tables");
                    exception.printStackTrace();
                }
            }
        });

    }


    public void getObjBookings() {
        System.out.println(tables.size() + " is the tables array size and i is " + i);

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
                    System.out.println(result);
                    int tableIDJSON;
                    for (int j = 0; j < result.getAsJsonArray().size(); j++) {
                        try {
                            tableIDJSON = result.getAsJsonArray().get(j).getAsJsonObject().get("tabObjID").getAsInt();
                            System.out.println(tableIDJSON + " is the json table id i got ya bish");
                            for (int k = 0; k < tables.size(); k++) {
                                if (tables.get(k).getId() == tableIDJSON) {
                                    System.out.println("got a match with JSON ya bish");
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

    public void confimBookings(final ArrayList<tableObject> bookings) {
        System.out.println(bookings.size() + " is the bookings array size and i is " + i);
        found = false;

        for (i = 0; i < bookings.size(); i++) {
            ArrayList<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
            parameters.add(new Pair<>("tabob", Integer.toString(bookings.get(i).getId())));
            parameters.add(new Pair<>("dayTime", Integer.toString(day)));
            parameters.add(new Pair<>("monthTime", Integer.toString(month)));
            parameters.add(new Pair<>("yearTime", Integer.toString(year)));
            parameters.add(new Pair<>("arrival", Integer.toString(intTime)));
            parameters.add(new Pair<>("leaving", Integer.toString(intTime + 200)));
            mClient.invokeApi("tablebookings", null, "GET", parameters, new ApiJsonOperationCallback() {
                @Override
                public void onCompleted(JsonElement result, Exception e, ServiceFilterResponse response) {
                    System.out.println(result);
                    int tableIDJSON;
                    for (int j = 0; j < result.getAsJsonArray().size(); j++) {
                        try {
                            tableIDJSON = result.getAsJsonArray().get(j).getAsJsonObject().get("tabObjID").getAsInt();
                            System.out.println(tableIDJSON + " is the json table id i got ya bish");
                            for (int k = 0; k < bookings.size(); k++) {
                                if (bookings.get(k).getId() == tableIDJSON) {
                                    System.out.println("got a match with JSON ya bish");
                                    found = true;
                                    returnString += bookings.get(k).getId() + ",";
                                }
                            }
                            returnString += " have already been booked";

                        } catch (Exception error) {
                            error.printStackTrace();
                        }
                    }

                }
            });
        }
    }

    public ArrayList<Reviews> getRating() {
        System.out.println("REVIEWS QUERY BEING EXECUTED----------");
        reviewsTable.execute(new TableQueryCallback<Reviews>() {
            public void onCompleted(List<Reviews> result, int count,
                                    Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    System.out.println("Reviews found");
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
        System.out.println("HEYA_--------------------------");
        usersTable.insert(u, new TableOperationCallback<UsersTable>() {
            public void onCompleted(UsersTable entity,
                                    Exception exception,
                                    ServiceFilterResponse response) {

                if (exception == null) {
                    signUpCode = 1; //meaning this sign up was legit
                    System.out.println("heya CODE IS " + signUpCode + "--------------------------");
                } else {
                    if (exception.getCause().toString().contains("400"))
                        signUpCode = 400;
                    else if (exception.getCause().toString().contains("409"))
                        signUpCode = 409;
                    exception.printStackTrace();
                }
            }
        });

        System.out.println("This is return code CODE IS " + signUpCode + "--------------------------");
    }

    public void sendReview(Reviews r, UserReviews u) {
        System.out.println(r.getId() + " " + r.getRating() + " " + u.getId() + " " + u.getRestaurantID() + " " + u.getUserID());
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
        System.out.println(type + "-------------------------------------");
        restaurantsTable.where().field("type1").eq(type).or().field("type2").eq(type).or().field("type3").eq(type)
                .execute(new TableQueryCallback<Restaurant>() {

                    public void onCompleted(List<Restaurant> result, int count,
                                            Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            if (result.size() == 0) //meaning the name typed was not found
                            {
                                System.out.println("FOUND NOTHING-------------------------------------");
                            } else {
                                restaurants.clear();
                                System.out.println("found restaurants with that type");
                                for (Restaurant item : result) {
                                    getDistanceBasedOnUsersLocation(item);
                                    item.setDistance(distance);
                                    restaurants.add(item);

                                    System.out.println("Restaurant name: " + item.getName() + "\nRestaurant ID: " + item.getId());
                                    System.out.println("Distance: " + distance);

                                }
                            }
                        } else {
                            System.out.println("Error searching for restaurant type");
                        }
                    }
                });
        System.out.println("number of restaurants with that type: " + restaurants.size());
        return restaurants;
    }

    public void getReview(String email, String restaurant) {
        System.out.println("GETTING REVIEW -------------------------------------");
        userReviewsTable.where().field("userID").eq(email).and().field("restaurantID").eq(restaurant)
                .execute(new TableQueryCallback<UserReviews>() {

                    public void onCompleted(List<UserReviews> result, int count,
                                            Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            if (result.size() == 0) //meaning the name typed was not found
                            {
                                System.out.println("NO REVIEW EXISTED---------------");
                                reviewExists = false;
                                System.out.println(reviewExists + " no review existed");
                            } else {
                                System.out.println("REVIEW EXISTS---------------");
                                System.out.println(result.get(0).getUserID() + ", " + result.get(0).getRestaurantID());
                                reviewExists = true;
                                System.out.println(reviewExists + " review existed");
                            }
                        }
                    }
                });
        System.out.println("FINISHED GETTING REVIEWS---------------");
        System.out.println(reviewExists);

    }

    public ArrayList<Restaurant> searchDatabaseForWheelchairFriendlyRestaurants() {
        System.out.println("IN METHOD");
        restaurantsTable.where().field("wheelchair").eq(true)
                .execute(new TableQueryCallback<Restaurant>() {

                    public void onCompleted(List<Restaurant> result, int count,
                                            Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            if (result.size() == 0) //meaning wheelchair accessible restaurants couldn't be found
                            {
                                System.out.println("GOT NOTHING");
                            } else {
                                System.out.println("GOT SOMETHING");
                                restaurants.clear();
                                for (Restaurant item : result) {
                                    getDistanceBasedOnUsersLocation(item);
                                    item.setDistance(distance);
                                    restaurants.add(item);
                                }
                                System.out.println("Size is " + restaurants.size());
                            }
                        }
                    }
                });
        System.out.println("Size before return is " + restaurants.size());
        return restaurants;
    }

    public static int getSignUpCode() {
        return signUpCode;
    }

    public ArrayList<Restaurant> searchByName(String name) {
        name.toLowerCase();
//        restaurantsTable.where().field("name").eq(name)
//                .execute(new TableQueryCallback<Restaurant>() {
//
//                    public void onCompleted(List<Restaurant> result, int count,
//                                            Exception exception, ServiceFilterResponse response) {
//                        if (exception == null) {
//                            if (result.size() == 0) //meaning the name typed was not found
//                            {
//
//                            } else {
//                                restaurants.clear();
//                                for (Restaurant item : result) {
//                                    getDistanceBasedOnUsersLocation(item);
//                                    item.setDistance(distance);
//                                    restaurants.add(item);
//                                }
//                            }
//                        }
//                    }
//                });

        ArrayList<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
        parameters.add(new Pair<>("name",name));

        mClient.invokeApi("searchname", null, "GET", parameters, new ApiJsonOperationCallback() {
            @Override
            public void onCompleted(JsonElement result, Exception exception, ServiceFilterResponse response) {
                try {
                    restaurants.clear();
                    Restaurant r;
                    System.out.println(result);
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

    public static boolean isFound() {
        return found;
    }

    public static boolean isReviewExists() {
        System.out.println(reviewExists + " IS REVIEWEXISTS");
        return reviewExists;
    }

    public static String getReturnString() {
        return returnString;
    }

    public static ArrayList<tableObject> getTables() {
        return tables;
    }

    public static ArrayList<Floorplan> getFloorplans() {
        return floorplans;
    }
}
