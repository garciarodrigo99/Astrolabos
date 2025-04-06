package es.ull.etsii.testastrolabos;

import android.view.View;

import android.widget.TextView;

public class GPSInfoPanel {
    private final View view_;

    private TextView mLatitudeTv, mLongitudeTv, mAltitudeTv, mAccuracyTv, mSpeedTv, mBearingTv, mSatelliteTv, mLastTimeTv;

    public GPSInfoPanel(MainActivity activity) {
        this.view_ = activity.ll_gps_info_panel;

        mLatitudeTv = this.view_.findViewById(R.id.tv_lat);
        mLongitudeTv = this.view_.findViewById(R.id.tv_lon);
        mAltitudeTv = this.view_.findViewById(R.id.tv_altitude);
        mAccuracyTv = this.view_.findViewById(R.id.tv_accuracy);
        mSpeedTv = this.view_.findViewById(R.id.tv_speed);
        mBearingTv = this.view_.findViewById(R.id.tv_bearing);
        mSatelliteTv = this.view_.findViewById(R.id.tv_satellites);
        mLastTimeTv = this.view_.findViewById(R.id.tv_last_time);
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

