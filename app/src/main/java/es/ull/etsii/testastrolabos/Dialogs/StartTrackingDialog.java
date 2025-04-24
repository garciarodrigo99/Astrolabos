package es.ull.etsii.testastrolabos.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import es.ull.etsii.testastrolabos.Airport.Airport;
import es.ull.etsii.testastrolabos.MainActivity;
import es.ull.etsii.testastrolabos.R;
import es.ull.etsii.testastrolabos.TrackSettings;

import java.util.List;

public class StartTrackingDialog extends DialogFragment {
    private Context context_;
    private MainActivity mActivity;
    private AcceptAction<TrackSettings> acceptAction;
    public StartTrackingDialog(MainActivity activity) {
        this.mActivity = activity;
        this.context_ = activity;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_tracking_start, null);

        TextView title = view.findViewById(R.id.tv_dialog_record_flight_title);
        final EditText flightNameEditText = view.findViewById(R.id.et_dialog_record_flight_flight_update);
        final EditText minUpdateEditText = view.findViewById(R.id.et_dialog_record_flight_slow_mode);
        final EditText maxUpdateEditText = view.findViewById(R.id.et_dialog_record_flight_fast_mode);
        CheckBox freeTrackingCb = view.findViewById(R.id.cb_free_tracking);
        LinearLayout flightTracking = view.findViewById(R.id.layout_airport_config);
        freeTrackingCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                flightTracking.setVisibility(View.VISIBLE);
            }  else {
                flightTracking.setVisibility(View.GONE);
            }
        });
        AutoCompleteTextView originAirportACTV = view.findViewById(R.id.ac_origin_airport);
        AutoCompleteTextView destinationAirportACTV = view.findViewById(R.id.ac_destination_airport);

        List<Airport> airports =  mActivity.getAirportDAO().getAllAirports();
        ArrayAdapter<Airport> originAirportAdapter = new ArrayAdapter<>(
                mActivity,
                android.R.layout.simple_dropdown_item_1line,
                airports
        );
        ArrayAdapter<Airport> destinationAirportAdapter = new ArrayAdapter<>(
                mActivity,
                android.R.layout.simple_dropdown_item_1line,
                airports
        );

        originAirportACTV.setAdapter(originAirportAdapter);
        destinationAirportACTV.setAdapter(destinationAirportAdapter);

        title.setText(R.string.dialogs_record_flight);

        minUpdateEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        maxUpdateEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setPositiveButton(R.string.dialogs_word_continue,(dialog, which) -> {
            String flightName = flightNameEditText.getText().toString();

            if (flightName.isEmpty()) {
                flightName = TrackSettings.DEFAULT_FLIGHT_NAME;
            }

            int maxUpdates;
            String maxUpdatesString = maxUpdateEditText.getText().toString();
            if (maxUpdatesString.isEmpty()) {
                maxUpdates = TrackSettings.DEFAULT_MAX_VALUE;
            } else {
                maxUpdates = Integer.parseInt(maxUpdatesString);
            }
            int minUpdates;
            String minUpdatesString = minUpdateEditText.getText().toString();
            if (minUpdatesString.isEmpty()) {
                minUpdates = TrackSettings.DEFAULT_MIN_VALUE;
            } else {
                minUpdates = Integer.parseInt(minUpdatesString);
            }
            TrackSettings data = null;
            boolean isFreeTracking = freeTrackingCb.isChecked();
            if (isFreeTracking){
                data = new TrackSettings(flightName,maxUpdates,minUpdates,
                        isFreeTracking);
            } else {
                // Get the text from the AutoCompleteTextView components
                String originAirportText = originAirportACTV.getText().toString();
                String destinationAirportText = destinationAirportACTV.getText().toString();

                // Find the corresponding Airport objects
                Airport originAirport = null;
                Airport destinationAirport = null;

                // Validate that airports are selected
                if (originAirportText.isEmpty() || destinationAirportText.isEmpty()) {
                    Toast.makeText(context_, "Please select both origin and destination airports", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Airport airport : airports) {
                    if (airport.toString().equals(originAirportText)) {
                        originAirport = airport;
                    }
                    if (airport.toString().equals(destinationAirportText)) {
                        destinationAirport = airport;
                    }
                }

                // Validate that selected airports exist in the database
                if (originAirport == null || destinationAirport == null) {
                    Toast.makeText(context_, "Selected airports not found in database", Toast.LENGTH_SHORT).show();
                    return;
                }

                data = new TrackSettings(flightName, maxUpdates, minUpdates,
                        isFreeTracking, originAirport, destinationAirport);
            }

            if (acceptAction != null) {
                acceptAction.accept(data);
            }
        });
        builder.setNegativeButton(R.string.dialogs_word_cancel,(dialog, which) -> {
            Toast.makeText(context_, "Flight tracking cancelled", Toast.LENGTH_SHORT).show();
        });

        builder.setView(view);
        return builder.create();
    }

    private void setDialog(){

    }

    public void setAcceptAction(AcceptAction<TrackSettings> acceptAction) {
        this.acceptAction = acceptAction;
    }
}
