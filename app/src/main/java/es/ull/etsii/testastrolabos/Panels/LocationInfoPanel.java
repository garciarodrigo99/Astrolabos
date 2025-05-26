package es.ull.etsii.testastrolabos.Panels;

import android.location.GnssStatus;
import android.location.Location;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import es.ull.etsii.testastrolabos.Data.Airport;
import es.ull.etsii.testastrolabos.MainActivity;
import es.ull.etsii.testastrolabos.Model.AstrolabosLocationModel;
import es.ull.etsii.testastrolabos.R;
import org.mapsforge.core.model.LatLong;

public class LocationInfoPanel {
    private MainActivity mActivity;

    private TextView mLatitudeTv, mLongitudeTv, mAltitudeTv, mAccuracyTv,
            mSpeedTv, mBearingTv, mSatelliteTv, mLastTimeTv, mWarningTv;

    private FlightInfoPanel mFlightInfoPanel;
    private final LinearLayout mWarningLayout,mFlightInfoPanelLayout;
    private AstrolabosLocationModel mAstrolabosLocationModel;
    public LocationInfoPanel(MainActivity activity) {
        this.mActivity = activity;

        mLatitudeTv = this.mActivity.findViewById(R.id.tv_lat);
        mLongitudeTv = this.mActivity.findViewById(R.id.tv_lon);
        mAltitudeTv = this.mActivity.findViewById(R.id.tv_altitude);
        mAccuracyTv = this.mActivity.findViewById(R.id.tv_accuracy);
        mSpeedTv = this.mActivity.findViewById(R.id.tv_speed);
        mBearingTv = this.mActivity.findViewById(R.id.tv_bearing);
        mSatelliteTv = this.mActivity.findViewById(R.id.tv_satellites);
        mLastTimeTv = this.mActivity.findViewById(R.id.tv_last_time);
        mWarningLayout = this.mActivity.findViewById(R.id.ll_warning);
        mWarningTv = this.mActivity.findViewById(R.id.tv_warning);
        this.mFlightInfoPanelLayout = this.mActivity.findViewById(R.id.ll_flight_info_panel);
        mAstrolabosLocationModel = new AstrolabosLocationModel();
    }

    public void updateLocation(Location location) {
        mAstrolabosLocationModel.updateLocation(location);
        setLatitude(String.valueOf(location.getLatitude()));
        setLongitude(String.valueOf(location.getLongitude()));
        updateFlightInfoPanel(mAstrolabosLocationModel.getPosition());
        setAltitude(mAstrolabosLocationModel.getAltitude().toString());
        setBearing(mAstrolabosLocationModel.getBearing().toString());
        setSpeed(mAstrolabosLocationModel.getSpeed().toString());
        setLastTime(mAstrolabosLocationModel.DATE_FORMAT.format(mAstrolabosLocationModel.getLastTime()));
        setAccuracy(String.valueOf(mAstrolabosLocationModel.getAccuracy()));
    }
    public void updateGnssStatus(GnssStatus gnssStatus) {
        mAstrolabosLocationModel.getSatellites().updateStatus(gnssStatus);
        setSatellite(mAstrolabosLocationModel.getSatellites().toString());
    }
    public void setLatitude(String latitude) {
        this.mLatitudeTv.setText(latitude);
    }

    public void setLongitude(String longitude) {
        this.mLongitudeTv.setText(longitude);
    }

    public void setAltitude(String altitude) {
        this.mAltitudeTv.setText(altitude);
    }

    public void setAccuracy(String accuracy) {
        this.mAccuracyTv.setText(accuracy);
    }

    public void setSpeed(String speed) {
        this.mSpeedTv.setText(speed);
    }

    public void setBearing(String bearing) {
        this.mBearingTv.setText(bearing);
    }

    public void setSatellite(String satellites) {
        this.mSatelliteTv.setText(satellites);
    }
    public void setLastTime(String lastTime) {
        this.mLastTimeTv.setText(lastTime);
    }

    public void startAirportTracking(Airport origin, Airport destination) {
        this.mFlightInfoPanel = new FlightInfoPanel(this.mActivity, origin, destination);
        mFlightInfoPanelLayout.setVisibility(View.VISIBLE);
    }

    public void stopAirportTracking() {
        this.mFlightInfoPanel = null;
        mFlightInfoPanelLayout.setVisibility(View.GONE);
    }

    public void updateFlightInfoPanel(LatLong latLong) {
        if (this.mFlightInfoPanel != null) {
            mFlightInfoPanel.updateLocation(latLong);
        }
    }

    public void setWarning(String warning) {
        this.mWarningLayout.setVisibility(View.VISIBLE);
        this.mWarningTv.setText(warning);
    }

    public void closeWarning() {
        this.mWarningLayout.setVisibility(View.GONE);
        this.mWarningTv.setText("");
    }
}

