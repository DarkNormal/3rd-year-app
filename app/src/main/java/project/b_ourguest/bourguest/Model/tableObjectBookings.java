package project.b_ourguest.bourguest.Model;

/**
 * Created by Robbie on 30/03/2015.
 */
public class tableObjectBookings {
    private int id;
    private int day;
    private int month;
    private int year;
    private int tabObjID;
    private String time;
    private String userID;

    public tableObjectBookings(int id, int day, int month, int year,int tabObjID, String time) {
        this.id = id;
        this.day = day;
        this.month = month;
        this.year = year;
        this.tabObjID = tabObjID;
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }
    public int getTabObjID() {
        return tabObjID;
    }

    public String getTime() {
        return time;
    }

    public int getId() {
        return id;
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
}
