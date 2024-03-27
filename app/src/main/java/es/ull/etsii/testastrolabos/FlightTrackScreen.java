package es.ull.etsii.testastrolabos;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import es.ull.etsii.testastrolabos.dialogs.*;

public class FlightTrackScreen {

    private Context mainActivityViewContext;
    private final View mainActivityView;
    private final ConstraintLayout startTrackingScreen;
    private final ConstraintLayout recordingTrackingScreen;

    private Button btnStartTracking;
    private Button btnFinishSave;
    private Button btnCancel;

    private SwitchCompat sw_mode;
    private TextView tv_tracking,tv_flight_name;

    public FlightTrackScreen(View view) {

        this.mainActivityView = view;
        this.mainActivityViewContext = view.getContext();
        this.startTrackingScreen = this.mainActivityView.findViewById(R.id.start_tracking_screen);
        this.recordingTrackingScreen = this.mainActivityView.findViewById(R.id.recording_tracking_screen);

        // Inicializar los botones
        initializeButtons();

        sw_mode = this.mainActivityView.findViewById(R.id.sw_speed_mode);
        tv_tracking = this.mainActivityView.findViewById(R.id.tv_tracking);
        tv_flight_name = this.mainActivityView.findViewById(R.id.tv_flight_name);

        // Establecer los listeners para los botones
        setListeners();
    }

    private void initializeButtons() {
        btnStartTracking = mainActivityView.findViewById(R.id.btn_start_tracking);
        btnFinishSave = mainActivityView.findViewById(R.id.btn_finish_save);
        btnCancel = mainActivityView.findViewById(R.id.btn_cancel);
    }

    private void setListeners() {
        btnStartTracking.setOnClickListener(v -> startTracking());

        btnFinishSave.setOnClickListener(v -> finishAndSave());

        btnCancel.setOnClickListener(v -> cancel());

        sw_mode.setOnClickListener(v -> {
            sw_mode.setText((sw_mode.isChecked()) ? sw_mode.getTextOn() : sw_mode.getTextOff());
        });
    }

    private void startTracking() {
        // Lógica para iniciar el seguimiento
        swapScreenVisibility(startTrackingScreen,recordingTrackingScreen);
    }

    private void finishAndSave() {
        showConfirmationDialog(
                this.mainActivityViewContext.getString(R.string.tv_dialog_confirmation_title_save_and_finish),
                this.mainActivityViewContext.getString(R.string.tv_dialog_confirmation_question_save_and_finish),
                this.mainActivityViewContext.getString(R.string.tv_dialog_confirmation_toast_text_save_and_finish));
    }

    private void cancel() {
        showConfirmationDialog(
                this.mainActivityViewContext.getString(R.string.tv_dialog_confirmation_title_cancel_tracking),
                this.mainActivityViewContext.getString(R.string.tv_dialog_confirmation_question_cancel_tracking),
                this.mainActivityViewContext.getString(R.string.tv_dialog_confirmation_toast_text_cancel_tracking));
    }

    private void showConfirmationDialog(String title, String question, String toastText) {
        // Lógica para cancelar el seguimiento
        AcceptCancelDialogs.showConfirmationDialog(this.mainActivityViewContext, title, question,
                new AcceptCancelActions<Void>() {
                    @Override
                    public void accept(Void data) {
                        //TODO: Guardar los registros en el archivo
                        swapScreenVisibility(recordingTrackingScreen,startTrackingScreen);
                        Toast.makeText(mainActivityViewContext,toastText,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void cancel(Void data) {}
                });
    }

    public static void swapScreenVisibility(View viewToSetGone, View viewToSetVisible){
        viewToSetGone.setVisibility(View.GONE);
        viewToSetVisible.setVisibility(View.VISIBLE);
    }
}

