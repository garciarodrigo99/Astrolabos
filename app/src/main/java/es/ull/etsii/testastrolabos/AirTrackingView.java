package es.ull.etsii.testastrolabos;

import android.util.Log;
import es.ull.etsii.testastrolabos.Airport.Airport;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class AirTrackingView extends TrackingViewManager{
    private Polyline mDestinationPolyline;
    private List<LatLong> mDestinationPoints;
    private final Airport mOriginAirport;
    private final Airport mDestinationAirport;
    private Marker mOriginMarker = null;
    private Marker mDestinationMarker = null;
    public AirTrackingView(MapViewManager mapViewManager, Airport origin, Airport destination) {
        super(mapViewManager);
        this.mOriginAirport = origin;
        this.mDestinationAirport = destination;
    }
    @Override
    public void updateLocation(LatLong latLong) {
        super.updateLocation(latLong);
        mDestinationPoints.set(0,latLong);
        mDestinationPolyline.setPoints(mDestinationPoints);
    }

    @Override
    public void startTracking() {
        paintAirports(mOriginAirport, mDestinationAirport);
        paintPathLine();
        addOriginToPathLine(new LatLong(mOriginAirport.getLatitude(), mOriginAirport.getLongitude()));
        paintDestinationLine();
    }

    private void paintDestinationLine() {
        mDestinationPoints = new ArrayList<>();
        mDestinationPoints.add(new LatLong(mOriginAirport.getLatitude(), mOriginAirport.getLongitude()));
        mDestinationPoints.add(new LatLong(mDestinationAirport.getLatitude(), mDestinationAirport.getLongitude()));
        Paint destinationStroke = mMapViewManager.getGraphicFactory().createPaint();
        destinationStroke.setColor(Color.BLACK);
        destinationStroke.setStrokeWidth(5);
        destinationStroke.setStyle(Style.STROKE);
        destinationStroke.setDashPathEffect(new float[]{20, 20}); // Línea segmentada
        mDestinationPolyline = new Polyline(destinationStroke,mMapViewManager.getGraphicFactory());
        mDestinationPolyline.setPoints(mDestinationPoints);
        mMapViewManager.mMapView.getLayerManager().getLayers().add(mDestinationPolyline);
    }

    @Override
    public void stopTracking() {
        mMapViewManager.mMapView.getLayerManager().getLayers().remove(mDestinationPolyline);
        mMapViewManager.mMapView.getLayerManager().getLayers().remove(mOriginMarker);
        mMapViewManager.mMapView.getLayerManager().getLayers().remove(mDestinationMarker);
        super.stopTracking();
    }

    private void paintAirports(Airport origin, Airport destination) {
        try {
            mOriginMarker = mMapViewManager.insertIconInMap(new LatLong(origin.getLatitude(), origin.getLongitude()),
                    R.drawable.ic_flight_takeoff);
            mDestinationMarker = mMapViewManager.insertIconInMap(new LatLong(destination.getLatitude(), destination.getLongitude()),
                    R.drawable.ic_flight_land);
        } catch (Exception e){
            Log.e("AirTrackingView", "Error painting airports", e);
        }
    }

    @Override
    protected void addOriginToPathLine(LatLong latLong) {
        mTrackPathPoints.add(latLong);
    }
}
