package project.b_ourguest.bourguest.Model;

/**
 * Created by marklordan on 02/04/15.
 */
public class Bookings {
    private int id,numTables;
    private String userID;

    public Bookings( int numTables, String userID) {
        this.numTables = numTables;
        this.userID = userID;
    }

    public int getId() {
        return id;
    }

    public int getNumTables() {
        return numTables;
    }

    public String getUserID() {
        return userID;
    }
}
