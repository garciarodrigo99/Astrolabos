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
    private boolean mHasToCenterMapOnPosition = false;
    private final MainActivity mActivity;

    // activity_main
    private ImageButton ib_toggle_GPSInfoPanel, ib_location_updates_settings,
            ib_startTracking, ib_addLayer,ib_center_on_location;
    LinearLayout ll_gps_info_panel;
    private final LocationInfoPanel mLocationInfoPanel;

    public MainActivityViewManager(MainActivity activity) {
        this.mActivity = activity;
        initializeButtons();
        mLocationInfoPanel = new LocationInfoPanel(mActivity);
        setupButtonsListeners();
    }

    private void initializeButtons() {
        ib_toggle_GPSInfoPanel = mActivity.findViewById(R.id.ib_toggle_gps_info_panel);
        ib_startTracking = mActivity.findViewById(R.id.ib_start_tracking);
        ib_location_updates_settings = mActivity.findViewById(R.id.ib_location_update_settings);
        ib_addLayer = mActivity.findViewById(R.id.ib_add_layer);
        ib_center_on_location = mActivity.findViewById(R.id.ib_center_on_location);
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
        ib_addLayer.setOnClickListener(v -> addLayer());
        ib_center_on_location.setOnClickListener(v -> onCenterOnLocationPressed());
    }

    private void onCenterOnLocationPressed() {
        mHasToCenterMapOnPosition = !mHasToCenterMapOnPosition;
        if (mHasToCenterMapOnPosition){
            ib_center_on_location.setImageResource(R.drawable.ic_center_map_in_location_on);
        } else {
            ib_center_on_location.setImageResource(R.drawable.ic_center_map_in_location_off);
        }
        mActivity.centerMapOnLocation(mHasToCenterMapOnPosition);
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
            ib_location_updates_settings.setImageResource(R.drawable.ic_location_on);
            mActivity.startLocationUpdates();
            mActivity.updateLocation();
        } else {
            ib_location_updates_settings.setImageResource(R.drawable.ic_location_off);
            mActivity.stopLocationUpdates();
            notTrackingLocation();
        }
    }

    private void toggleGPSInfoPanel() {
        mIsGPSInfoPanelVisible = !mIsGPSInfoPanelVisible;
        if (mIsGPSInfoPanelVisible) {
            ll_gps_info_panel.setVisibility(View.VISIBLE);
        } else {
            ll_gps_info_panel.setVisibility(View.GONE);
        }
    }

    private void addLayer() {
        FileManager.openDocumentIntent(mActivity);
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
        mLocationInfoPanel.setLatitude(String.valueOf(location.getLatitude()));
        mLocationInfoPanel.setLongitude(String.valueOf(location.getLongitude()));
        mLocationInfoPanel.setAccuracy(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()){
            mLocationInfoPanel.setAltitude(String.valueOf(location.getAltitude()));
        } else {
            mLocationInfoPanel.setAltitude(mActivity.getString(R.string.not_available_message));
        }
        if (location.hasSpeed()){
            mLocationInfoPanel.setSpeed(speedFormat(location.getSpeed()));
        } else {
            mLocationInfoPanel.setSpeed(mActivity.getString(R.string.not_available_message));
        }
        if (location.hasBearing()){
            mLocationInfoPanel.setBearing(String.valueOf(location.getBearing()));
        } else {
            mLocationInfoPanel.setBearing(mActivity.getString(R.string.not_available_message));
        }
        long timeInMillis = location.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedTime = sdf.format(new Date(timeInMillis));
        mLocationInfoPanel.setLastTime(formattedTime);
    }

    /**
     * When some the location information can not be shown in each element of the GUI, a string is set in all of those
     * elements of the GUI to help the user what is going on with location.
     * @param warning String with the warning to show in each element of the GUI
     */
    private void setWarningTextUIValues(String warning){
        mLocationInfoPanel.setLatitude(warning);
        mLocationInfoPanel.setLongitude(warning);
        mLocationInfoPanel.setAccuracy(warning);
        mLocationInfoPanel.setAltitude(warning);
        mLocationInfoPanel.setSpeed(warning);
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
