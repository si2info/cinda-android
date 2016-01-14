package info.si2.iista.volunteernetworks.apiclient;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class ItemSync {

    private long id;
    private String idGpx;
    private int type;
    private String imgCampaign, title, url, date;
    private boolean isSync, isSynchronizing;

    public ItemSync () {}

    public ItemSync (int type, String imgCampaign, String title, String url, String date, boolean isSync) {
        this.type = type;
        this.imgCampaign = imgCampaign;
        this.title = title;
        this.url = url;
        this.date = date;
        this.isSync = isSync;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdGpx() {
        return idGpx;
    }

    public void setIdGpx(String idGpx) {
        this.idGpx = idGpx;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImgCampaign() {
        return imgCampaign;
    }

    public void setImgCampaign(String imgCampaign) {
        this.imgCampaign = imgCampaign;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String description) {
        this.date = description;
    }

    public boolean isSynchronizing() {
        return isSynchronizing;
    }

    public void setIsSynchronizing(boolean isSynchronizing) {
        this.isSynchronizing = isSynchronizing;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
