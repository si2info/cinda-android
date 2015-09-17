package info.si2.iista.volunteernetworks.camera;

import android.os.Environment;

import java.io.File;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 16/9/15
 * Project: Virde
 */
public final class BaseAlbumDirFactory extends AlbumStorageDirFactory {

    // Standard storage location for digital camera files
    private static final String CAMERA_DIR = "/";

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File (
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }
}
