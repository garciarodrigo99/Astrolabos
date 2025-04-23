package es.ull.etsii.testastrolabos.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import es.ull.etsii.testastrolabos.MainActivity;
import es.ull.etsii.testastrolabos.R;
import es.ull.etsii.testastrolabos.TrackSettings;
import es.ull.etsii.testastrolabos.TrackingActionDialog;

public class StartTrackingDialog extends DialogFragment {
    private Context context_;
    private Activity activity;
    private AcceptAction<TrackSettings> acceptAction;
    public StartTrackingDialog(MainActivity activity) {
        this.activity = activity;
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
            TrackSettings data = new TrackSettings(flightName,maxUpdates,minUpdates);
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

    public void setAcceptAction(AcceptAction<TrackSettings> acceptAction) {
        this.acceptAction = acceptAction;
    }
}
