package org.example.models;

public class Station {

    private boolean passengerTraffic;
    private String type;
    private String stationName;
    private String stationShortCode;
    private int stationUICCode;
    private String countryCode;
    private float longitude;
    private float latitude;

    public Station() {
    }

    public boolean isPassengerTraffic() {
        return passengerTraffic;
    }

    public String getType() {
        return type;
    }

    public String getStationName() {
        return stationName;
    }

    public String getStationShortCode() {
        return stationShortCode;
    }

    public int getStationUICCode() {
        return stationUICCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setPassengerTraffic(boolean passengerTraffic) {
        this.passengerTraffic = passengerTraffic;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public void setStationShortCode(String stationShortCode) {
        this.stationShortCode = stationShortCode;
    }

    public void setStationUICCode(int stationUICCode) {
        this.stationUICCode = stationUICCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
}
