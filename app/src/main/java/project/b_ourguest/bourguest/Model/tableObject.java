package project.b_ourguest.bourguest.Model;

/**
 * Created by Robbie on 30/03/2015.
 */
public class tableObject {
    private int id,xcoord,ycoord,objType,floorplanID;
    private boolean available;
    private int color;

    public tableObject(int id, int xcoord, int ycoord, int objType, int floorplanID, boolean available) {
        this.id = id;
        this.xcoord = xcoord;
        this.ycoord = ycoord;
        this.objType = objType;
        this.floorplanID = floorplanID;
        this.available = available;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
    public int getId() {
        return id;
    }

    public int getXcoord() {
        return xcoord;
    }

    public int getYcoord() {
        return ycoord;
    }

    public int getObjType() {
        return objType;
    }

    public int getFloorplanID() {
        return floorplanID;
    }

    public boolean isAvailable() {
        return available;
    }

}
