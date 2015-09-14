package info.si2.iista.volunteernetworks.apiclient;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 3/9/15
 * Project: Virde
 */
public class ItemCampaign {

    private int type;
    @SerializedName("ID")
    private int id;
    private String headerColor;
    private String title;
    private String description;
    private String shortDescription;
    private String scope;
    private String image;
    @SerializedName("is_subscribed")
    private boolean isSuscribe;
    @SerializedName("date_start")
    private Date dateStart;
    @SerializedName("date_end")
    private Date dateEnd;
    private boolean loaded;

    public ItemCampaign(){

    }

    public ItemCampaign(int type, String headerColor, String title, String description, boolean isSuscribe) {
        this.type = type;
        this.headerColor = headerColor;
        this.title = title;
        this.description = description;
        this.isSuscribe = isSuscribe;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(String headerColor) {
        this.headerColor = headerColor;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSuscribe() {
        return isSuscribe;
    }

    public void setIsSuscribe(boolean isSuscribe) {
        this.isSuscribe = isSuscribe;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

}
