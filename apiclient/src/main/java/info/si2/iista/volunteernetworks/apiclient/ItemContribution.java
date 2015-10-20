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

    private String description;

    @SerializedName("create_date")
    private String date;

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

}
