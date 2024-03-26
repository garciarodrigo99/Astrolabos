package es.ull.etsii.testastrolabos;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import org.jetbrains.annotations.NotNull;

/**
 * Static class for LocationRequest abstract factory
 */
public class LocationAF {
    private static final int FAST_UPDATE = 1;
    private static final int DEFAULT_UPDATE = 10;

    public static @NotNull LocationRequest getFastUpdateLocationRequest(){
        return LocationAF.getCustomLocationRequest(FAST_UPDATE,FAST_UPDATE,Priority.PRIORITY_HIGH_ACCURACY);
    }

    public static @NotNull LocationRequest getPowerBalanceLocationRequest(){
        return LocationAF.getCustomLocationRequest(DEFAULT_UPDATE,DEFAULT_UPDATE,Priority.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public static @NotNull LocationRequest getCustomLocationRequest(int intervalSeconds, int minUpdateSeconds, @Priority int priority){
        return new LocationRequest.Builder(1000L * intervalSeconds).
                setMinUpdateIntervalMillis(1000L * minUpdateSeconds).
                setPriority(priority).
                build();
    }
}
