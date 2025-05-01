package es.ull.etsii.testastrolabos;

import android.widget.TextView;
import es.ull.etsii.testastrolabos.Airport.Airport;
import es.ull.etsii.testastrolabos.DataModels.DistanceModel;
import org.mapsforge.core.model.LatLong;

public class FlightInfoPanel {
    private final MainActivity mActivity;

    private final TextView mOriginAirportTv, mDestinationAirportTv,
            mTimeFromStartTrackingTv, mTimeToDestinationTv,
            mDistanceFromOriginTv, mDistanceToDestinationTv;
    private final Airport mOriginAirport;
    private final Airport mDestinationAirport;
    private LatLong mCurrentLocation;
    private DistanceModel mOriginDistance;
    private DistanceModel mDestinationDistance;
    private final double JET_SPEED = 787;
    private final double TURBOPROP_SPEED = 340;

    public FlightInfoPanel(MainActivity activity, Airport originAirport, Airport destinationAirport) {
        this.mActivity = activity;
        mOriginAirportTv = this.mActivity.findViewById(R.id.tv_originAirport);
        mDestinationAirportTv = this.mActivity.findViewById(R.id.tv_destinationAirport);
        mTimeFromStartTrackingTv = this.mActivity.findViewById(R.id.tv_timeFromStartTracking);
        mTimeToDestinationTv = this.mActivity.findViewById(R.id.tv_timeToDestination);
        mDistanceFromOriginTv = this.mActivity.findViewById(R.id.tv_distanceFromOrigin);
        mDistanceToDestinationTv = this.mActivity.findViewById(R.id.tv_distanceToDestination);
        mOriginAirport = originAirport;
        mOriginAirportTv.setText(originAirport.getCodeIATA());
        mDestinationAirport = destinationAirport;
        mDestinationAirportTv.setText(destinationAirport.getCodeIATA());
        mOriginDistance = new DistanceModel();
        mDestinationDistance = new DistanceModel();
    }

    public void updateLocation(LatLong location) {
        this.mCurrentLocation = location;
        updateDistanceFromOrigin();
        updateDistanceToDestination();
        updateTimeFromStartTracking();
        updateTimeToDestination();
    }

    private void updateTimeFromStartTracking() {

    }
    private void updateTimeToDestination() {
        double metersToDestination = mCurrentLocation.sphericalDistance(mDestinationAirport.getLatLong());
        double kilometersToDestination = metersToDestination / 1000.0;  // dividir primero
        double timeInHours = kilometersToDestination / JET_SPEED;       // tiempo en horas, como double

        int hours = (int) timeInHours;                                  // parte entera = horas
        int minutes = (int) Math.round((timeInHours - hours) * 60);     // decimales convertidos a minutos

        mTimeToDestinationTv.setText(hours + ":" + String.format("%02d", minutes));  // para que los minutos tengan 2 dígitos
    }

    private void updateDistanceFromOrigin() {
        mOriginDistance.setDistance(mCurrentLocation.sphericalDistance(mOriginAirport.getLatLong()));
        mDistanceFromOriginTv.setText(mOriginDistance.toString());
    }
    private void updateDistanceToDestination() {
        mDestinationDistance.setDistance(mCurrentLocation.sphericalDistance(mDestinationAirport.getLatLong()));
        mDistanceToDestinationTv.setText(mDestinationDistance.toString());
    }

    public DistanceModel getOriginDistance() {
        return mOriginDistance;
    }

    public DistanceModel getDestinationDistance() {
        return mDestinationDistance;
    }
}

