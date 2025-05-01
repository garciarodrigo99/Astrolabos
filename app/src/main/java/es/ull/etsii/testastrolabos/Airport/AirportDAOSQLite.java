package es.ull.etsii.testastrolabos.Airport;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AirportDAOSQLite implements AirportDAO {
    private static final String DATABASE_NAME = "airports.db";
    private final Context context;
    private SQLiteDatabase database;
    public AirportDAOSQLite(Context context) {
        this.context = context;
        try{
            loadDb();
        } catch (IOException e) {
            Log.e("AirportDAOSQLite", e.getMessage(), e);
        }

    }
    public void loadDb() throws IOException, SQLException {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        try {
            createFileDb(dbFile);
            database = SQLiteDatabase.openDatabase(
                    dbFile.getPath(),
                    null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (IOException e) {
            Log.w("AirportDAOSQLite", e.getMessage(), e);
        }
    }

    private void createFileDb(File dbFile) throws IOException {
        if (dbFile.exists()) {
            dbFile.getParentFile().mkdirs();

            InputStream input = null;
            try {
                input = context.getAssets().open(DATABASE_NAME);
                Log.d("AirportDAOSQLite", "Archivo cargado correctamente desde assets.");
            } catch (IOException e) {
                Log.e("AirportDAOSQLite", "No se pudo abrir el archivo desde assets", e);
            }
            // InputStream input = context.getAssets().open(DATABASE_NAME);
            OutputStream output = Files.newOutputStream(Paths.get(dbFile.getPath()));

            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            input.close();
        }
    }
    @Override
    public void add(Airport airport) {

    }

    @Override
    public void update(Airport airport) {

    }

    @Override
    public void delete(Airport airport) {

    }

    @Override
    public List<Airport> getAllAirports() {
        List<Airport> list = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM airports",
                null);

        if (cursor.moveToFirst()) {
            do {
                String iataCode = cursor.getString(0);
                String icaoCode = cursor.getString(1);
                String name = cursor.getString(2);
                double latitude = cursor.getDouble(3);
                double longitude = cursor.getDouble(4);
                int elevation = cursor.getInt(5);
                String timeZone = cursor.getString(6);
                String country = cursor.getString(8);
                String city = cursor.getString(9);
                String state = cursor.getString(10);
                String county = cursor.getString(11);

                Airport airport = new Airport(
                        iataCode,icaoCode,name,latitude,longitude,elevation,
                        timeZone,city,county,state,country);
                list.add(airport);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    @Override
    public Airport getAirportByIATACode(String code) {
        return null;
    }

    @Override
    public Airport getAirportByICAOCode(String code) {
        return null;
    }
}
