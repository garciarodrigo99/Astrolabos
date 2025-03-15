package es.ull.etsii.testastrolabos;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import es.ull.etsii.testastrolabos.Dialogs.AcceptCancelActions;
import es.ull.etsii.testastrolabos.Dialogs.Dialogs;
import es.ull.etsii.testastrolabos.Utils.PermissionUtils;
import es.ull.etsii.testastrolabos.Writers.FileFormat;
import es.ull.etsii.testastrolabos.Writers.JsonFormat;

public class GPSInfoPanel {

    private Context context;
    private final View view;
    private Activity activity;

    private TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed;

    FileFormat fileFormat;

    public GPSInfoPanel(MainActivity activity) {
        this.activity = activity;
        this.view = activity.view_gps_info_panel;
        this.context = view.getContext();

        tv_lat = this.view.findViewById(R.id.tv_lat);
        tv_lon = this.view.findViewById(R.id.tv_lon);
        tv_altitude = this.view.findViewById(R.id.tv_altitude);
        tv_accuracy = this.view.findViewById(R.id.tv_accuracy);
        tv_speed = this.view.findViewById(R.id.tv_speed);
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

