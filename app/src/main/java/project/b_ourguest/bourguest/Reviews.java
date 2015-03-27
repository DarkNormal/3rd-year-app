package project.b_ourguest.bourguest;

/**
 * Created by Robbie on 07/03/2015.
 */
public class Reviews {
    private String id;
    private double rating;
    private double numReviews;
    private double reviews;

    public Reviews(String s,double d)
    {
        id = s;
        reviews = d;
    }
    public Reviews(String s,double d, double crap)
    {
        id = s;
        rating = crap;
        
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
}
