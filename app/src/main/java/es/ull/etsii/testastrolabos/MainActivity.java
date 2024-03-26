package es.ull.etsii.testastrolabos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnSuccessListener;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_READING_REQUEST_CODE = 1001;
    private static final int FAST_UPDATE = 1;
    private static final int DEFAULT_UPDATE = 30;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

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

        /* As LocationRequest method setPriority is deprecated outside the constructor/builder,
        * two objects are created in this method to having them available to switch between each other
        * in sw_gps.setOnClickListener method.*/
        // Set properties of high accuracy request
        LocationRequest highAccuracyLR = LocationAF.getFastUpdateLocationRequest();

        // Set properties of power balance request
        LocationRequest powerBalanceLR = LocationAF.getPowerBalanceLocationRequest();

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
                appLocationRequest = highAccuracyLR;
            } else {
                tv_sensor.setText(sw_gps.getTextOff());
                appLocationRequest = powerBalanceLR;
            }
        });
        updateGPS();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //TODO: manejar cuando hay permisos de localización, pero la localización está desactivada
        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this,
                            getString(R.string.location_permissions_granted),
                            Toast.LENGTH_SHORT).
                            show();
                    updateGPS();
                    //checkLocationEnabled();
                }
                else {
                    Toast.makeText(MainActivity.this,
                                    getString(R.string.location_permissions_not_granted),
                            Toast.LENGTH_LONG).
                            show();
                    //UIWriter.locationPermissionNotGranted(MainActivity.this);
                    //finish();
                }
                break;
        }
    }
    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        // Permissions are not granted
        if (!(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            // Request permissions if the OS version is supported
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
            return;
        }
        // The location is not enabled
/*        if (!(isLocationEnable())) {
            UIWriter.locationNotEnabled(MainActivity.this);
            return;
        }*/
        // Location permissions is granted and location is enabled.
        //Toast.makeText(MainActivity.this,"Location permissions granted",Toast.LENGTH_LONG).show();
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            // We got permissions. Put the values in the GUI
            Toast.makeText(MainActivity.this,
                    getString(R.string.location_updated),
                    Toast.LENGTH_SHORT).
                    show();

/*               updateUIValues(location);
            // Tracking está activado
            if (fileWriter != null) {
                Date date = new Date();

                // Formatear la fecha y hora en el formato deseado
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String timestamp = sdf.format(date);
                fileWriter.addContent(timestamp,location);
            }*/
        });
    }
}
