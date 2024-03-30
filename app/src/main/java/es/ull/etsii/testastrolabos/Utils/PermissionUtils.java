package es.ull.etsii.testastrolabos.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class PermissionUtils {

    public static final int REQUEST_CODE = 100;

    public static void openFilePicker(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");  // Tipo de archivo a seleccionar
        intent.putExtra(Intent.EXTRA_TITLE, "nombre_predeterminado.txt");  // Nombre predeterminado del archivo
        activity.startActivityForResult(intent, REQUEST_CODE);
    }
}