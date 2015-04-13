package project.b_ourguest.bourguest.Model;

/**
 * Created by marklordan on 02/04/15.
 */
public class Bookings {
    private int id;
    private int numPeople;
    private int numTables;
    private int day;
    private int month;
    private int year;
    private int time;
    private String userID;

    public Bookings( int numTables, String userID,int numPeople,int day,int month,int year,int time) {
        this.numTables = numTables;
        this.userID = userID;
        this.numPeople = numPeople;
        this.day = day;
        this.month = month;
        this.year = year;
        this.time = time;
    }
    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getTime() {
        return time;
    }
    public int getNumPeople() {
        return numPeople;
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
