package es.ull.etsii.testastrolabos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import es.ull.etsii.testastrolabos.Writers.FileFormat;

public class GPSInfoPanelActionDialog extends DialogFragment {
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
            Toast.makeText(getContext(), "Opción 1 seleccionada", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btn2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opción 2 seleccionada", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btn3.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opción 3 seleccionada", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }
}
