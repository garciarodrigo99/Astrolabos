package es.ull.etsii.testastrolabos;


import es.ull.etsii.testastrolabos.Airport.Airport;

public class TrackSettings{
    public static final String DEFAULT_FLIGHT_NAME = "default_track_name";
    public static final int DEFAULT_MAX_VALUE = 10;
    public static final int DEFAULT_MIN_VALUE = 1;
    private final String mFlightName;
    private final Integer mMaxValue;
    private final Integer mMinValue;
    private final boolean mIsFreeTracking;
    private final Airport mOriginAirport;
    private final Airport mDestinationAirport;

    public TrackSettings(String flightName, Integer maxValue, Integer minValue,
                        boolean isFreeTracking, Airport originAirport, Airport destinationAirport) {
        this.mFlightName = flightName;
        this.mMaxValue = maxValue;
        this.mMinValue = minValue;
        this.mIsFreeTracking = isFreeTracking;
        this.mOriginAirport = originAirport;
        this.mDestinationAirport = destinationAirport;
    }

    public TrackSettings(String flightName, Integer maxValue, Integer minValue,
                         boolean isFreeTracking){
        this(flightName,maxValue,minValue,isFreeTracking,null,null);
    }

    public String getFlightName() {
        return mFlightName;
    }

    public Integer getMaxValue() {
        return mMaxValue;
    }

    public Integer getMinValue() {
        return mMinValue;
    }

    public boolean isFreeTracking() {
        return mIsFreeTracking;
    }

    public Airport getOriginAirport() {
        return mOriginAirport;
    }

    public Airport getDestinationAirport() {
        return mDestinationAirport;
    }

    public static TrackSettings getDefaultSettings(){
        return new TrackSettings(DEFAULT_FLIGHT_NAME, DEFAULT_MAX_VALUE, DEFAULT_MIN_VALUE,
                false, null,null);
    }
}