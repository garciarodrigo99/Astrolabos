package es.ull.etsii.testastrolabos.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import es.ull.etsii.testastrolabos.MainActivity;
import es.ull.etsii.testastrolabos.TrackingManager;

public class ActivityResultHandler {
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private static final int OPEN_FILE_RC = 0;

    public static final int CREATE_FILE_RC = 100;
    private final Context mContext;
    private final TrackingManager mTrackingManager;
    private final MainActivity mMainActivity;

    public ActivityResultHandler(MainActivity activity, TrackingManager trackingManager) {
        this.mContext = activity.getApplicationContext();
        this.mTrackingManager = trackingManager;
        this.mMainActivity = activity;
    }

    public void handle(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case CREATE_FILE_RC:
                handleFileWriting(data);
                break;
            case OPEN_FILE_RC:
                handleMapSelection(data);
                break;
            default:
                break;
        }
    }

    private void handleMapSelection(Intent data) {
        if (data == null) {
           return;
        }
        Uri uri = data.getData();
        mMainActivity.loadMap(uri);
    }

    private void handleFileWriting(Intent data) {
        if (data != null && data.getData() != null) {
            Uri uri = data.getData();
            FileManager.writeFileContent(mMainActivity,uri,mTrackingManager.fileFormat.toString());
            Toast.makeText(mMainActivity, "Archivo guardado con éxito", Toast.LENGTH_SHORT).show();
        }
    }
}