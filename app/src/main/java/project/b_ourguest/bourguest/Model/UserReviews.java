package project.b_ourguest.bourguest.Model;

/**
 * Created by Robbie on 14/03/2015.
 */
public class UserReviews {
    private String id;
    private String userID;
    private String restaurantID;

    public UserReviews(String i,String r)
    {
        userID = i;
        restaurantID = r;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public String getId() {
        return id;
    }

    public String getUserID() {
        return userID;
    }
}
