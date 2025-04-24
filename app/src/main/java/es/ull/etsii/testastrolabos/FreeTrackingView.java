package es.ull.etsii.testastrolabos;

import org.mapsforge.core.model.LatLong;

public class FreeTrackingView extends TrackingViewManager {
    public FreeTrackingView(MapViewManager mapViewManager) {
        super(mapViewManager);
    }

    @Override
    public void startTracking() {
        paintPathLine();
    }

    @SuppressWarnings("unused")
    @Override
    protected void addOriginToPathLine(LatLong latLong) {
        // Intentionally left blank
    }
}
