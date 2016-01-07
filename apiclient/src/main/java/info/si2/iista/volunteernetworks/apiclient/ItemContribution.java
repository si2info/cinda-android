package info.si2.iista.volunteernetworks.apiclient;

import com.google.gson.annotations.SerializedName;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 1/10/15
 * Project: Virde
 */
public class ItemContribution {

    private int id;
    private boolean isMine;

    @SerializedName("author_name")
    private String user;
    private int author_id;

    private String description;

    @SerializedName("create_date")
    private String date;

    // Wear
    private int ID;
    private String name;
    private String geopos;

    public ItemContribution(int id, boolean isMine, String user, String description, String date) {
        this.id = id;
        this.isMine = isMine;
        this.user = user;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeopos() {
        return geopos;
    }

    public void setGeopos(String geopos) {
        this.geopos = geopos;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

}
