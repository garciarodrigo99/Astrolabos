package es.ull.etsii.testastrolabos.Data;

import android.location.GnssStatus;

public class Satellites {
    private GnssStatus mGnssStatus;
    private int mTotalSatellites;
    private int mLocationSatellites;
    public Satellites(){
        mTotalSatellites = 0;
        mLocationSatellites = 0;
    }
    public void updateStatus(GnssStatus gnssStatus){
        mGnssStatus = gnssStatus;
        if (mGnssStatus == null){
            mTotalSatellites = 0;
            mLocationSatellites = 0;
            return;
        }
        setTotalSatellites();
        setLocationSatellites();
    }

    private void setLocationSatellites() {
        mLocationSatellites = 0;
        for (int i = 0; i < mGnssStatus.getSatelliteCount(); i++) {
            if (mGnssStatus.usedInFix(i)) {
                mLocationSatellites++;
            }
        }
    }

    private void setTotalSatellites(){
        mTotalSatellites = mGnssStatus.getSatelliteCount();
    }

    @Override
    public String toString() {
        return mLocationSatellites + "/" + mTotalSatellites + " (POS/SATS)";
    }
}
