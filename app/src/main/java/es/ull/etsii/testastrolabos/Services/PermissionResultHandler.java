package es.ull.etsii.testastrolabos.Services;

import android.app.Activity;
import android.widget.Toast;
import android.content.pm.PackageManager;
import es.ull.etsii.testastrolabos.MainActivity;
import es.ull.etsii.testastrolabos.R;

public class PermissionResultHandler {
    public final int PERMISSIONS_FINE_LOCATION = 99;
    private final Activity mActivity;

    public PermissionResultHandler(MainActivity activity) {
        this.mActivity = activity;
    }

    public void handlePermissionResult(int requestCode, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                handleLocationPermissionResult(grantResults);
                break;
            default:
                break;
        }
    }

    private void handleLocationPermissionResult(int[] grantResults) {
        //TODO: manejar cuando hay permisos de localización, pero la localización está desactivada
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onLocationPermissionGranted();
        } else {
            onLocationPermissionDenied();
        }
    }

    private void onLocationPermissionGranted() {
        Toast.makeText(mActivity, mActivity.getString(R.string.location_permissions_granted), Toast.LENGTH_SHORT).show();

        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).updateLocation();
        }
    }

    private void onLocationPermissionDenied() {
        Toast.makeText(mActivity, mActivity.getString(R.string.location_permissions_not_granted), Toast.LENGTH_LONG).show();
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).onLocationPermissionDenied();
        }
        // mActivity.finish(); // si lo necesitas
    }
}