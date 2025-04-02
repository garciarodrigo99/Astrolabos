package es.ull.etsii.testastrolabos;

import android.location.Location;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;

import java.text.DecimalFormat;

/**
 * Class that manage the actions to update the values of the UI
 */
public class UIWriter {
    public static void locationPermissionNotGranted(MainActivity activity){
        String warning = activity.getString(R.string.location_permissions_not_available);
        setWarningTextUIValues(activity,warning);
        activity.ib_location_updates_settings.setEnabled(false);
    }
    public static void locationNotEnabled(MainActivity activity){
        String warning = activity.getString(R.string.location_not_enabled_label);
        setWarningTextUIValues(activity,warning);
        activity.ib_location_updates_settings.setEnabled(false);
    }
    public static void notTrackingLocation(MainActivity activity){
        String warning = activity.getString(R.string.not_tracking_location);
        setWarningTextUIValues(activity,warning);
    }
    public static void locationNull(MainActivity activity){
        String warning = activity.getString(R.string.location_is_null);
        setWarningTextUIValues(activity,warning);
    }
    public static void writeLocation(MainActivity activity, Location location){
        // If the GPS cannot set a valid location, the location value will be null.
        // It must be handled this case due to nullPointerException that causes the app ends with no explanation to the user.
        if (location == null){
            // Location is null, maybe GPS sensors are not sending location information yet.
            UIWriter.locationNull(activity);
            return;
        }
        activity.gpsInfoPanel.setLatitude(String.valueOf(location.getLatitude()));
        activity.gpsInfoPanel.setLongitude(String.valueOf(location.getLongitude()));
        activity.gpsInfoPanel.setAccuracy(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()){
            activity.gpsInfoPanel.setAltitude(String.valueOf(location.getAltitude()));
        } else {
            activity.gpsInfoPanel.setAltitude(activity.getString(R.string.not_available_message));
        }
        if (location.hasSpeed()){
            activity.gpsInfoPanel.setSpeed(String.valueOf(location.getSpeed()));
        } else {
            activity.gpsInfoPanel.setSpeed(activity.getString(R.string.not_available_message));
        }
    }

    /**
     * When some the location information can not be shown in each element of the GUI, a string is set in all of those
     * elements of the GUI to help the user what is going on with location.
     * @param warning String with the warning to show in each element of the GUI
     */
    private static void setWarningTextUIValues(MainActivity activity, String warning){
        activity.gpsInfoPanel.setLatitude(warning);
        activity.gpsInfoPanel.setLongitude(warning);
        activity.gpsInfoPanel.setAccuracy(warning);
        activity.gpsInfoPanel.setAltitude(warning);
        activity.gpsInfoPanel.setSpeed(warning);
    }

    /**
     * When a switch changes from disable to enable, switch set as enabled and sets the correspondent text dependent if
     * it is checked or not. (probably the text was changed during the disabling)
     * @param _switch Switch to apply the changes
     */
    public static void whenEnableSwitch(SwitchCompat _switch, TextView switchTextView){
        _switch.setEnabled(true);
        switchTextView.setText(_switch.isChecked() ? _switch.getTextOn().toString() : _switch.getTextOff().toString());
    }

    private static String speedFormat(double speed){
        speed *= 3.6;
        Double formatedDouble = (double) speed;
        DecimalFormat decimalFormat;
        String toReturn;
        if (speed < 10.0){
            if (speed < 5.0){
                decimalFormat = new DecimalFormat("#.##");
            } else {
                decimalFormat = new DecimalFormat("#.#");
            }
            toReturn = decimalFormat.format(formatedDouble);
        } else {
            toReturn = Integer.toString(formatedDouble.intValue());
        }
        return toReturn;
    }
}
