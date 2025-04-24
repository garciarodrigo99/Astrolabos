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
import es.ull.etsii.testastrolabos.Airport.Airport;
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
    final Activity mActivity;
    final MapView mMapView;
    final GraphicFactory mGraphicFactory;
    final List<Layer> mLayers;
    MultiMapDataStore mMultiMapDataStore;
    TileRendererLayer mTileRendererLayer;
    TileCache mTileCache;
    private boolean mHasToCenterMap = false;
    Marker mUserMarker;
    private boolean mIsTracking = false;
    private TrackingViewManager mTrackingViewManager = null;
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
            mMapView.getLayerManager().getLayers().add(mUserMarker);

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

        if(mTrackingViewManager != null){
            mTrackingViewManager.updateLocation(latLong);
        }
        mUserMarker.requestRedraw();
        mMapView.repaint();
        Log.d("MapViewManager", "Updated location icon in map: ");
    }

    public void setCenterMapOnLocation(boolean centerMapOnLocation) {
        this.mHasToCenterMap = centerMapOnLocation;
    }

    public void startTracking(TrackSettings trackSettings) {
        if (!trackSettings.isFreeTracking()){
            mTrackingViewManager = new AirTrackingView(
                    this,
                    trackSettings.getOriginAirport(),
                    trackSettings.getDestinationAirport());
        } else {
            mTrackingViewManager = new FreeTrackingView(this);
        }
        mTrackingViewManager.startTracking();
    }

    public void stopTracking() {
        mTrackingViewManager.stopTracking();
        mTrackingViewManager = null;
        this.mIsTracking = false;
    }
}

