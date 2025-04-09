package es.ull.etsii.testastrolabos;

import android.location.Location;
import android.view.View;
import android.widget.*;
import es.ull.etsii.testastrolabos.Dialogs.LocationUpdatesSettingsDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivityViewManager {

    private boolean mIsGPSInfoPanelVisible = false;
    final private MainActivity mActivity;

    // activity_main
    private ImageButton ib_toggle_GPSInfoPanel, ib_location_updates_settings, ib_startTracking;
    LinearLayout ll_gps_info_panel;
    final private GPSInfoPanel mGpsInfoPanel;

    public MainActivityViewManager(MainActivity activity) {
        this.mActivity = activity;
        initializeButtons();
        mGpsInfoPanel = new GPSInfoPanel(mActivity);
        setupButtonsListeners();
    }

    private void initializeButtons() {
        ib_toggle_GPSInfoPanel = mActivity.findViewById(R.id.ib_toggle_gps_info_panel);
        ib_startTracking = mActivity.findViewById(R.id.ib_start_tracking);
        ib_location_updates_settings = mActivity.findViewById(R.id.ib_location_update_settings);
        ll_gps_info_panel = mActivity.findViewById(R.id.ll_gps_info_panel);
    }

    private void setupButtonsListeners() {
        ib_toggle_GPSInfoPanel.setOnClickListener(v -> toggleGPSInfoPanel());

        ib_startTracking.setOnClickListener(v -> {
            if (!mActivity.isTracking()) {
                mActivity.startTracking();
                return;
            }
            mActivity.launchTrackingDialog();
        });

        ib_startTracking.setOnLongClickListener(v ->
                showTrackingActionDialog());

        ib_location_updates_settings.setOnClickListener(v ->
                defineLocationUpdates());

        ib_location_updates_settings.setOnLongClickListener(v ->
                showLocationUpdatesSettingsDialog());
    }

    private boolean showLocationUpdatesSettingsDialog() {
        if (mActivity.isLocationUpdatesEnabled()) {
            LocationUpdatesSettingsDialog dialog = new LocationUpdatesSettingsDialog(mActivity);
            dialog.show(mActivity.getSupportFragmentManager(), "MiDialogo");
        }
        return true;
    }

    private boolean showTrackingActionDialog() {
        if (mActivity.isTracking()) {
            TrackingActionDialog actionDialog = new TrackingActionDialog(mActivity.mFlightTrackManager);
            actionDialog.show(mActivity.getSupportFragmentManager(), "MiDialogo");
        }
        return true;
    }

    private void defineLocationUpdates() {
        mActivity.setLocationUpdates(!mActivity.isLocationUpdatesEnabled());
        if (mActivity.isLocationUpdatesEnabled()){
            ib_location_updates_settings.setImageResource(R.drawable.location_on);
            mActivity.startLocationUpdates();
            mActivity.updateLocation();
        } else {
            ib_location_updates_settings.setImageResource(R.drawable.location_off);
            mActivity.stopLocationUpdates();
            notTrackingLocation();
        }
    }

    private void toggleGPSInfoPanel() {
        if (mIsGPSInfoPanelVisible) {
            ll_gps_info_panel.setVisibility(View.GONE);
        } else {
            ll_gps_info_panel.setVisibility(View.VISIBLE);
        }
        mIsGPSInfoPanelVisible = !mIsGPSInfoPanelVisible;
    }

    public void locationPermissionNotGranted(){
        String warning = mActivity.getString(R.string.location_permissions_not_available);
        setWarningTextUIValues(warning);
        ib_location_updates_settings.setEnabled(false);
    }
    public void locationNotEnabled(){
        String warning = mActivity.getString(R.string.location_not_enabled_label);
        setWarningTextUIValues(warning);
        ib_location_updates_settings.setEnabled(false);
    }
    public void notTrackingLocation(){
        String warning = mActivity.getString(R.string.not_tracking_location);
        setWarningTextUIValues(warning);
    }
    public void locationNull(){
        String warning = mActivity.getString(R.string.location_is_null);
        setWarningTextUIValues(warning);
    }
    public void writeLocation(Location location){
        // If the GPS cannot set a valid location, the location value will be null.
        // It must be handled this case due to nullPointerException that causes the app ends with no explanation to the user.
        if (location == null){
            // Location is null, maybe GPS sensors are not sending location information yet.
            locationNull();
            return;
        }
        mGpsInfoPanel.setLatitude(String.valueOf(location.getLatitude()));
        mGpsInfoPanel.setLongitude(String.valueOf(location.getLongitude()));
        mGpsInfoPanel.setAccuracy(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()){
            mGpsInfoPanel.setAltitude(String.valueOf(location.getAltitude()));
        } else {
            mGpsInfoPanel.setAltitude(mActivity.getString(R.string.not_available_message));
        }
        if (location.hasSpeed()){
            mGpsInfoPanel.setSpeed(speedFormat(location.getSpeed()));
        } else {
            mGpsInfoPanel.setSpeed(mActivity.getString(R.string.not_available_message));
        }
        if (location.hasBearing()){
            mGpsInfoPanel.setBearing(String.valueOf(location.getBearing()));
        } else {
            mGpsInfoPanel.setBearing(mActivity.getString(R.string.not_available_message));
        }
        long timeInMillis = location.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedTime = sdf.format(new Date(timeInMillis));
        mGpsInfoPanel.setLastTime(formattedTime);
    }

    /**
     * When some the location information can not be shown in each element of the GUI, a string is set in all of those
     * elements of the GUI to help the user what is going on with location.
     * @param warning String with the warning to show in each element of the GUI
     */
    private void setWarningTextUIValues(String warning){
        mGpsInfoPanel.setLatitude(warning);
        mGpsInfoPanel.setLongitude(warning);
        mGpsInfoPanel.setAccuracy(warning);
        mGpsInfoPanel.setAltitude(warning);
        mGpsInfoPanel.setSpeed(warning);
    }

    private String speedFormat(double speed){
        speed *= 3.6;
        Double formatedDouble = speed;
        if (speed >= 10.0){
            return Integer.toString(formatedDouble.intValue());
        }
        return new DecimalFormat(speed < 5.0 ? "#.##" : "#.#").format(formatedDouble);
    }
}
