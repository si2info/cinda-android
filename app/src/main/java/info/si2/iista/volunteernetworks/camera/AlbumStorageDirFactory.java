package info.si2.iista.volunteernetworks.camera;

import java.io.File;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 16/9/15
 * Project: Virde
 */
abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
