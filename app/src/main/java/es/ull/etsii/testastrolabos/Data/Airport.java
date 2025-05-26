package es.ull.etsii.testastrolabos.Data;

import org.jetbrains.annotations.NotNull;
import org.mapsforge.core.model.LatLong;

public class Airport{
    private final String mIATACode;
    private final String mICAOCode;
    private final String mAirportName;
    private final double mLatitude;
    private final double mLongitude;
    private final int mElevation;
    private final String mTimezone;
    private final String mCityName;
    private final String mCountyName;
    private final String mStateName;
    private final String mCountryCode;

    public Airport(){
        this.mIATACode = "";
        this.mICAOCode = "";
        this.mAirportName = "";
        this.mLatitude = 0.0;
        this.mLongitude = 0.0;
        this.mElevation = 0;
        this.mTimezone = "";
        this.mCityName = "";
        this.mCountyName = "";
        this.mStateName = "";
        this.mCountryCode = "";
    }

    public Airport(String iataCode, String icaoCode, String airportName,
                   Double latitude, Double longitude, int elevation,
                   String timezone, String cityName,
                   String countyName, String stateName, String countryCode) {
        this.mIATACode = iataCode;
        this.mICAOCode = icaoCode;
        this.mAirportName = airportName;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mElevation = elevation;
        this.mTimezone = timezone;
        this.mCityName = cityName;
        this.mCountyName = countyName;
        this.mStateName = stateName;
        this.mCountryCode = countryCode;
    }
    public String getCodeIATA() {
        return mIATACode;
    }

    public String getCodeICAO() {
        return mICAOCode;
    }

    public String getAirportName() {
        return mAirportName;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public LatLong getLatLong() {
        return new LatLong(mLatitude, mLongitude);
    }
    public int getElevation() {
        return mElevation;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public String getCityName() {
        return mCityName;
    }

    public String getCountyName() {
        return mCountyName;
    }

    public String getStateName() {
        return mStateName;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    @Override
    public @NotNull String toString() {
        return mIATACode + ", " +
                mICAOCode + ", " +
                mAirportName + ", " +
                mCityName + ", " +
                mCountyName + ", " +
                mStateName + ", " +
                mCountryCode;
    }
}
