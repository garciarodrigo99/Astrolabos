package es.ull.etsii.testastrolabos.Writers;

import android.location.Location;
import es.ull.etsii.testastrolabos.TrackSettings;

public abstract class FileFormat {

    protected TrackSettings info;
    protected Boolean acceptUpdates;
    protected final String formatType;
    public FileFormat(TrackSettings info, String formatType) {
        this.info = info;
        this.acceptUpdates = true;
        this.formatType = formatType;
    }
    public abstract void addContent(String timeStamp,
                                    Location location);
    public void stopUpdates() {
        this.acceptUpdates = false;
    }
    public boolean isUpdatesAvailable() {
        return this.acceptUpdates;
    }

}
