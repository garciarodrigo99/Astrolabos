package es.ull.etsii.testastrolabos.Airport;

import org.jetbrains.annotations.NotNull;

public class Airport{
    private final String mIATACode;
    private final String mICAOCode;
    private final String mAirportName;
    private final double mLatitude;
    private final double mLongitude;
    private final int mElevation;
    private final String mTimezone;
    private final String mCityName;
    private final String mCityCode;
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
        this.mCityCode = "";
        this.mCountyName = "";
        this.mStateName = "";
        this.mCountryCode = "";
    }

    public Airport(String iataCode, String icaoCode, String airportName,
                   Double latitude, Double longitude, int elevation,
                   String timezone, String cityName, String cityCode,
                   String countyName, String stateName, String countryCode) {
        this.mIATACode = iataCode;
        this.mICAOCode = icaoCode;
        this.mAirportName = airportName;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mElevation = elevation;
        this.mTimezone = timezone;
        this.mCityName = cityName;
        this.mCityCode = cityCode;
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

    public int getElevation() {
        return mElevation;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public String getCityName() {
        return mCityName;
    }

    public String getCityCode() {
        return mCityCode;
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
