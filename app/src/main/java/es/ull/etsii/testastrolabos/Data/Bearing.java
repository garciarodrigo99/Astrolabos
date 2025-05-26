package es.ull.etsii.testastrolabos.Data;

import es.ull.etsii.testastrolabos.Model.AstrolabosLocationModel;

public class Bearing {
    private double mBearing;
    public Bearing() {}
    public void setBearing(double bearing) {
        mBearing = bearing;
    }

    public int getBearing() {
        return (int)(mBearing);
    }
    @Override
    public String toString() {
        if (mBearing == AstrolabosLocationModel.LOCATION_WITH_NO_DATA)
            return AstrolabosLocationModel.UNAVAILABLE_DATA;
        return String.format("%03dº",getBearing());
    }
}
