package es.ull.etsii.testastrolabos.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import es.ull.etsii.testastrolabos.R;
import es.ull.etsii.testastrolabos.TrackingManager;

public class TrackingActionDialog extends DialogFragment {
    private TrackingManager trackingManager_;
    public TrackingActionDialog(TrackingManager trackingManager) {
        this.trackingManager_ = trackingManager;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_tracking_action, null);

        // Create buttons
        Button btn1 = view.findViewById(R.id.dialog_tracking_action_btn_exit);
        Button btn2 = view.findViewById(R.id.dialog_tracking_action_btn_finish);
        Button btn3 = view.findViewById(R.id.dialog_tracking_action_btn_cancel);

        // Set slots
        btn1.setOnClickListener(v -> {
            Toast.makeText(getContext(), R.string.dialog_tracking_action_btn_exit,
                    Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btn2.setOnClickListener(v -> {
            Toast.makeText(getContext(), R.string.dialog_tracking_action_btn_finish,
                    Toast.LENGTH_SHORT).show();
            trackingManager_.finishAndSave();
            dismiss();
        });

        btn3.setOnClickListener(v -> {
            Toast.makeText(getContext(), R.string.dialog_tracking_action_btn_cancel,
                    Toast.LENGTH_SHORT).show();
            trackingManager_.cancel();
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }
}
