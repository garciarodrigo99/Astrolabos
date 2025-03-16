package es.ull.etsii.testastrolabos;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import es.ull.etsii.testastrolabos.Utils.PermissionUtils;
import es.ull.etsii.testastrolabos.Writers.*;
import es.ull.etsii.testastrolabos.Dialogs.*;

public class FlightTrackScreen {

    private Context context;
    private final View view;
    private Activity activity;
    private final ConstraintLayout startTrackingScreen;
    private final ConstraintLayout recordingTrackingScreen;
    private Button btnFinishSave;
    private Button btnCancel;

    private SwitchCompat sw_mode;
    private TextView tv_tracking,tv_flight_name;

    public enum State {
        TRACKING,
        NOT_TRACKING
    };

    private State state_ = State.NOT_TRACKING;
    FileFormat fileFormat;

    public FlightTrackScreen(MainActivity activity) {
        this.activity = activity;
        this.view = activity.view_flight_track;
        this.context = view.getContext();
        this.startTrackingScreen = this.view.findViewById(R.id.start_tracking_screen);
        this.recordingTrackingScreen = this.view.findViewById(R.id.recording_tracking_screen);

        // Inicializar los botones
        initializeButtons();

        sw_mode = this.view.findViewById(R.id.sw_speed_mode);
        tv_tracking = this.view.findViewById(R.id.tv_tracking);
        tv_flight_name = this.view.findViewById(R.id.tv_flight_name);

        // Establecer los listeners para los botones
        setListeners();
    }

    public State getState() {
        return state_;
    }

    private void initializeButtons() {
//        btnStartTracking = view.findViewById(R.id.btn_start_tracking);
        btnFinishSave = view.findViewById(R.id.btn_finish_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
    }

    private void setListeners() {
//        btnStartTracking.setOnClickListener(v -> startTracking());

        btnFinishSave.setOnClickListener(v -> finishAndSave());

        btnCancel.setOnClickListener(v -> cancel());

        sw_mode.setOnClickListener(v -> {
            sw_mode.setText((sw_mode.isChecked()) ? sw_mode.getTextOn() : sw_mode.getTextOff());
        });
    }

    public void startTracking() {
        // Lógica para iniciar el seguimiento
        Dialogs.showRecordFlightDialog(this.context, new AcceptCancelActions<TrackSettings>() {
            @Override
            public void accept(TrackSettings data) {
                // Aquí puedes realizar la acción específica según los datos proporcionados
                if (data != null) {
                    // Por ejemplo, guardar los datos en una base de datos o hacer algo más
                    //recordFlightData = data;
                    //TODO: uncomment
                    Toast.makeText(view.getContext(), data.getFlightName() + " tracking started", Toast.LENGTH_SHORT).show();
                    //fileWriter = new FileWriter(data.getFlightName(),data.getMaxUpdate(),data.getMinUpdate());
                    //TODO: uncomment
                    tv_flight_name.setText(data.getFlightName());
                    fileFormat = new JsonFormat(data);
                } else {
                    // Si el usuario cancela el diálogo, no se hace nada
                    Toast.makeText(view.getContext(), "Cancelando en accept data=null", Toast.LENGTH_SHORT).show();
                }
                swapScreenVisibility(startTrackingScreen,recordingTrackingScreen);
                state_ = State.TRACKING;
            }

            @Override
            public void cancel(TrackSettings data) {
                Toast.makeText(context, "Flight tracking cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void finishAndSave() {
        Runnable finishAndSave = new Runnable() {
            @Override
            public void run() {
                PermissionUtils.openFilePicker(activity);
                state_ = State.NOT_TRACKING;
            }
        };
        showConfirmationDialog(
                this.context.getString(R.string.tv_dialog_confirmation_title_save_and_finish),
                this.context.getString(R.string.tv_dialog_confirmation_question_save_and_finish),
                this.context.getString(R.string.tv_dialog_confirmation_toast_text_save_and_finish),
                finishAndSave);

    }

    public void cancel() {
        Runnable cancel = new Runnable() {
            @Override
            public void run() {
                fileFormat = null;
                state_ = State.NOT_TRACKING;
            }
        };

        showConfirmationDialog(
                this.context.getString(R.string.tv_dialog_confirmation_title_cancel_tracking),
                this.context.getString(R.string.tv_dialog_confirmation_question_cancel_tracking),
                this.context.getString(R.string.tv_dialog_confirmation_toast_text_cancel_tracking),
                cancel);
    }

    private void showConfirmationDialog(String title, String question, String toastText, Runnable acceptAction) {
        // Lógica para cancelar el seguimiento
        Dialogs.showConfirmationDialog(this.context, title, question,
                new AcceptCancelActions<Void>() {
                    @Override
                    public void accept(Void data) {
                        //TODO: Guardar los registros en el archivo
                        swapScreenVisibility(recordingTrackingScreen,startTrackingScreen);
                        Toast.makeText(context,toastText,Toast.LENGTH_LONG).show();
                        acceptAction.run();
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

