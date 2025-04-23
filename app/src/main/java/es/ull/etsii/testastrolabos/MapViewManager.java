package es.ull.etsii.testastrolabos;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import androidx.core.content.ContextCompat;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.datastore.MultiMapDataStore;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class MapViewManager {
    private final Activity mActivity;
    private final MapView mMapView;
    private final GraphicFactory mGraphicFactory;
    private final List<Layer> mLayers;
    private MultiMapDataStore mMultiMapDataStore;
    private TileRendererLayer mTileRendererLayer;
    private TileCache mTileCache;
    private boolean mHasToCenterMap = false;
    private Marker mUserMarker;
    private Polyline mTrackPathPolyline;
    private List<LatLong> mTrackPathPoints;
    private boolean mIsTracking = false;
    public MapViewManager(MainActivity activity, MapView mapView) {
        this.mActivity = activity;
        this.mMapView = mapView;
        this.mGraphicFactory = AndroidGraphicFactory.INSTANCE;
        this.mLayers = new ArrayList<>();
        try {
            mMultiMapDataStore = new MultiMapDataStore(MultiMapDataStore.DataPolicy.RETURN_ALL); // Check if problems
            mTileCache = AndroidUtil.createTileCache(
                    mActivity,
                    "multi_tilecache",
                    mapView.getModel().displayModel.getTileSize(),
                    1.0f,
                    mapView.getModel().frameBufferModel.getOverdrawFactor()
            );

            mMapView.getMapScaleBar().setVisible(true);
            mMapView.setBuiltInZoomControls(true);

            // Posición inicial ficticia
            LatLong initialPos = new LatLong(0, 0);
            Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_navigation);
            if (drawable == null) {
                Log.e("MapViewManager", "Drawable current location is null");
            }
            Bitmap originalLocationIcon = AndroidGraphicFactory.convertToBitmap(drawable);
            // Crear el marcador
            mUserMarker = new Marker(initialPos, originalLocationIcon, 0, 0);

            // Añadir directamente al LayerManager
            mapView.getLayerManager().getLayers().add(mUserMarker);

        } catch (Exception e) {
            Log.e("MapViewManager", "Error al inicializar el objeto", e);
        }
    }

    public void loadMap(Uri uri){
        Log.d("MapViewManager", "URI: " + uri.toString());
        try {
            FileInputStream fis = (FileInputStream) mActivity.getContentResolver().openInputStream(uri);

            MapDataStore newMap = new MapFile(fis);
            mMultiMapDataStore.addMapDataStore(newMap,false,false);
            if (mTileRendererLayer != null) {
                mMapView.getLayerManager().getLayers().remove(mTileRendererLayer);
            }
            mTileRendererLayer = new TileRendererLayer(
                    mTileCache,
                    mMultiMapDataStore,
                    mMapView.getModel().mapViewPosition,
                    mGraphicFactory);

            mTileRendererLayer.setXmlRenderTheme(MapsforgeThemes.DEFAULT);

            /*
             * On its own a tileRendererLayer does not know where to display the map, so we need to
             * associate it with our mapView.
             */
            mMapView.getLayerManager().getLayers().add(mTileRendererLayer);
            bringPaintingsToFront();
            Log.i("MapViewManager", "Capa añadida correctamente desde URI: " + uri.toString());

        } catch (Exception e) {
            Log.e("MapViewManager", "Error al añadir mapa desde URI", e);
        }
    }

    private void bringPaintingsToFront() {
        if (mUserMarker == null) {
            return;
        }
        mMapView.getLayerManager().getLayers().remove(mUserMarker,false);
        mMapView.getLayerManager().getLayers().add(mUserMarker);
    }

    public void paintIcons(){
        try {
            /*
             * The map also needs to know which area to display and at what zoom level.
             * Note: this map position is specific to El Teide area.
             */
            mMapView.setCenter(new LatLong(28.272440, -16.642372));
            mMapView.setZoomLevel((byte) 8);

            LatLong simulatedPosition = new LatLong(30.675, -14.602);

            Paint paintStroke = mGraphicFactory.createPaint();
            paintStroke.setColor(Color.BLACK);
            paintStroke.setStrokeWidth(5);
            paintStroke.setStyle(Style.STROKE);
            paintStroke.setDashPathEffect(new float[]{20, 20}); // Línea segmentada

            // Crear la lista de coordenadas
            List<LatLong> geoPoints = new ArrayList<>();
            geoPoints.add(simulatedPosition);
            geoPoints.add(new LatLong(28.4636, -16.2518)); // Santa Cruz de Tenerife

            // Crear la Polyline
            Polyline polyline = new Polyline(paintStroke, mGraphicFactory);
            polyline.setPoints(geoPoints);

            // Agregar la línea a las capas del mapa
            Layers layers = mMapView.getLayerManager().getLayers();
            layers.add(polyline);

            Paint paintStroke2 = mGraphicFactory.createPaint();
            paintStroke2.setColor(Color.RED);
            paintStroke2.setStrokeWidth(4);
            paintStroke2.setStyle(Style.STROKE);

            // Crear la lista de coordenadas
            List<LatLong> geoPoints2 = new ArrayList<>();
            geoPoints2.add(new LatLong(40.472222, -3.560833)); // Las Palmas de Gran Canaria
            geoPoints2.add(simulatedPosition); // TFS

            // Crear la Polyline
            Polyline polyline2 = new Polyline(paintStroke2, mGraphicFactory);
            polyline2.setPoints(geoPoints2);
            layers.add(polyline2);

            // Cargar el icono desde los recursos con ContextCompat
            Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_airplane);

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
        } catch (Exception e){
            Log.e("MapViewManager", "Error al iconos al mapa", e);
        }
    }

    private org.mapsforge.core.graphics.Bitmap getRotatedUserIcon(float angle) {
        // 1. Obtener el drawable normal
        Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_navigation);

        if (drawable == null) {
            Log.e("MapViewManager", "Drawable navigation es null");
            return null;
        }

        // 2. Convertir a android.graphics.Bitmap
        android.graphics.Bitmap androidBitmap = android.graphics.Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                android.graphics.Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(androidBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        // 3. Rotar el bitmap usando Matrix
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);  // 'bearing' es el rumbo en grados

        android.graphics.Bitmap rotatedAndroidBitmap = android.graphics.Bitmap.createBitmap(
                androidBitmap, 0, 0,
                androidBitmap.getWidth(),
                androidBitmap.getHeight(),
                matrix, true);

        // 4. Convertir de nuevo a org.mapsforge.core.graphics.Bitmap
        BitmapDrawable rotatedDrawable = new BitmapDrawable(mActivity.getResources(), rotatedAndroidBitmap);
        return AndroidGraphicFactory.convertToBitmap(rotatedDrawable);
    }

    public void updateLocation(Location location) {
        if (location == null){
            Log.d("MapViewManager", "Location is null");
            return;
        }
        Log.d("MapViewManager", location.getLatitude() + " " + location.getLongitude());
        if (mUserMarker == null){
            Log.d("MapViewManager", "User marker is null");
            return;
        }
        LatLong latLong = new LatLong(location.getLatitude(), location.getLongitude());
        mUserMarker.setLatLong(latLong);

        if(location.hasBearing()){
            float bearing = location.getBearing();
            org.mapsforge.core.graphics.Bitmap rotatedIcon = getRotatedUserIcon(bearing);
            if (rotatedIcon != null) {
                mUserMarker.setBitmap(rotatedIcon);
            }
        }

        if (mHasToCenterMap){
            mMapView.setCenter(latLong);
        }

        if(mIsTracking){
            mUserMarker.setLatLong(latLong);

            mTrackPathPoints.add(latLong);
            mTrackPathPolyline.setPoints(mTrackPathPoints);
        }
        mUserMarker.requestRedraw();
        mMapView.repaint();
        Log.d("MapViewManager", "Updated location icon in map: ");
    }

    public void setCenterMapOnLocation(boolean centerMapOnLocation) {
        this.mHasToCenterMap = centerMapOnLocation;
    }

    public void startTracking() {
        try {
            Paint paintStroke = mGraphicFactory.createPaint();
            paintStroke.setColor(Color.RED);
            paintStroke.setStrokeWidth(4);
            paintStroke.setStyle(Style.STROKE);
            mTrackPathPoints = new ArrayList<>();
            mTrackPathPolyline = new Polyline(paintStroke, mGraphicFactory);
            mTrackPathPolyline.setPoints(mTrackPathPoints);
            mMapView.getLayerManager().getLayers().add(mTrackPathPolyline);
            this.mIsTracking = true;
        } catch (Exception e){
            Log.e("MapViewManager", "Error starting tracking", e);
        }
    }
    public void stopTracking() {
        this.mIsTracking = false;
        mMapView.getLayerManager().getLayers().remove(mTrackPathPolyline);
        mTrackPathPolyline = null;
        mTrackPathPoints = null;
    }
}

