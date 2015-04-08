package project.b_ourguest.bourguest.Model;

/**
 * Created by Robbie on 07/03/2015.
 */
public class Reviews {
    private String id;
    private String restID;
    private double rating;
    private double numReviews;
    private double reviews;

    public Reviews(String s,double d)
    {
        restID = s;
        reviews = d;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestID() {
        return restID;
    }
}
