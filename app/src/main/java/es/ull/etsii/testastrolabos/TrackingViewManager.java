package es.ull.etsii.testastrolabos;

import android.graphics.drawable.Drawable;
import android.util.Log;
import androidx.core.content.ContextCompat;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public abstract class TrackingViewManager {
    protected boolean mIsTracking = false;
    protected MapViewManager mMapViewManager;
    protected TrackSettings mTrackSettings;

    protected Polyline mTrackPathPolyline;
    protected List<LatLong> mTrackPathPoints;

    public TrackingViewManager(MapViewManager mapViewManager) {
        this.mMapViewManager = mapViewManager;
    }
    public void setEnableTracking(boolean enableTracking) {
        this.mIsTracking = enableTracking;
    }
    public void updateLocation(LatLong latLong) {
        mTrackPathPoints.add(latLong);
        mTrackPathPolyline.setPoints(mTrackPathPoints);
    }
    public void stopTracking() {
        this.mIsTracking = false;
        mMapViewManager.mMapView.getLayerManager().getLayers().remove(mTrackPathPolyline);
    }
    public void insertIconInMap(LatLong latLong, int id) {
        try {
            Drawable drawable = ContextCompat.getDrawable(mMapViewManager.mActivity, id);
            if (drawable == null) {
                Log.e("MapViewManager", "Drawable icon is null");
            }
            Bitmap originLocationIcon = AndroidGraphicFactory.convertToBitmap(drawable);
            Marker marker = new Marker(latLong, originLocationIcon, 0, 0);
            mMapViewManager.mMapView.getLayerManager().getLayers().add(marker);
        } catch (Exception e){
            Log.e("MapViewManager", "Error trying to insert icon into Map View", e);
        }
    }
    public abstract void startTracking();
    protected abstract void addOriginToPathLine(LatLong latLong);
    protected void paintPathLine(){
        Paint pathStroke = mMapViewManager.mGraphicFactory.createPaint();
        pathStroke.setColor(Color.RED);
        pathStroke.setStrokeWidth(4);
        pathStroke.setStyle(Style.STROKE);
        mTrackPathPoints = new ArrayList<>();
        mTrackPathPolyline = new Polyline(pathStroke, mMapViewManager.mGraphicFactory);
        mTrackPathPolyline.setPoints(mTrackPathPoints);
        mMapViewManager.mMapView.getLayerManager().getLayers().add(mTrackPathPolyline);
    }
}
