package es.ull.etsii.testastrolabos.Data;

import es.ull.etsii.testastrolabos.Model.AstrolabosLocationModel;

public class Speed {
    public enum Unit{
        KILOMETERS_PER_HOUR,
        METERS_PER_SECOND,
        MILES_PER_HOUR,
        KNOTS,
        MACH;
        @Override
        public String toString() {
            return switch (this) {
                case KILOMETERS_PER_HOUR -> "km/h";
                case METERS_PER_SECOND -> "m/s";
                case MILES_PER_HOUR -> "mph";
                case KNOTS -> "Knots";
                case MACH -> "Mach";
                default -> super.toString();
            };
        }
    }
    private Unit mUnit = Unit.KILOMETERS_PER_HOUR;
    private double mKilometersPerHourSpeed = 0.0;
    private static final double MPS_TO_KMH = 3.6;
    private static final double MPS_TO_MILESPERHOUR = 2.23694;
    private static final double MPS_TO_KNOTS = 1.94384;
    private static final double MPS_TO_MACH = 0.00291545;
    private static final double MPS = 1.0; // Meters per second
    private double mConversionFactor = MPS_TO_KMH;

    /**
     * Default constructor assigns 0.0 kilometers per hour
     */
    public Speed(){}

    public void setSpeed(double speed){
        mKilometersPerHourSpeed = speed;
    }
    public void setUnit(Unit unit){
        mUnit = unit;
        switch (unit){
            case KILOMETERS_PER_HOUR:
                mConversionFactor = MPS_TO_KMH;
                break;
            case METERS_PER_SECOND:
                mConversionFactor = MPS;
                break;
            case MILES_PER_HOUR:
                mConversionFactor = MPS_TO_MILESPERHOUR;
                break;
            case KNOTS:
                mConversionFactor = MPS_TO_KNOTS;
                break;
            case MACH:
                mConversionFactor = MPS_TO_MACH;
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
