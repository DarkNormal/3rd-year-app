package project.b_ourguest.bourguest.Model;

/**
 * Created by Robbie on 30/03/2015.
 */
public class Floorplan {
    private int id,height,width,numObjects,restID;

    public Floorplan(int i,int h,int w,int n,int r)
    {
        id = i;
        height = h;
        width = w;
        numObjects = n;
        restID = r;
    }

    public int getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getNumObjects() {
        return numObjects;
    }

    public int getRestaurantID() {
        return restID;
    }
}
