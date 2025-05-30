package es.ull.etsii.testastrolabos.Panels;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import es.ull.etsii.testastrolabos.Data.Airport;
import es.ull.etsii.testastrolabos.Data.Distance;
import es.ull.etsii.testastrolabos.MainActivity;
import es.ull.etsii.testastrolabos.R;
import org.mapsforge.core.model.LatLong;

public class FlightInfoPanel {
    private final MainActivity mActivity;

    private final TextView mOriginAirportTv, mDestinationAirportTv,
            mTimeFromStartTrackingTv, mTimeToDestinationTv,
            mDistanceFromOriginTv, mDistanceToDestinationTv,
            mProgressTv;
    private final ProgressBar mProgressBar;
    private final Airport mOriginAirport;
    private final Airport mDestinationAirport;
    private LatLong mCurrentLocation;
    private Distance mOriginDistance, mDestinationDistance, mTotalDistance;
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
        mProgressTv = this.mActivity.findViewById(R.id.tv_progress);
//        mProgressTv.set
        mProgressBar = this.mActivity.findViewById(R.id.progressBar);
        mOriginAirport = originAirport;
        mOriginAirportTv.setText(originAirport.getCodeIATA());
        mDestinationAirport = destinationAirport;
        mDestinationAirportTv.setText(destinationAirport.getCodeIATA());
        mOriginDistance = new Distance();
        mDestinationDistance = new Distance();
        mTotalDistance = new Distance();
        mTotalDistance.setDistance(mOriginAirport.getLatLong().
                sphericalDistance(mDestinationAirport.getLatLong()));
        mProgressBar.setProgress(0);
    }

    public void updateLocation(LatLong location) {
        this.mCurrentLocation = location;
        updateDistanceFromOrigin();
        updateDistanceToDestination();
        updateTimeFromStartTracking();
        updateTimeToDestination();
        updateProgressBar();
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

    private void updateProgressBar() {
        double progress = (double) mOriginDistance.getDistance() / mTotalDistance.getDistance();
        progress = Math.round(progress * 100);
        mProgressBar.setProgress((int)progress);
        String progressText = (int) progress + "%";
        mProgressTv.setText(progressText);
        Log.d("ProgressBar", progressText);
    }

    public Distance getOriginDistance() {
        return mOriginDistance;
    }

    public Distance getDestinationDistance() {
        return mDestinationDistance;
    }
}

