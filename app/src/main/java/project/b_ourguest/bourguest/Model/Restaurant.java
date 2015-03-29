package project.b_ourguest.bourguest.Model;

/**
 * Created by Robbie on 16/12/2014.
 */
public class Restaurant {
    private String id;
    private String restaurantName;
    private String bio;
    private String longitude;
    private String latitude;
    private String type;
    private boolean wheelchair;
    private String appImage;
    private double distance;
    
    public Restaurant(String id, String name, String bio, String longitude, String latitude, String type, boolean wcA, String appImage){
        this.id = id;
        this.restaurantName = name;
        this.bio = bio;
        this.longitude = longitude;
        this.latitude = latitude;
        this.type = type;
        this.wheelchair = wcA;
        this.appImage = appImage;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return restaurantName;
    }
    
    public void setName(String name) {
        this.restaurantName = name;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getLongitude() {
        return longitude;
    }
    
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    
    public String getLatitude() {
        return latitude;
    }
    
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public boolean getWheelchairAccessible() {
        return wheelchair;
    }
    
    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        this.wheelchair = wheelchairAccessible;
    }
    
    public String getAppImage() {
        return appImage;
    }
    
    public void setAppImage(String appImage) {
        this.appImage = appImage;
    }
    public double getDistance() {
        return distance;
    }
    
    public void setDistance(double distance) {
        this.distance = distance;
    }
}
