package es.ull.etsii.testastrolabos;

import android.widget.TextView;

public class LocationInfoPanel {
    private MainActivity mActivity;

    private TextView mLatitudeTv, mLongitudeTv, mAltitudeTv, mAccuracyTv, mSpeedTv, mBearingTv, mSatelliteTv, mLastTimeTv;

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
}

