package es.ull.etsii.testastrolabos.Dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import es.ull.etsii.testastrolabos.R;

public class Dialogs {
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
