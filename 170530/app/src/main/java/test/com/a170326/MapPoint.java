package test.com.a170326;

import java.io.Serializable;

/**
 * Created by Heera on 2017-03-26.
 */

public class MapPoint implements Serializable{

    private String Name;
    private double latitude;
    private double longitude;

    public MapPoint(){
        super();
    }

    public MapPoint(String Name, double latitude, double longitude) {
        this.Name = Name;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getName(){
        return Name;
    }

    public void setName(String Name){
        this.Name = Name;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
