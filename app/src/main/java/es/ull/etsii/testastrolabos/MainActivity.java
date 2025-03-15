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
import com.google.android.gms.tasks.OnSuccessListener;
import es.ull.etsii.testastrolabos.Utils.FileUtils;
import es.ull.etsii.testastrolabos.Utils.PermissionUtils;
import es.ull.etsii.testastrolabos.GPSInfoPanel;
import org.jetbrains.annotations.NotNull;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_READING_REQUEST_CODE = 1001;
    private static final int FAST_UPDATE = 1;
    private static final int DEFAULT_UPDATE = 30;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private static final int SELECT_MAP_FILE = 0;

    // activity_main
    TextView tv_sensor, tv_updates;
    SwitchCompat sw_location_updates, sw_gps;

    // Location request for location settings
    LocationRequest appLocationRequest;
    LocationCallback locationCallBack;

    // Class to use location info
    FusedLocationProviderClient fusedLocationProviderClient;

    FrameLayout fl_flight_track, fl_map, fl_gps_info_panel;
    View view_flight_track, view_gps_info_panel;

    MapView view_map;
    FlightTrackScreen flightTrackScreen;
    GPSInfoPanel gpsInfoPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidGraphicFactory.createInstance(getApplication());

        // Matching attributes with activity_main.xml
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        sw_location_updates = findViewById(R.id.sw_location_updates);
        sw_gps = findViewById(R.id.sw_gps);

        fl_flight_track = findViewById(R.id.fl_flight_track);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        view_flight_track = inflater.inflate(R.layout.flight_track_screen, null);
        flightTrackScreen = new FlightTrackScreen(this);
        fl_flight_track.addView(view_flight_track);

        fl_gps_info_panel = findViewById(R.id.fl_gps_info_panel);
        view_gps_info_panel = inflater.inflate(R.layout.gps_info_panel,null);
        gpsInfoPanel = new GPSInfoPanel(this);
        fl_flight_track.addView(view_gps_info_panel);

        // Referenciar el FrameLayout donde se agregará el MapView
        fl_map = findViewById(R.id.fl_map);

        // Inflar el layout con el MapView (map_view.xml)
        View mapViewLayout = inflater.inflate(R.layout.map_view, null);

        // Obtener la instancia del MapView
        view_map = mapViewLayout.findViewById(R.id.mapView);

        if(view_map != null){
            // Configurar el MapView
            Intent intent = new Intent(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, SELECT_MAP_FILE);

            fl_map.addView(mapViewLayout);
        } else {
            System.out.println("MapView is null");
        }


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
        if (requestCode == SELECT_MAP_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                openMap(uri);
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
    private void openMap(Uri uri) {
        try {
            /*
             * We then make some simple adjustments, such as showing a scale bar and zoom controls.
             */
            view_map.getMapScaleBar().setVisible(true);
            view_map.setBuiltInZoomControls(true);

            /*
             * To avoid redrawing all the tiles all the time, we need to set up a tile cache with an
             * utility method.
             */
            TileCache tileCache = AndroidUtil.createTileCache(this, "mapcache",
                    view_map.getModel().displayModel.getTileSize(), 1f,
                    view_map.getModel().frameBufferModel.getOverdrawFactor());

            /*
             * Now we need to set up the process of displaying a map. A map can have several layers,
             * stacked on top of each other. A layer can be a map or some visual elements, such as
             * markers. Here we only show a map based on a mapsforge map file. For this we need a
             * TileRendererLayer. A TileRendererLayer needs a TileCache to hold the generated map
             * tiles, a map file from which the tiles are generated and Rendertheme that defines the
             * appearance of the map.
             */
            FileInputStream fis = (FileInputStream) getContentResolver().openInputStream(uri);
            MapDataStore mapDataStore = new MapFile(fis);
            TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                    view_map.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
            tileRendererLayer.setXmlRenderTheme(MapsforgeThemes.MOTORIDER);

            /*
             * On its own a tileRendererLayer does not know where to display the map, so we need to
             * associate it with our mapView.
             */
            view_map.getLayerManager().getLayers().add(tileRendererLayer);

            /*
             * The map also needs to know which area to display and at what zoom level.
             * Note: this map position is specific to Berlin area.
             */
            view_map.setCenter(new LatLong(52.517037, 13.38886));
            view_map.setZoomLevel((byte) 12);
        } catch (Exception e) {
            /*
             * In case of map file errors avoid crash, but developers should handle these cases!
             */
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        /*
         * Whenever your activity exits, some cleanup operations have to be performed lest your app
         * runs out of memory.
         */
        view_map.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
    }
}
