package es.ull.etsii.testastrolabos.DataModels;

public class BearingModel {
    private double mBearing;
    public BearingModel() {}
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
