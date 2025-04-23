package es.ull.etsii.testastrolabos.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import es.ull.etsii.testastrolabos.MainActivity;
import es.ull.etsii.testastrolabos.R;

public class LocationUpdatesSettingsDialog extends DialogFragment {
    private Context context_;
    private Activity activity;
    public LocationUpdatesSettingsDialog(MainActivity activity) {
        this.activity = activity;
        this.context_ = activity;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_updates_config, null);

        // Create buttons
        SwitchCompat sw_gps = view.findViewById(R.id.dialog_updates_config_sw_sensor);
        EditText et_sensor = view.findViewById(R.id.dialog_updates_config_number_seconds);

        // Set slots
        sw_gps.setOnClickListener(v -> {
            if (sw_gps.isChecked()){
                sw_gps.setText(sw_gps.getTextOn());
            } else {
                sw_gps.setText(sw_gps.getTextOff());
            }
        });

        builder.setPositiveButton("Guardar",(dialog, which) -> {
            // TODO: update time and sensor
            //mLocationManager.setFastUpdateLocationRequest();
            //mLocationManager.setPowerBalanceLocationRequest();
        });
        builder.setNegativeButton("Cancelar",(dialog, which) -> {
        });

        builder.setView(view);
        return builder.create();
    }
}
