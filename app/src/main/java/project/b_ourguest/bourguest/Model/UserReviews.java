package project.b_ourguest.bourguest.Model;

/**
 * Created by Robbie on 14/03/2015.
 */
public class UserReviews {
    private String id;
    private String userID;
    private String restID;

    public UserReviews(String i,String r)
    {
        userID = i;
        restID = r;
    }

    public String getRestID() {
        return restID;
    }

    public String getId() {
        return id;
    }

    public String getUserID() {
        return userID;
    }
}
