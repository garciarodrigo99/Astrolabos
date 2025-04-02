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
import android.widget.*;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;
import es.ull.etsii.testastrolabos.Dialogs.LocationUpdatesSettingsDialog;
import es.ull.etsii.testastrolabos.Utils.FileUtils;
import es.ull.etsii.testastrolabos.Utils.PermissionUtils;
import org.jetbrains.annotations.NotNull;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_READING_REQUEST_CODE = 1001;
    private static final int FAST_UPDATE = 1;
    private static final int DEFAULT_UPDATE = 30;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private static final int SELECT_MAP_FILE = 0;
    private boolean isGPSInfoPanelVisible = false;
    private boolean isLocationUpdateEnabled = false;

    // activity_main
    Button  btn_startTracking;
    ImageButton ib_toggle_GPSInfoPanel, ib_location_updates_settings;

    AstrolabosLocationManager mLocationManager;

    FrameLayout fl_map;
    LinearLayout ll_gps_info_panel;

    MapView view_map;
    TrackingManager flightTrackManager;
    GPSInfoPanel gpsInfoPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidGraphicFactory.createInstance(getApplication());

        // Matching attributes with activity_main.xml
        ib_toggle_GPSInfoPanel = findViewById(R.id.ib_toggle_gps_info_panel);
        btn_startTracking = findViewById(R.id.btn_start_tracking);
        ib_location_updates_settings = findViewById(R.id.ib_location_update_settings);

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        flightTrackManager = new TrackingManager(this);

        ll_gps_info_panel = findViewById(R.id.ll_gps_info_panel);
        gpsInfoPanel = new GPSInfoPanel(this);

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

        checkPermissions();

        mLocationManager = AstrolabosLocationManager.getInstance(this);

        ib_toggle_GPSInfoPanel.setOnClickListener(v -> {
            if (isGPSInfoPanelVisible) {
                ll_gps_info_panel.setVisibility(View.GONE);
            } else {
                ll_gps_info_panel.setVisibility(View.VISIBLE);
            }
            isGPSInfoPanelVisible = !isGPSInfoPanelVisible;
        });

        btn_startTracking.setOnClickListener(v -> {
            if (flightTrackManager.getState() == TrackingManager.State.NOT_TRACKING) {
                flightTrackManager.startTracking();
                return;
            }
            flightTrackManager.showTrackingSettings();
        });

        btn_startTracking.setOnLongClickListener(v -> {
            if (flightTrackManager.getState() != TrackingManager.State.TRACKING) {
                return true;
            }
            TrackingActionDialog actionDialog = new TrackingActionDialog(flightTrackManager);
            actionDialog.show(getSupportFragmentManager(), "MiDialogo");
            return true;
        });

        ib_location_updates_settings.setOnClickListener(v -> {
            isLocationUpdateEnabled = !isLocationUpdateEnabled;
            if (isLocationUpdateEnabled){
                ib_location_updates_settings.setImageResource(R.drawable.location_on);
                mLocationManager.startLocationUpdates();
                updateLocation();
            } else {
                ib_location_updates_settings.setImageResource(R.drawable.location_off);
                mLocationManager.stopLocationUpdates();
                UIWriter.notTrackingLocation(this);
            }
        });

        ib_location_updates_settings.setOnLongClickListener(v -> {
            if (!isLocationUpdateEnabled) {
                return true;
            }
            LocationUpdatesSettingsDialog dialog = new LocationUpdatesSettingsDialog(this);
            dialog.show(getSupportFragmentManager(), "MiDialogo");
            return true;

        });
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
                    updateLocation();
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
        // TODO: refactor to switch
        if (requestCode == PermissionUtils.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                FileUtils.writeFileContent(this, uri, flightTrackManager.fileFormat.toString());
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

    private void updateLocation(){
        Log.d("MainActivity", "updateLocation");
        if(!checkPermissions()) return;
        mLocationManager.getLastKnownLocation();
    }

    /**
     * @return true if the location service is activated.
     * false if the location service is NOT activated.
     */
    public boolean isLocationActivated(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return ((locationManager != null) && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)));
    }

    private boolean checkPermissions() {
        // Permissions are not granted
        //TODO: Request for permissions
        if (!(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            // Request permissions if the OS version is supported
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
            return false;
        }
        // The location is not enabled
        //TODO: create a function that guides the user to activate location
        if (!(isLocationActivated())) {
            Toast.makeText(MainActivity.this,
                    getString(R.string.location_not_enabled_label),
                    Toast.LENGTH_LONG).
                    show();
            UIWriter.locationNotEnabled(MainActivity.this);
            return false;
        }
        return true;
    }

    //TODO: move to future permissions manager class
    public boolean hasLocationPermissions(){
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Method to aisle the cases when location is obtained.
     * It could be null or valid values
     */
    public void writeLocation(Location location){
        //TODO:Implementar método observador
        UIWriter.writeLocation(MainActivity.this,location);
        if (flightTrackManager.fileFormat == null) return;

        Date date = new Date();

        // Formatear la fecha y hora en el formato deseado
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String timestamp = sdf.format(date);
        flightTrackManager.fileFormat.addContent(timestamp,location);
    }
    private void openMap(Uri uri) {
        try {
            /*
             * We then make some simple adjustments, such as showing a scale bar and zoom controls.
             */
            view_map.getMapScaleBar().setVisible(true);
            view_map.setBuiltInZoomControls(true);

            /*
             * To avoid redrawing all the tiles all the time, we need to set up a tile cache with a
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
             * Note: this map position is specific to El Teide area.
             */
            view_map.setCenter(new LatLong(28.272440, -16.642372));
            view_map.setZoomLevel((byte) 8);

            LatLong simulatedPosition = new LatLong(30.675, -14.602);

            Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
            paintStroke.setColor(Color.BLACK);
            paintStroke.setStrokeWidth(5);
            paintStroke.setStyle(Style.STROKE);
            paintStroke.setDashPathEffect(new float[]{20, 20}); // Línea segmentada

            // Crear la lista de coordenadas
            List<LatLong> geoPoints = new ArrayList<>();
            geoPoints.add(simulatedPosition);
            geoPoints.add(new LatLong(28.4636, -16.2518)); // Santa Cruz de Tenerife

            // Crear la Polyline
            Polyline polyline = new Polyline(paintStroke, AndroidGraphicFactory.INSTANCE);
            polyline.setPoints(geoPoints);

            // Agregar la línea a las capas del mapa
            Layers layers = view_map.getLayerManager().getLayers();
            layers.add(polyline);

            Paint paintStroke2 = AndroidGraphicFactory.INSTANCE.createPaint();
            paintStroke2.setColor(Color.RED);
            paintStroke2.setStrokeWidth(4);
            paintStroke2.setStyle(Style.STROKE);

            // Crear la lista de coordenadas
            List<LatLong> geoPoints2 = new ArrayList<>();
            geoPoints2.add(new LatLong(40.472222, -3.560833)); // Las Palmas de Gran Canaria
            geoPoints2.add(simulatedPosition); // TFS

            // Crear la Polyline
            Polyline polyline2 = new Polyline(paintStroke2, AndroidGraphicFactory.INSTANCE);
            polyline2.setPoints(geoPoints2);
            layers.add(polyline2);

            // Cargar el icono desde los recursos con ContextCompat
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.airplane);

            // Verificar que el drawable no sea nulo
            if (drawable == null) {
                return;
            }

            // Convertir el Drawable a Bitmap
            Bitmap iconBitmap = AndroidGraphicFactory.convertToBitmap(drawable);
            // Crear el marcador
            Marker marker = new Marker(simulatedPosition, iconBitmap, 0, 0);

            // Agregar el marcador al mapa
            layers.add(marker);

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
