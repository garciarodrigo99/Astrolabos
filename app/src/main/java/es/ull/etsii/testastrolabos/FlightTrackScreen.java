package es.ull.etsii.testastrolabos;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

public class FlightTrackScreen {

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
        switchScreenVisibility(startTrackingScreen,recordingTrackingScreen);
    }

    private void finishAndSave() {
        switchScreenVisibility(recordingTrackingScreen,startTrackingScreen);
        // Lógica para finalizar y guardar el seguimiento
        // ...
    }

    private void cancel() {
        switchScreenVisibility(recordingTrackingScreen,startTrackingScreen);
        // Lógica para cancelar el seguimiento
        // ...
    }

    public static void switchScreenVisibility(View viewToSetGone, View viewToSetVisible){
        viewToSetGone.setVisibility(View.GONE);
        viewToSetVisible.setVisibility(View.VISIBLE);
    }
}

