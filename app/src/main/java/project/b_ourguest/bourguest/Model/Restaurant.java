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
    private String type1;
    private String type2;
    private String type3;
    private boolean wheelchair;
    private String appImage;
    private double distance;
    private String phoneNum;
    private String openingHours;
    private String email;
    
    public Restaurant(String id, String name, String bio, String longitude, String latitude, String t1, boolean wcA,
                      String appImage,String t2,String t3,String p,String o,String e){
        this.id = id;
        this.restaurantName = name;
        this.bio = bio;
        this.longitude = longitude;
        this.latitude = latitude;
        this.type1 = t1;
        this.wheelchair = wcA;
        this.appImage = appImage;
        this.type2 = t2;
        this.type3 = t3;
        this.phoneNum = p;
        this.email = e;
        this.openingHours = o;
    }

    public String getEmail() {
        return email;
    }
    public String getOpeningHours() {
        return openingHours;
    }
    public String getType3() {
        return type3;
    }
    public String getType2() {
        return type2;
    }
    public String getType1() {
        return type1;
    }
    public String getPhoneNum() {
        return phoneNum;
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
    public String getLongitude() {
        return longitude;
    }
    public String getLatitude() {
        return latitude;
    }
    public boolean getWheelchairAccessible() {
        return wheelchair;
    }
    public String getAppImage() {
        return appImage;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
}
