package es.ull.etsii.testastrolabos;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import android.widget.TextView;

public class GPSInfoPanel {
    private final View view_;

    private TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed;

    public GPSInfoPanel(MainActivity activity) {
        this.view_ = activity.ll_gps_info_panel;

        tv_lat = this.view_.findViewById(R.id.tv_lat);
        tv_lon = this.view_.findViewById(R.id.tv_lon);
        tv_altitude = this.view_.findViewById(R.id.tv_altitude);
        tv_accuracy = this.view_.findViewById(R.id.tv_accuracy);
        tv_speed = this.view_.findViewById(R.id.tv_speed);
    }

    public void setLatitude(String latitude) {
        this.tv_lat.setText(latitude);
    }

    public void setLongitude(String longitude) {
        this.tv_lon.setText(longitude);
    }

    public void setAltitude(String altitude) {
        this.tv_altitude.setText(altitude);
    }

    public void setAccuracy(String accuracy) {
        this.tv_accuracy.setText(accuracy);
    }

    public void setSpeed(String speed) {
        this.tv_speed.setText(speed);
    }
}

