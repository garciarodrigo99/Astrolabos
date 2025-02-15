package es.ull.etsii.testastrolabos;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;
import es.ull.etsii.testastrolabos.Utils.FileUtils;
import es.ull.etsii.testastrolabos.Utils.PermissionUtils;
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

    FrameLayout fl_flight_track;
    View view_flight_track;
    FlightTrackScreen flightTrackScreen;

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

        fl_flight_track = findViewById(R.id.fl_flight_track);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        view_flight_track = inflater.inflate(R.layout.flight_track_screen, null);
        flightTrackScreen = new FlightTrackScreen(this);
        fl_flight_track.addView(view_flight_track);

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
            // At this point location permissions are granted and location services activated
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // save location
                locationObtainedHandler(locationResult.getLastLocation());
            }
        };

        // Switches listeners
        sw_location_updates.setOnClickListener(v -> {
            boolean thisSwitchIsChecked = sw_location_updates.isChecked();
            sw_gps.setEnabled(thisSwitchIsChecked);
            if (thisSwitchIsChecked){
                UIWriter.whenEnableSwitch(sw_gps,tv_sensor);
                tv_updates.setText(sw_location_updates.getTextOn());
                startLocationUpdates();
            } else {
                stopLocationUpdates();
            }
        });

        sw_gps.setOnClickListener(v -> {
            if (sw_gps.isChecked()){
                appLocationRequest = highAccuracyLR;
                tv_sensor.setText(sw_gps.getTextOn());
            } else {
                appLocationRequest = powerBalanceLR;
                tv_sensor.setText(sw_gps.getTextOff());
            }
        });

        //TODO: Resolve bug: why the application fails if updateGPS is not called onCreate method.
        updateGPS();
    }

    // Function to handle when certain permissions are granted or not
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
                    UIWriter.locationPermissionNotGranted(MainActivity.this);
                    //finish();
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionUtils.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                FileUtils.writeFileContent(this, uri, "Contenido del archivo");
                Toast.makeText(this, "Archivo guardado con éxito", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @return true if the location service is activated.
     * false if the location service is NOT activated.
     */
    public boolean isLocationActivated(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return ((locationManager != null) && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)));
    }

    /**
     * Method that activate the trigger function locationCallBack with the LocationRequest settings
     * @see  com.google.android.gms.location.LocationCallback
     */
    private void startLocationUpdates() {
        tv_updates.setText(getString(R.string.sw_locations_updates_textOn));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(appLocationRequest, locationCallBack, null);
        updateGPS();
    }

    /**
     * Method that stop the trigger function locationCallBack
     * @see  com.google.android.gms.location.LocationCallback
     */
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
        UIWriter.notTrackingLocation(MainActivity.this);
    }

    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        // Permissions are not granted
        //TODO: Request for permissions
        if (!(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            // Request permissions if the OS version is supported
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
            return;
        }
        // The location is not enabled
        //TODO: create a function that guides the user to activate location
        if (!(isLocationActivated())) {
            Toast.makeText(MainActivity.this,
                    getString(R.string.location_not_enabled_label),
                    Toast.LENGTH_LONG).
                    show();
            UIWriter.locationNotEnabled(MainActivity.this);
            return;
        }
        // Location permissions is granted and location is enabled.
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            // We got permissions. Put the values in the GUI
            Toast.makeText(MainActivity.this,
                    getString(R.string.location_updated),
                    Toast.LENGTH_SHORT).
                    show();

              locationObtainedHandler(location);
        });
    }

    /**
     * Method to aisle the cases when location is obtained.
     * It could be null or valid values
     */
    private void locationObtainedHandler(Location location){
        // Write location values
        //TODO:Implementar método observador
        UIWriter.writeLocation(MainActivity.this,location);
        if (flightTrackScreen.fileFormat != null) {
            Date date = new Date();

            // Formatear la fecha y hora en el formato deseado
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String timestamp = sdf.format(date);
            flightTrackScreen.fileFormat.addContent(timestamp,location);
        }
    }
}
