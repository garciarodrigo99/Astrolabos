package es.ull.etsii.testastrolabos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import es.ull.etsii.testastrolabos.Model.TrackSettings;
import es.ull.etsii.testastrolabos.Services.FileManager;
import es.ull.etsii.testastrolabos.Services.Writers.FileFormat;
import es.ull.etsii.testastrolabos.Services.Writers.JsonFormat;
import es.ull.etsii.testastrolabos.Dialogs.*;

import java.util.function.Consumer;

public class TrackingManager {

    private Context context_;
    private MainActivity activity;
    private TrackSettings mTrackSettings;

    public enum State {
        TRACKING,
        NOT_TRACKING
    };

    private State state_ = State.NOT_TRACKING;
    public FileFormat fileFormat;
    private StartTrackingDialog startTrackingDialog;

    public TrackingManager(MainActivity activity) {
        this.activity = activity;
        this.context_ = activity;
        this.startTrackingDialog = new StartTrackingDialog(activity);
    }

    public State getState() {
        return state_;
    }

    public TrackSettings startTracking(Consumer<TrackSettings> onAccepted) {
        startTrackingDialog.setAcceptAction(data -> {
            this.mTrackSettings = data;
            if (mTrackSettings != null) {
                // Por ejemplo, guardar los datos en una base de datos o hacer algo más
                //recordFlightData = data;
                //TODO: uncomment
                Toast.makeText(context_, mTrackSettings.getFlightName() + " tracking started", Toast.LENGTH_SHORT).show();
                //fileWriter = new FileWriter(data.getFlightName(),data.getMaxUpdate(),data.getMinUpdate());
                //TODO: uncomment
                fileFormat = new JsonFormat(mTrackSettings);
                state_ = State.TRACKING;
                onAccepted.accept(mTrackSettings);
            } else {
                // Si el usuario cancela el diálogo, no se hace nada
                Toast.makeText(context_, "Cancelando en accept mTrackSettings=null", Toast.LENGTH_SHORT).show();
            }
        });
        startTrackingDialog.show(activity.getSupportFragmentManager(), "StartTrackingDialog");
        return mTrackSettings;
    }

    public void showTrackingSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context_);
        LayoutInflater inflater = LayoutInflater.from(this.context_);
        View dialogView = inflater.inflate(R.layout.dialog_tracking_config, null);
        builder.setView(dialogView);
        builder.setPositiveButton("Guardar",(dialog, which) -> {
            // TODO: set update time
        });
        builder.setNegativeButton("Cancelar",(dialog, which) -> {
//            action.cancel(null);
        });
        builder.create().show();
    }

    public void finishAndSave() {
        Runnable finishAndSave = new Runnable() {
            @Override
            public void run() {
                FileManager.createDocumentIntent(activity);
                state_ = State.NOT_TRACKING;
                activity.stopTracking();
            }
        };
        showConfirmationDialog(
                this.context_.getString(R.string.tv_dialog_confirmation_title_save_and_finish),
                this.context_.getString(R.string.tv_dialog_confirmation_question_save_and_finish),
                this.context_.getString(R.string.tv_dialog_confirmation_toast_text_save_and_finish),
                finishAndSave);

    }

    public void cancel() {
        Runnable cancel = new Runnable() {
            @Override
            public void run() {
                fileFormat = null;
                state_ = State.NOT_TRACKING;
                mTrackSettings = null;
                activity.stopTracking();
            }
        };

        showConfirmationDialog(
                this.context_.getString(R.string.tv_dialog_confirmation_title_cancel_tracking),
                this.context_.getString(R.string.tv_dialog_confirmation_question_cancel_tracking),
                this.context_.getString(R.string.tv_dialog_confirmation_toast_text_cancel_tracking),
                cancel);
    }

    private void showConfirmationDialog(String title, String question, String toastText, Runnable acceptAction) {
        // Lógica para cancelar el seguimiento
        Dialogs.showConfirmationDialog(this.context_, title, question,
                new AcceptCancelActions<Void>() {
                    @Override
                    public void accept(Void data) {
                        //TODO: Guardar los registros en el archivo
                        Toast.makeText(context_,toastText,Toast.LENGTH_LONG).show();
                        acceptAction.run();
                    }

                    @Override
                    public void cancel(Void data) {}

                });
    }

    public TrackSettings getTrackSettings() {
        return mTrackSettings;
    }
}

