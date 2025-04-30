package es.ull.etsii.testastrolabos.DataModels;

public class SpeedModel {
    public enum Unit{
        KILOMETERS_PER_HOUR,
        METERS_PER_SECOND,
        MILES_PER_HOUR,
        KNOTS,
        MACH;
        @Override
        public String toString() {
            switch (this) {
                case KILOMETERS_PER_HOUR: return "km/h";
                case METERS_PER_SECOND: return "m/s";
                case MILES_PER_HOUR: return "mph";
                case KNOTS: return "Knots";
                case MACH: return "Mach";
                default: return super.toString();
            }
        }
    }
    private Unit mUnit = Unit.KILOMETERS_PER_HOUR;
    private double mKilometersPerHourSpeed = 0.0;
    private static final double KMH_TO_METERSPERSECOND = 0.27778;
    private static final double KMH_TO_MILESPERHOUR = 0.621371;
    private static final double KMH_TO_KNOTS = 0.539957;
    private static final double KMH_TO_MACH = 0.0008163;
    private static final double DEFAULT_CONVERSION_FACTOR = 1.0;
    private double mConversionFactor = DEFAULT_CONVERSION_FACTOR;

    /**
     * Default constructor assigns 0.0 kilometers per hour
     */
    public SpeedModel(){}

    public  void setSpeed(double speed){
        mKilometersPerHourSpeed = speed;
    }
    public void setUnit(Unit unit){
        mUnit = unit;
        switch (unit){
            case KILOMETERS_PER_HOUR:
                mConversionFactor = DEFAULT_CONVERSION_FACTOR;
                break;
            case METERS_PER_SECOND:
                mConversionFactor = KMH_TO_METERSPERSECOND;
                break;
            case MILES_PER_HOUR:
                mConversionFactor = KMH_TO_MILESPERHOUR;
                break;
            case KNOTS:
                mConversionFactor = KMH_TO_KNOTS;
                break;
            case MACH:
                mConversionFactor = KMH_TO_MACH;
                break;
        }
    }
    public int getSpeed(){
        return (int)(mKilometersPerHourSpeed * mConversionFactor);
    }

    @Override
    public String toString() {
        if (mKilometersPerHourSpeed == AstrolabosLocationModel.LOCATION_WITH_NO_DATA)
            return AstrolabosLocationModel.UNAVAILABLE_DATA;
        return getSpeed() + " " + mUnit.toString();
    }
}
