package es.ull.etsii.testastrolabos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

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

        // Obtener botones
        Button btn1 = view.findViewById(R.id.btn_exit_dialog);
        Button btn2 = view.findViewById(R.id.btn_finish_save);
        Button btn3 = view.findViewById(R.id.btn_cancel);

        // Configurar botones
        btn1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Exit", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btn2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Finish and save", Toast.LENGTH_SHORT).show();
            trackingManager_.finishAndSave();
            dismiss();
        });

        btn3.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cancel tracking", Toast.LENGTH_SHORT).show();
            trackingManager_.cancel();
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }
}
