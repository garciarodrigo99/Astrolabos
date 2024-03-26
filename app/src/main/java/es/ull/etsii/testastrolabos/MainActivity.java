package es.ull.etsii.testastrolabos;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.gms.location.*;
import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_READING_REQUEST_CODE = 1001;
    private static final int FAST_UPDATE = 1;
    private static final int DEFAULT_UPDATE = 30;

    // activity_main
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates;
    SwitchCompat sw_location_updates, sw_gps;

    // Location request for location settings
    LocationRequest appLocationRequest;
    LocationCallback locationCallBack;

    // Class to use location info
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Matching attributes with activity_main.xml
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        sw_location_updates = findViewById(R.id.sw_location_updates);
        sw_gps = findViewById(R.id.sw_gps);

        // As LocationRequest method setPriority is deprecated outside the constructor/builder,
        // two objects are created in this method to having them available to switch between each other
        // in sw_gps.setOnClickListener method.

        // Set properties of high accuracy request
        // TODO: Crear un abstract factory para encapsular estas asignaciones en otra clase.
        LocationRequest highAccuracyLR = new LocationRequest.Builder(1000 * FAST_UPDATE).
                setMinUpdateIntervalMillis(1000 * FAST_UPDATE).
                setPriority(Priority.PRIORITY_HIGH_ACCURACY).
                build();

        // Set properties of power balance request
        LocationRequest powerBalanceLR = new LocationRequest.Builder(1000 * DEFAULT_UPDATE).
                setMinUpdateIntervalMillis(1000 * DEFAULT_UPDATE).
                setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY).
                build();

        // By default, the object would be initialized with high accuracy
        appLocationRequest = highAccuracyLR;

        // event that is trigger each interval is met to know the location
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // save location
                //TODO:
                //updateUIValues(locationResult.getLastLocation());
            }
        };

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
