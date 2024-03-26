package es.ull.etsii.testastrolabos;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_READING_REQUEST_CODE = 1001;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates;
    SwitchCompat sw_location_updates, sw_gps;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        sw_location_updates = findViewById(R.id.sw_location_updates);
        sw_gps = findViewById(R.id.sw_gps);

        // Switches listeners
        sw_location_updates.setOnClickListener(v -> {
            if (sw_location_updates.isChecked()){
                sw_gps.setEnabled(true);
                tv_updates.setText(sw_location_updates.getTextOn());
            } else {
                sw_gps.setEnabled(false);
                tv_updates.setText(sw_location_updates.getTextOff());
            }
        });

        sw_gps.setOnClickListener(v -> {
            if (sw_gps.isChecked()){
                tv_sensor.setText(sw_gps.getTextOn());
            } else {
                tv_sensor.setText(sw_gps.getTextOff());
            }
        });

    }
}
