package es.ull.etsii.testastrolabos.Utils;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.OutputStream;

public class FileUtils {

    public static void writeFileContent(Context context, Uri uri, String content) {
        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(content.getBytes());
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}