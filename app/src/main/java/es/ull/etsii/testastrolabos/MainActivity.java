package es.ull.etsii.testastrolabos;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.ull.etsii.testastrolabos.Airport.AirportDAO;
import es.ull.etsii.testastrolabos.Airport.AirportDAOSQLite;
import org.jetbrains.annotations.NotNull;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private boolean isLocationUpdatesEnabled = false;

    private MainActivityViewManager mViewManager;
    private ActivityResultHandler mActivityResultHandler;
    private PermissionResultHandler mPermissionResultHandler;
    private PermissionManager mPermissionManager;

    AstrolabosLocationManager mLocationManager;

    FrameLayout fl_map;

    MapView view_map;
    TrackingManager mFlightTrackManager;
    private MapViewManager mMapViewManager;
    AirportDAO mAirportDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidGraphicFactory.createInstance(getApplication());

        mViewManager = new MainActivityViewManager(this);

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        mFlightTrackManager = new TrackingManager(this);

        // Referenciar el FrameLayout donde se agregará el MapView
        fl_map = findViewById(R.id.fl_map);

        // Inflar el layout con el MapView (map_view.xml)
        View mapViewLayout = inflater.inflate(R.layout.map_view, null);

        // Obtener la instancia del MapView
        view_map = mapViewLayout.findViewById(R.id.mapView);
        mMapViewManager = new MapViewManager(this, view_map);

        if(view_map != null){
            fl_map.addView(mapViewLayout);
            FileManager.openDocumentIntent(this);
        } else {
            System.out.println("MapView is null");
        }

        mActivityResultHandler = new ActivityResultHandler(this,mFlightTrackManager);
        mPermissionResultHandler = new PermissionResultHandler(this);
        mPermissionManager = new PermissionManager(this,mPermissionResultHandler,mViewManager);

        mPermissionManager.checkLocationPermission();

        mLocationManager = AstrolabosLocationManager.getInstance(this);
        mAirportDAO = new AirportDAOSQLite(this);
    }

    // Function to handle when certain permissions are granted or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionResultHandler.handlePermissionResult(requestCode, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mActivityResultHandler.handle(requestCode, resultCode, data);
    }

    public void updateLocation(){
        Log.d("MainActivity", "updateLocation");
        if(!mPermissionManager.checkLocationPermission()) return;
        mLocationManager.getLastKnownLocation();
    }

    //TODO: move to future permissions manager class
    public boolean hasLocationPermissions(){
        return mPermissionManager.isLocationPermissionGranted();
    }

    /**
     * Method to aisle the cases when location is obtained.
     * It could be null or valid values.
     * It is call, when enable location updates, from mLocationManager recurrently
     */
    public void writeLocation(Location location){
        //TODO:Implementar método observador

        // GPS INFO PANEL
        mViewManager.writeLocation(location);
        // MAP VIEW
        mMapViewManager.updateLocation(location);
        // FILE
        if (mFlightTrackManager.fileFormat == null) return;
        Date date = new Date();
        // Formatear la fecha y hora en el formato deseado
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String timestamp = sdf.format(date);
        mFlightTrackManager.fileFormat.addContent(timestamp,location);
    }
    public void loadMap(Uri uri) {
        mMapViewManager.loadMap(uri);
    }

    public void startTracking(){
        mFlightTrackManager.startTracking(trackSettings -> {
            mMapViewManager.startTracking(trackSettings);
        });
    }

    public void stopTracking(){
        mMapViewManager.stopTracking();
    }

    public void launchTrackingDialog(){
        mFlightTrackManager.showTrackingSettings();
    }

    public boolean isTracking(){
        return mFlightTrackManager.getState() == TrackingManager.State.TRACKING;
    }

    public void startLocationUpdates(){
        mLocationManager.startLocationUpdates();
    }

    public void stopLocationUpdates(){
        mLocationManager.stopLocationUpdates();
    }

    public boolean isLocationUpdatesEnabled() {
        return isLocationUpdatesEnabled;
    }

    public void setLocationUpdates(boolean enabled) {
        isLocationUpdatesEnabled = enabled;
    }

    public void onLocationPermissionDenied() {
        mViewManager.locationPermissionNotGranted();
    }

    public AirportDAO getAirportDAO() {
        return mAirportDAO;
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

    public void centerMapOnLocation(boolean hasToCenterMapOnPosition) {
        mMapViewManager.setCenterMapOnLocation(hasToCenterMapOnPosition);
    }
}
