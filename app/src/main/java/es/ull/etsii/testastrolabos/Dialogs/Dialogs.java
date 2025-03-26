package es.ull.etsii.testastrolabos.Dialogs;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import es.ull.etsii.testastrolabos.R;
import es.ull.etsii.testastrolabos.TrackSettings;

public class Dialogs {

    public static void showRecordFlightDialog(Context context, final AcceptCancelActions<TrackSettings> action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_record_flight, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.tv_dialog_record_flight_title);
        final EditText flightNameEditText = dialogView.findViewById(R.id.et_dialog_record_flight_flight_update);
        final EditText minUpdateEditText = dialogView.findViewById(R.id.et_dialog_record_flight_slow_mode);
        final EditText maxUpdateEditText = dialogView.findViewById(R.id.et_dialog_record_flight_fast_mode);

        title.setText(R.string.dialogs_record_flight);

        minUpdateEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        maxUpdateEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setPositiveButton(R.string.dialogs_word_continue, (dialog, which) -> {
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
            action.accept(data);
        });

        builder.setNegativeButton(R.string.dialogs_word_cancel, (dialog, which) -> action.cancel(null));

        builder.create().show();
    }


    public static void showConfirmationDialog(Context context, String title, String message, final AcceptCancelActions<Void> action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);
        builder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.tv_dialog_confirmation_title);
        TextView messageTextView = dialogView.findViewById(R.id.tv_dialog_confirmation_question);

        titleTextView.setText(title);
        messageTextView.setText(message);

        builder.setPositiveButton(R.string.dialogs_word_yes, (dialog, which) -> action.accept(null));

        builder.setNegativeButton(R.string.dialogs_word_cancel, (dialog, which) -> action.cancel(null));

        builder.create().show();
    }

    public static void showInformationDialog(Context context, String title, String message, final AcceptAction<Void> action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);
        builder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.tv_dialog_confirmation_title);
        TextView messageTextView = dialogView.findViewById(R.id.tv_dialog_confirmation_question);

        titleTextView.setText(title);
        messageTextView.setText(message);

        builder.setPositiveButton(R.string.dialogs_word_accept, (dialog, which) -> action.accept(null));

        builder.create().show();
    }
}
