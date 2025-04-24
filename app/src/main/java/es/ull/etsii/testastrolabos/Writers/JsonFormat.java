package es.ull.etsii.testastrolabos.Writers;

import android.location.Location;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.ull.etsii.testastrolabos.TrackSettings;
import org.jetbrains.annotations.NotNull;

public class JsonFormat extends FileFormat {

    public static final String FORMAT_TYPE = ".json";
    private JsonObject jsonObject;
    private JsonObject infoObject;
    public JsonFormat(TrackSettings trackSettings) {
        super(trackSettings,FORMAT_TYPE);
        // Crear el objeto JSON principal
        jsonObject = new JsonObject();

        // Crear el objeto "info" con los datos correspondientes
        infoObject = new JsonObject();
        infoObject.addProperty("fligh_name", this.info.getFlightName());
        infoObject.addProperty("max_update_time", this.info.getMaxValue());
        infoObject.addProperty("min_update_time", this.info.getMinValue());
        if (!trackSettings.isFreeTracking()){
            infoObject.addProperty("origin_latitude", this.info.getOriginAirport().getLatitude());
            infoObject.addProperty("origin_longitude", this.info.getOriginAirport().getLongitude());
            infoObject.addProperty("destination_latitude", this.info.getDestinationAirport().getLatitude());
            infoObject.addProperty("destination_longitude", this.info.getDestinationAirport().getLongitude());
        }

        // Agregar el objeto "info" al JSON principal
        jsonObject.add("info",infoObject);
    }

    @Override
    public void addContent(String timeStamp, Location location) {
        if (!acceptUpdates)
            return;

        // Crear un nuevo objeto JSON para el nuevo registro de seguimiento
        JsonObject newTrackingObject = new JsonObject();
        newTrackingObject.addProperty("timestamp", timeStamp);

        fillLocationInfo(newTrackingObject,location);

        // Obtener el array de seguimiento existente o crear uno nuevo si no existe
        JsonArray trackingArray = jsonObject.has("tracking") ? jsonObject.getAsJsonArray("tracking") : new JsonArray();

        // Agregar el nuevo registro de seguimiento al array
        trackingArray.add(newTrackingObject);

        // Agregar el array de seguimiento al JSON principal
        jsonObject.add("tracking", trackingArray);
    }

    private void fillLocationInfo(JsonObject trackingObject, Location location) {
        if (location == null){
            nullLocation(trackingObject);
            return;
        }
        trackingObject.addProperty("lat", location.getLatitude());
        trackingObject.addProperty("long", location.getLongitude());
        trackingObject.addProperty("alt", location.getAltitude());
        if (location.hasAltitude()){
            trackingObject.addProperty("accuracy", location.getAltitude());
        } else {
            trackingObject.addProperty("accuracy",DEFAULT_VALUE);
        }
        if (location.hasSpeed()){
            trackingObject.addProperty("speed", location.getSpeed());
        } else {
            trackingObject.addProperty("speed",DEFAULT_VALUE);
        }
    }

    private void nullLocation(JsonObject trackingObject){
        trackingObject.addProperty("lat", DEFAULT_VALUE);
        trackingObject.addProperty("long", DEFAULT_VALUE);
        trackingObject.addProperty("alt", DEFAULT_VALUE);
        trackingObject.addProperty("accuracy", DEFAULT_VALUE);
        trackingObject.addProperty("speed", DEFAULT_VALUE);
    }

    @Override
    public @NotNull String toString() {
        // Convertir el JSON a una cadena de texto
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }
}
