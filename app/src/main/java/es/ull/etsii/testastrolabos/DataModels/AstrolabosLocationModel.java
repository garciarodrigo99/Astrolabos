package es.ull.etsii.testastrolabos.DataModels;

import android.location.Location;
import android.os.Build;
import org.mapsforge.core.model.LatLong;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AstrolabosLocationModel {
    /**
     * The application does not have granted location permissions
     */
    public static final int LOCATION_PERMISSIONS_NOT_GRANTED = -1000;
    /**
     * The application has granted location permissions, but the mobile phone
     * location updates are not enabled
     */
    public static final int LOCATION_NOT_ACTIVATED = -1001;
    /**
     * The app has permissions granted y mobile phone location updates are
     * enabled, but the user in the app does not allow location updates
     */
    public static final int LOCATION_UPDATES_NOT_ENABLED = -1002;
    /**
     * Some data like speed or bearing are not always available in some
     * location updates
     */
    public static final int LOCATION_WITH_NO_DATA = -1003;
    public static final String UNAVAILABLE_DATA = "-";

    private LatLong mPosition;
    private SpeedModel mSpeed;
    private AltitudeModel mAltitude;
    private BearingModel mBearing;
    private SatellitesModel mSatellites;
    private Date mLastTime;
    public final SimpleDateFormat DATE_FORMAT;
    private int mAccuracy;
    public AstrolabosLocationModel() {
        this.mPosition = new LatLong(0.0, 0.0);
        this.mSpeed = new SpeedModel();
        this.mAltitude = new AltitudeModel();
        this.mBearing = new BearingModel();
        this.mSatellites = new SatellitesModel();
        DATE_FORMAT = new SimpleDateFormat("HH:mm:ss",
                Locale.getDefault());
    }
    public void updateLocation(Location location) {
        this.mPosition = new LatLong(location.getLatitude(), location.getLongitude());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this.mAltitude.setAltitude(location.hasMslAltitude() ? location.getMslAltitudeMeters() : LOCATION_WITH_NO_DATA);
        } else {
            this.mAltitude.setAltitude(location.hasAltitude() ? location.getAltitude() : LOCATION_WITH_NO_DATA);
        }
        this.mSpeed.setSpeed(location.hasSpeed() ? location.getSpeed() : LOCATION_WITH_NO_DATA);
        this.mBearing.setBearing(location.hasBearing() ? location.getBearing() : LOCATION_WITH_NO_DATA);
        this.mLastTime = new Date(location.getTime());
        this.mAccuracy = (int) location.getAccuracy();
    }

    public LatLong getPosition() {
        return mPosition;
    }

    public SpeedModel getSpeed() {
        return mSpeed;
    }

    public AltitudeModel getAltitude() {
        return mAltitude;
    }

    public BearingModel getBearing() {
        return mBearing;
    }

    public SatellitesModel getSatellites() {
        return mSatellites;
    }
    public Date getLastTime() {
        return mLastTime;
    }
    public int getAccuracy() {
        return mAccuracy;
    }
}
