package es.ull.etsii.testastrolabos.Data;

import es.ull.etsii.testastrolabos.Model.AstrolabosLocationModel;

public class Altitude {
    public enum Unit{
        METERS,
        FEET;
        @Override
        public String toString() {
            return switch (this) {
                case METERS -> "m";
                case FEET -> "ft";
                default -> super.toString();
            };
        }
    }
    private Unit mUnit = Unit.METERS;
    private double mMetersAltitude = 0.0;
    public static final double METERS_TO_FEET = 3.28084;
    private static final double DEFAULT_CONVERSION_FACTOR = 1.0;
    private double mConversionFactor = DEFAULT_CONVERSION_FACTOR;

    /**
     * Default constructor assigns 0.0 Meters
     */
    public Altitude(){}

    public  void setAltitude(double altitude){
        mMetersAltitude = altitude;
    }
    public void setUnit(Unit unit){
        mUnit = unit;
        if(mUnit == Unit.METERS){
            mConversionFactor = DEFAULT_CONVERSION_FACTOR;
        } else if(mUnit == Unit.FEET){
            mConversionFactor = METERS_TO_FEET;
        }
    }
    public int getAltitude(){
        return (int)(mMetersAltitude * mConversionFactor);
    }

    @Override
    public String toString() {
        if (mMetersAltitude == AstrolabosLocationModel.LOCATION_WITH_NO_DATA)
            return AstrolabosLocationModel.UNAVAILABLE_DATA;
        return getAltitude() + " " + mUnit.toString();
    }
}
