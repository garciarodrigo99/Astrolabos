package es.ull.etsii.testastrolabos;

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.layer.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public abstract class TrackingViewManager {
    protected boolean mIsTracking = false;
    protected MapViewManager mMapViewManager;

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

    public abstract void startTracking();
    protected abstract void addOriginToPathLine(LatLong latLong);
    protected void paintPathLine(){
        Paint pathStroke = mMapViewManager.getGraphicFactory().createPaint();
        pathStroke.setColor(Color.RED);
        pathStroke.setStrokeWidth(4);
        pathStroke.setStyle(Style.STROKE);
        mTrackPathPoints = new ArrayList<>();
        mTrackPathPolyline = new Polyline(pathStroke, mMapViewManager.getGraphicFactory());
        mTrackPathPolyline.setPoints(mTrackPathPoints);
        mMapViewManager.mMapView.getLayerManager().getLayers().add(mTrackPathPolyline);
    }
}
