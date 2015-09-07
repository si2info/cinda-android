package info.si2.iista.bolunteernetworks.apiclient;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class ItemSync {

    private int type;
    private String imgCampaign, title, description;
    private boolean isSync, isSynchronizing;

    public ItemSync (int type, String imgCampaign, String title, String description, boolean isSync) {
        this.type = type;
        this.imgCampaign = imgCampaign;
        this.title = title;
        this.description = description;
        this.isSync = isSync;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSynchronizing() {
        return isSynchronizing;
    }

    public void setIsSynchronizing(boolean isSynchronizing) {
        this.isSynchronizing = isSynchronizing;
    }

}
