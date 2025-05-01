package es.ull.etsii.testastrolabos.DataModels;

public class DistanceModel {
    public enum Unit{
        KILOMETERS,
        NAUTICAL_MILES,
        STATUTE_MILES;
        @Override
        public String toString() {
            return switch (this) {
                case KILOMETERS -> "km";
                case NAUTICAL_MILES -> "nm";
                case STATUTE_MILES -> "mi";
                default -> super.toString();
            };
        }
    }
    private Unit mUnit = Unit.KILOMETERS;
    private double mMetersDistance = 0.0;
    private static final double DEFAULT_CONVERSION_FACTOR = 0.001; // Android API provide distance in meters
    private static final double KILOMETERS_TO_NAUTICALMILES = 0.53996 * DEFAULT_CONVERSION_FACTOR;
    private static final double KILOMETERS_TO_STATUTEMILES = 0.621371192 * DEFAULT_CONVERSION_FACTOR;
    private double mConversionFactor = DEFAULT_CONVERSION_FACTOR;

    /**
     * Default constructor assigns 0.0 Kilometers
     */
    public DistanceModel(){}

    public  void setDistance(double distance){
        mMetersDistance = distance;
    }
    public void setUnit(Unit unit){
        mUnit = unit;
        if(mUnit == Unit.KILOMETERS){
            mConversionFactor = DEFAULT_CONVERSION_FACTOR;
        } else if(mUnit == Unit.NAUTICAL_MILES){
            mConversionFactor = KILOMETERS_TO_NAUTICALMILES;
        } else if(mUnit == Unit.STATUTE_MILES){
            mConversionFactor = KILOMETERS_TO_STATUTEMILES;
        }
    }
    public int getDistance(){
        return (int)(mMetersDistance * mConversionFactor);
    }

    @Override
    public String toString() {
        if (mMetersDistance == AstrolabosLocationModel.LOCATION_WITH_NO_DATA)
            return AstrolabosLocationModel.UNAVAILABLE_DATA;
        return getDistance() + " " + mUnit.toString();
    }
}
