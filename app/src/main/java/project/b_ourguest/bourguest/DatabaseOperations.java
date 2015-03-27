package project.b_ourguest.bourguest;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbie on 06/02/2015.
 */
public class DatabaseOperations {
    private static MobileServiceClient mClient = StartActivity.getMobileServiceClient();
    private static MobileServiceTable<Restaurant> restaurantsTable = StartActivity.getRestaurantsTable();
    private static MobileServiceTable<Users> usersTable = StartActivity.getUsersTable();
    private static MobileServiceTable<Reviews> reviewsTable = StartActivity.getReviewsTable();
    private static MobileServiceTable<UserReviews> userReviewsTable = StartActivity.getUserReviewsTable();
    private ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
    private static boolean reviewExists;
    private boolean signIn;
    private static int signUpCode;
    private double distance = 0;
    private ArrayList<Reviews> rev = new ArrayList<Reviews>();
    
    public DatabaseOperations(){};
    //http://azure.microsoft.com/en-us/documentation/articles/mobile-services-android-how-to-use-client-library/
    //https://msdn.microsoft.com/library/azure/jj554212.aspx
    
    public ArrayList<Restaurant> getRestaurants() //should get nearest restaurants
    {
        restaurantsTable.execute(new TableQueryCallback<Restaurant>() {
            public void onCompleted(List<Restaurant> result, int count,
                                    Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    restaurants.clear();
                    for (Restaurant item : result) {
                        getDistanceBasedOnUsersLocation(item);
                        if(distance < 3)
                        {
                            item.setDistance(distance);
                            restaurants.add(item);
                        }
                        System.out.println("Restaurant name: " + item.getName() + "\nRestaurant ID: " + item.getId());
                        System.out.println("Distance: " + distance);
                        System.out.println("SIZE OF RESTAURANT ARRAY " + restaurants.size());
                    }
                }
                else
                {
                    exception.printStackTrace();
                }
            }
        });
        return restaurants;
    }
    
    public void getDistanceBasedOnUsersLocation(Restaurant item)
    {
        distance = 6371 * Math.acos( Math.cos( Math.toRadians(Double.parseDouble(item.getLongitude())) )
                                    * Math.cos( Math.toRadians( -6.391252 ) )
                                    * Math.cos( Math.toRadians( 53.284412 )
                                               -  Math.toRadians(Double.parseDouble(item.getLatitude())) )
                                    +  Math.sin( Math.toRadians(Double.parseDouble(item.getLongitude())) )
                                    * Math.sin( Math.toRadians( -6.391252 ) ) );
    }
    
    public boolean validateSignIn(String email,String password)
    {
        usersTable.where().field("id").eq(email)
        .and().field("password").eq(password)
        .execute(new TableQueryCallback<Users>() {
            
            public void onCompleted(List<Users> result, int count,
                                    Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    if (result.size() == 0) //was not found
                    {
                        signIn = false;
                    } else {
                        signIn = true;
                        
                    }
                }
            }
        });
        return signIn;
    }
    
    public ArrayList<Reviews> getRating(){
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
    
    public void signUserUp(String email,String password)
    {
        Users u = new Users(email, password);
        System.out.println("HEYA_--------------------------");
        usersTable.insert(u, new TableOperationCallback<Users>() {
            public void onCompleted(Users entity,
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
    
    public void sendReview(Reviews r,UserReviews u)
    {
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
    
    
    public ArrayList<Restaurant> searchByType(String type)
    {
        System.out.println(type + "-------------------------------------");
        restaurantsTable.where().field("type").eq(type)
        .execute(new TableQueryCallback<Restaurant>() {
            
            public void onCompleted(List<Restaurant> result, int count,
                                    Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    if(result.size() == 0) //meaning the name typed was not found
                    {
                        System.out.println("FOUND NOTHING-------------------------------------");
                    }
                    else {
                        restaurants.clear();
                        System.out.println("found restaurants with that type");
                        for(Restaurant item : result)
                        {
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
    
    public void getReview(String email,String restaurant)
    {
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
                    } else {
                        System.out.println("REVIEW EXISTS---------------");
                        System.out.println(result.get(0).getUserID() + ", " + result.get(0).getRestaurantID());
                        reviewExists = true;
                    }
                }
            }
        });
        System.out.println("FINISHED GETTING REVIEWS---------------");
        
    }
    
    public ArrayList<Restaurant> searchDatabaseForWheelchairFriendlyRestaurants()
    {
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
    
    public ArrayList<Restaurant> searchByName(String name)
    {
        name.toLowerCase();
        restaurantsTable.where().field("name").eq(name)
        .execute(new TableQueryCallback<Restaurant>() {
            
            public void onCompleted(List<Restaurant> result, int count,
                                    Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    if(result.size() == 0) //meaning the name typed was not found
                    {
                        
                    }
                    else {
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
    public static boolean isReviewExists() {
        return reviewExists;
    }
}
