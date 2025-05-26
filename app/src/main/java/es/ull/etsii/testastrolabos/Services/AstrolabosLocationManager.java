package es.ull.etsii.testastrolabos.Services;

import android.annotation.SuppressLint;
import android.location.GnssStatus;
import android.location.LocationManager;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.location.*;
import es.ull.etsii.testastrolabos.MainActivity;
import es.ull.etsii.testastrolabos.R;
import org.jetbrains.annotations.NotNull;

/**
 * Static class for LocationRequest abstract factory
 */
public final class AstrolabosLocationManager {
    public @interface Sensor {
        int GPS = 100;
        int CELL_DATA = 102;
    }
    public enum State{
        UPDATING,
        NOT_UPDATING
    }
    private State state = State.NOT_UPDATING;
    private static AstrolabosLocationManager mInstance;
    private MainActivity mMainActivity;
    private final int kFastUpdate = 1;
    private final int kDefaultUpdate = 10;
    private LocationRequest mAppLocationRequest;
    private LocationCallback mLocationCallBack;
    private GnssStatus.Callback mGnssStatusCallback;

    // Class to use location info
    private FusedLocationProviderClient mLocationClient;

    private AstrolabosLocationManager(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;
        mLocationCallBack = new LocationCallback() {
            // At this point location permissions are granted and location services activated
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // save location
                mMainActivity.writeLocation(locationResult.getLastLocation());
            }
        };
        mLocationClient = LocationServices.getFusedLocationProviderClient(mMainActivity);
    }

    public static AstrolabosLocationManager getInstance(MainActivity mainActivity) {
        if (mInstance == null) {
            mInstance = new AstrolabosLocationManager(mainActivity);
        }
        return mInstance;
    }

    /**
     * Method that activate the trigger function locationCallBack with the LocationRequest settings
     * @see  com.google.android.gms.location.LocationCallback
     */
    //    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        if (!mMainActivity.hasLocationPermissions()) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        setFastUpdateLocationRequest();
        mLocationClient.requestLocationUpdates(mAppLocationRequest, mLocationCallBack, null);
        registerGnssStatusCallback();
    }

    /**
     *
     */
    @SuppressLint("MissingPermission")
    private void registerGnssStatusCallback() {
        if (mGnssStatusCallback == null) {
            mGnssStatusCallback = new GnssStatus.Callback() {
                @Override
                public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                    mMainActivity.writeGnssStatus(status);
                }
            };
        }

        LocationManager locationManager = (LocationManager) mMainActivity.getSystemService(MainActivity.LOCATION_SERVICE);
        if (locationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                locationManager.registerGnssStatusCallback(
                        mMainActivity.getMainExecutor(),
                        mGnssStatusCallback
                );
            } else {
                locationManager.registerGnssStatusCallback(mGnssStatusCallback);
            }
        }
    }

    public void updateLocationRequest(int newInterval, int newPriority,int sensor) {
        mLocationClient.removeLocationUpdates(mLocationCallBack); // Detiene el request actual
        setCustomLocationRequest(newInterval, newPriority, sensor); // Inicia con los nuevos parámetros
    }

    /**
     * Method that stop the trigger function locationCallBack
     * @see  com.google.android.gms.location.LocationCallback
     */
    public void stopLocationUpdates() {
        mLocationClient.removeLocationUpdates(mLocationCallBack);
    }

    public void setFastUpdateLocationRequest(){
        setCustomLocationRequest(kFastUpdate,kFastUpdate,Sensor.GPS);
    }

    public void setPowerBalanceLocationRequest(){
        setCustomLocationRequest(kDefaultUpdate,kDefaultUpdate,Sensor.CELL_DATA);
    }

    private void setCustomLocationRequest(int intervalSeconds, int minUpdateSeconds, @Priority int priority){
        mAppLocationRequest =  new LocationRequest.Builder(1000L * intervalSeconds).
                setMinUpdateIntervalMillis(1000L * minUpdateSeconds).
                setPriority(priority).
                build();
    }

    @SuppressLint("MissingPermission")
    public void getLastKnownLocation(){
        mLocationClient.getLastLocation().addOnSuccessListener(mMainActivity, location -> {
            // We got permissions. Put the values in the GUI
            Toast.makeText(mMainActivity,
                            mMainActivity.getString(R.string.location_updated),
                            Toast.LENGTH_SHORT).
                    show();

            mMainActivity.writeLocation(location);
        });
    }
}
