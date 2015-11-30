package info.si2.iista.volunteernetworks.apiclient;

import com.google.gson.annotations.SerializedName;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class ItemServer {

    private int id, type;

    private String name, description, url;

    @SerializedName("gmaps")
    private ItemGoogleMaps mapsKeys;

    @SerializedName("parse")
    private ItemParse parseKeys;

    private boolean active;

    public ItemServer () {}

    public ItemServer(int id, int type, String url, String description, boolean active) {
        this.id = id;
        this.type = type;
        this.url = url;
        this.description = description;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ItemGoogleMaps getMapsKeys() {
        return mapsKeys;
    }

    public void setMapsKeys(ItemGoogleMaps mapsKeys) {
        this.mapsKeys = mapsKeys;
    }

    public ItemParse getParseKeys() {
        return parseKeys;
    }

    public void setParseKeys(ItemParse parseKeys) {
        this.parseKeys = parseKeys;
    }

}
