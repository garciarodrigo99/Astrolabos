package es.ull.etsii.testastrolabos;


public class TrackSettings{
    public static final String DEFAULT_FLIGHT_NAME = "default_track_name";
    public static final int DEFAULT_MAX_VALUE = 60;
    public static final int DEFAULT_MIN_VALUE = 5;
    private final String flight_name;
    private final Integer max_value;
    private final Integer min_value;

    public TrackSettings(String flight_name, Integer max_value, Integer min_value) {
        this.flight_name = flight_name;
        this.max_value = max_value;
        this.min_value = min_value;
    }

    public String getFlightName() {
        return flight_name;
    }

    public Integer getMaxValue() {
        return max_value;
    }

    public Integer getMinValue() {
        return min_value;
    }

    public static TrackSettings getDefaultSettings(){
        return new TrackSettings(DEFAULT_FLIGHT_NAME,DEFAULT_MAX_VALUE,DEFAULT_MIN_VALUE);
    }
}