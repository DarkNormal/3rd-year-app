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
    private String openClose;
    private String Email;
    private boolean wifi;
    private boolean vegan;


    public Restaurant(String id, String restaurantName, String bio, String longitude, String latitude, String type1,
                      boolean wheelchair, String appImage,String type2, String type3,
                       String phoneNum, String openClose, String email,
                      boolean wifi, boolean vegan,double distance) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.bio = bio;
        this.longitude = longitude;
        this.latitude = latitude;
        this.type1 = type1;
        this.type2 = type2;
        this.type3 = type3;
        this.wheelchair = wheelchair;
        this.appImage = appImage;
        this.phoneNum = phoneNum;
        this.openClose = openClose;
        Email = email;
        this.wifi = wifi;
        this.vegan = vegan;
        this.distance = distance;
    }

    public Restaurant(String id, String name, String bio, String longitude, String latitude, String t1, boolean wcA,
                      String appImage,String t2,String t3,String p,String o,String e,boolean wifi,boolean vegan){
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
        this.openClose = o;
        this.Email = e;
        this.wifi = wifi;
        this.vegan = vegan;
    }

    public boolean isWifi() {
        return wifi;
    }
    public boolean isVegan() {
        return vegan;
    }
    public String getEmail() {
        return Email;
    }
    public String getOpeningHours() {
        return openClose;
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
