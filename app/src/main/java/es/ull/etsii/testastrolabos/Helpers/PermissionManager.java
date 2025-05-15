package es.ull.etsii.testastrolabos.Helpers;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import es.ull.etsii.testastrolabos.MainActivity;
import es.ull.etsii.testastrolabos.MainActivityViewManager;
import es.ull.etsii.testastrolabos.R;

import static android.content.Context.LOCATION_SERVICE;

public class PermissionManager {
    private final Activity mActivity;
    private final PermissionResultHandler mPermissionResultHandler;
    private final MainActivityViewManager mViewManager;

    public final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    public PermissionManager(MainActivity activity, @NonNull PermissionResultHandler permissionResultHandler, @NonNull MainActivityViewManager viewManager) {
        this.mActivity = activity;
        this.mPermissionResultHandler = permissionResultHandler;
        this.mViewManager = viewManager;
    }


    public boolean checkLocationPermission() {
        if (!isFineLocationPermissionGranted()) {
            requestLocationPermission();
            return false;
        }

        //TODO: create a function that guides the user to activate location
        if (!isLocationActivated()) {
            Toast.makeText(mActivity,
                    mActivity.getString(R.string.location_not_enabled_label),
                    Toast.LENGTH_LONG).show();
            mViewManager.locationNotEnabled();
            return false;
        }
        return true;
    }
    public boolean isLocationPermissionGranted() {
        return isFineLocationPermissionGranted() &&
                ActivityCompat.checkSelfPermission(
                        mActivity, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ;
    }

    private boolean isFineLocationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(
                mActivity, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
            mActivity.requestPermissions(
                    new String[]{ACCESS_FINE_LOCATION},
                    mPermissionResultHandler.PERMISSIONS_FINE_LOCATION
            );
    }

    /**
     * @return true if the location service is activated.
     * false if the location service is NOT activated.
     */
    private boolean isLocationActivated() {
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(LOCATION_SERVICE);
        return ((locationManager != null) && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)));
    }
}