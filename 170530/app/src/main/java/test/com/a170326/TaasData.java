package test.com.a170326;

/**
 * Created by Heera on 2017-06-01.
 */

public class TaasData {

    public String taas_lat;
    public String taas_lon;

    public TaasData(){

    }

    //TAAS에서 받아온 위치들의 위도 경도 설정
    public TaasData(String taas_lon, String taas_lat){
        this.taas_lon = taas_lon;
        this.taas_lat = taas_lat;
    }
}