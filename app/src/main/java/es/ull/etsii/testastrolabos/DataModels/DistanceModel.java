package es.ull.etsii.testastrolabos.DataModels;


public class DistanceModel {
    public enum Unit{
        KILOMETERS,
        NAUTICAL_MILES;
        @Override
        public String toString() {
            switch (this) {
                case KILOMETERS: return "km";
                case NAUTICAL_MILES: return "nm";
                default: return super.toString();
            }
        }
    }
    private Unit mUnit = Unit.KILOMETERS;
    private double mKilometersDistance = 0.0;
    private static final double KILOMETERS_TO_NAUTICALMILES = 0.53996;
    private static final double DEFAULT_CONVERSION_FACTOR = 1.0;
    private double mConversionFactor = DEFAULT_CONVERSION_FACTOR;

    /**
     * Default constructor assigns 0.0 Kilometers
     */
    public DistanceModel(){}

    public  void setDistance(double distance){
        mKilometersDistance = distance;
    }
    public void setUnit(Unit unit){
        mUnit = unit;
        if(mUnit == Unit.KILOMETERS){
            mConversionFactor = DEFAULT_CONVERSION_FACTOR;
        } else if(mUnit == Unit.NAUTICAL_MILES){
            mConversionFactor = KILOMETERS_TO_NAUTICALMILES;
        }
    }
    public int getDistance(){
        return (int)(mKilometersDistance * mConversionFactor);
    }

    @Override
    public String toString() {
        if (mKilometersDistance == AstrolabosLocationModel.LOCATION_WITH_NO_DATA)
            return AstrolabosLocationModel.UNAVAILABLE_DATA;
        return getDistance() + " " + mUnit.toString();
    }
}
