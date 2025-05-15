package es.ull.etsii.testastrolabos.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.IOException;
import java.io.OutputStream;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FileManager {
    public static final int CREATE_FILE_RC = 100;
    private static final int OPEN_FILE_RC = 0;
    private static final Logger logger = Logger.getLogger(FileManager.class.getName());

    public static void createDocumentIntent(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");  // Tipo de archivo a seleccionar
        intent.putExtra(Intent.EXTRA_TITLE, "nombre_predeterminado.txt");  // Nombre predeterminado del archivo
        activity.startActivityForResult(intent, CREATE_FILE_RC);
    }

    public static void openDocumentIntent(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        activity.startActivityForResult(intent, OPEN_FILE_RC);
    }

    public static void writeFileContent(Context context, Uri uri, String content) {
        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(content.getBytes());
                outputStream.close();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al intentar escribir en archivo", e);
        }
    }
}
