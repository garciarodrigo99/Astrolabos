package es.ull.etsii.testastrolabos;

import android.location.GnssStatus;
import android.location.Location;
import android.view.View;
import android.widget.*;
import es.ull.etsii.testastrolabos.Model.AstrolabosLocationModel;
import es.ull.etsii.testastrolabos.Model.TrackSettings;
import es.ull.etsii.testastrolabos.Dialogs.LocationUpdatesSettingsDialog;
import es.ull.etsii.testastrolabos.Dialogs.TrackingActionDialog;
import es.ull.etsii.testastrolabos.Panels.LocationInfoPanel;
import es.ull.etsii.testastrolabos.Services.FileManager;

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
        mLocationInfoPanel.closeWarning();
        mLocationInfoPanel.updateLocation(location);
    }

    public void startTracking(TrackSettings trackSettings){
        if ((trackSettings == null) || (trackSettings.isFreeTracking())){
            return;
        }
        mLocationInfoPanel.startAirportTracking(trackSettings.getOriginAirport(),
                trackSettings.getDestinationAirport());
    }

    public void stopTracking(){
        mLocationInfoPanel.stopAirportTracking();
    }

    /**
     * When the location callback is null, a warning string is set in the
     * warning text view to indicate location update status.
     * @param warning String with the warning to show in warning text view.
     */
    private void setWarningTextUIValues(String warning){
        mLocationInfoPanel.setWarning(warning);
        mLocationInfoPanel.setLatitude(AstrolabosLocationModel.UNAVAILABLE_DATA);
        mLocationInfoPanel.setLongitude(AstrolabosLocationModel.UNAVAILABLE_DATA);
        mLocationInfoPanel.setBearing(AstrolabosLocationModel.UNAVAILABLE_DATA);
        mLocationInfoPanel.setAltitude(AstrolabosLocationModel.UNAVAILABLE_DATA);
        mLocationInfoPanel.setSpeed(AstrolabosLocationModel.UNAVAILABLE_DATA);
        mLocationInfoPanel.setLastTime(AstrolabosLocationModel.UNAVAILABLE_DATA);
        mLocationInfoPanel.setAccuracy(AstrolabosLocationModel.UNAVAILABLE_DATA);
    }

    public void writeGnssStatus(GnssStatus gnssStatus) {
        mLocationInfoPanel.updateGnssStatus(gnssStatus);
    }
}
