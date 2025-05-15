package es.ull.etsii.testastrolabos;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.CheckResult;
import androidx.core.content.ContextCompat;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.reader.MapFile;

import java.io.FileInputStream;

public class MapViewManager {
    final Activity mActivity;
    final MapView mMapView;
    private MapViewRenderer mMapViewRenderer;
    private boolean mHasToCenterMap = false;
    Marker mUserMarker;
    private boolean mIsTracking = false;
    private TrackingViewManager mTrackingViewManager = null;
    public MapViewManager(MainActivity activity, MapView mapView) {
        this.mActivity = activity;
        this.mMapView = mapView;
        try {
            mMapViewRenderer = new MapViewRenderer(this);

            mMapView.getMapScaleBar().setVisible(true);
            mMapView.setBuiltInZoomControls(true);

            LatLong initialPos = new LatLong(0, 0);
            mUserMarker = insertIconInMap(initialPos,R.drawable.ic_navigation);
        } catch (Exception e) {
            Log.e("MapViewManager", "Error creating map view manager", e);
        }
    }

    @CheckResult
    Marker insertIconInMap(LatLong latLong, int id) {
        Marker marker = null;
        try {
            Drawable drawable = ContextCompat.getDrawable(mActivity, id);
            if (drawable == null) {
                Log.e("MapViewManager", "Drawable icon is null");
                return marker;
            }
            Bitmap originLocationIcon = AndroidGraphicFactory.convertToBitmap(drawable);
            marker = new Marker(latLong, originLocationIcon, 0, 0);
            mMapView.getLayerManager().getLayers().add(marker);
        } catch (Exception e){
            Log.e("MapViewManager", "Error trying to insert icon into Map View", e);
        }
        return marker;
    }

    public GraphicFactory getGraphicFactory() {
        return mMapViewRenderer.getGraphicFactory();
    }

    public void loadMap(Uri uri){
        Log.d("MapViewManager", "URI: " + uri.toString());
        try {
            FileInputStream fis = (FileInputStream) mActivity.getContentResolver().openInputStream(uri);

            MapDataStore newMap = new MapFile(fis);
            mMapViewRenderer.getMultiMapDataStore().addMapDataStore(newMap,false,false);
            if (mMapViewRenderer.getTileRendererLayer() != null) {
                mMapView.getLayerManager().getLayers().remove(mMapViewRenderer.getTileRendererLayer());
            }
            mMapViewRenderer.initTileRendererLayer();

            /*
             * On its own a tileRendererLayer does not know where to display the map, so we need to
             * associate it with our mapView.
             */
            mMapView.getLayerManager().getLayers().add(mMapViewRenderer.getTileRendererLayer());
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
            mTrackingViewManager = new AirTrackingManager(
                    this,
                    trackSettings.getOriginAirport(),
                    trackSettings.getDestinationAirport());

        } else {
            mTrackingViewManager = new TrackingViewManager(this);
        }
        mTrackingViewManager.startTracking();
    }

    public void stopTracking() {
        mTrackingViewManager.stopTracking();
        mTrackingViewManager = null;
        this.mIsTracking = false;
        mMapView.getLayerManager().getLayers().remove(mUserMarker);
        mUserMarker = insertIconInMap(mUserMarker.getLatLong(),R.drawable.ic_navigation);
    }
}

