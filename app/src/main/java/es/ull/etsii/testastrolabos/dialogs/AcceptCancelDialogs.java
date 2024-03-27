package es.ull.etsii.testastrolabos.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import es.ull.etsii.testastrolabos.R;

public class AcceptCancelDialogs {
/*    public static class RecordFlightData {
        private String flightName;
        private int minUpdate;
        private int maxUpdate;

        public RecordFlightData(String flightName, int minUpdate, int maxUpdate) {
            this.flightName = flightName;
            this.minUpdate = minUpdate;
            this.maxUpdate = maxUpdate;
        }

        // Getters y setters

        public int getMinUpdate() {
            return minUpdate;
        }

        public int getMaxUpdate() {
            return maxUpdate;
        }

        public String getFlightName() {
            return flightName;
        }
    }
    public static void showRecordFlightDialog(Context context, final AcceptCancelActions<RecordFlightData> action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_record_flight, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.dialog_title);
        final EditText flightNameEditText = dialogView.findViewById(R.id.flight_name_edittext);
        final EditText minUpdateEditText = dialogView.findViewById(R.id.min_update_edittext);
        final EditText maxUpdateEditText = dialogView.findViewById(R.id.max_update_edittext);

        title.setText("Record flight");

        minUpdateEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        maxUpdateEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RecordFlightData data = new RecordFlightData(
                        flightNameEditText.getText().toString(),
                        Integer.parseInt(minUpdateEditText.getText().toString()),
                        Integer.parseInt(maxUpdateEditText.getText().toString())
                );
                action.accept(data);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No se hace nada
                action.cancel(null);
            }
        });

        builder.create().show();
    }*/


    public static void showConfirmationDialog(Context context, String title, String message, final AcceptCancelActions<Void> action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);
        builder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.tv_dialog_confirmation_title);
        TextView messageTextView = dialogView.findViewById(R.id.tv_dialog_confirmation_question);

        titleTextView.setText(title);
        messageTextView.setText(message);

        builder.setPositiveButton("Yes", (dialog, which) -> action.accept(null));

        builder.setNegativeButton("Cancel", (dialog, which) -> action.cancel(null));

        builder.create().show();
    }
}
