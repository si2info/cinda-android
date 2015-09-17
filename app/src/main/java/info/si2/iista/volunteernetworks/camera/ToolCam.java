package info.si2.iista.volunteernetworks.camera;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import info.si2.iista.volunteernetworks.R;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 16/9/15
 * Project: Virde
 */
public class ToolCam {

    // App
    private static Context context;

    // Camera and gallery
    private static String mCurrentPhotoPath;
    private static AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    // Qualities
    public static final int LOW = 1;
    public static final int MEDIUM = 2;
    public static final int LARGE = 3;
    public static final int ORIGINAL = 4;

    /**
     * Devuelve el archivo del álbum de la aplicación
     * @return File de la aplicación
     */
    private static File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            if (mAlbumStorageDirFactory == null)
                mAlbumStorageDirFactory = new BaseAlbumDirFactory();

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("Camera", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(context.getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    /**
     * Nombre del álbum de esta aplicación
     * @return String del nombre del álbum
     */
    private static String getAlbumName() {
        return context.getString(R.string.album_name);
    }

    /**
     * Creación de File con su nombre
     * @return File de la foto a guardar
     * @throws IOException
     */
    private static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault()).format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
    }

    /**
     * Creación de File con su nombre y ruta en mCurrenPhotoPath
     * @param c Contexto desde donde se llama al método
     * @return File de la foto a guardar
     * @throws IOException
     */
    public static File setUpPhotoFile(Context c) throws IOException {

        context = c;
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        Util.saveStPreferenceModel(c, c.getString(R.string.currentPhotoPath), mCurrentPhotoPath);

        return f;

    }

    /**
     * Elimina una foto que ya no es necesaria
     * @param path Path de la imagen
     * @return True or False si ha realizado la operación
     */
    public static boolean deleteUnusedFile (String path) {

        if (path != null) {
            File file = new File(path);
            mCurrentPhotoPath = null;
            return file.delete();
        }

        return true;

    }

    /**
     * Obtener ruta de la foto seleccionada
     */
    public static String getPath(Context c, Intent data) {

        Uri uri = data.getData();

        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        // Get the cursor
        Cursor cursor = c.getContentResolver().query(uri,
                filePathColumn, null, null, null);
        // Move to first row
        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            return imgDecodableString;

        }

        return "";
    }

    /**
     * Refresh de la galería para luego por encontrar la foto desde otras aplicaciones
     */
    public static void galleryAddPic(Context context) {

        if (mCurrentPhotoPath == null)
            mCurrentPhotoPath = Util.getStPreferenceModel(context, context.getString(R.string.currentPhotoPath));

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    // Decodes image and scales it to reduce memory consumption
    private static Bitmap decodeFile(File f) {
        try {
            Bitmap b;

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            int quality = getQuality(context, 1);
            if (o.outHeight > quality || o.outWidth > quality) {
                scale = (int)Math.pow(2, (int) Math.ceil(Math.log(quality /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

            return b;
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String reduceAndSaveImage (String dir, boolean fromCamera) {

        try {

            File reduced = createImageFile();
            Bitmap originalReduced = decodeFile(new File(dir));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (originalReduced != null) {
                originalReduced.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            }
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(reduced);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            if (fromCamera) {
                File original = new File(dir);
                boolean isDelete = original.delete();
                if (isDelete)
                    Log.i("File", "Original deleted");
            }

            return reduced.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static int getQuality (Context c, int quality) {

        ArrayList<Integer> intQualities = new ArrayList<>();

        intQualities.add(800);  // Low
        intQualities.add(1024); // Normal
        intQualities.add(2048); // Hight
        intQualities.add(4000); // Original

        for (Integer selectedQuality : intQualities) {
            if (quality == selectedQuality)
                return selectedQuality;
        }

        return intQualities.get(1);

    }

}
