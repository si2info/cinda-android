package info.si2.iista.volunteernetworks.apiclient;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 1/10/15
 * Project: Virde
 */
public class ItemContribution {

    private boolean isMine;
    private String user, description, date;

    public ItemContribution(boolean isMine, String user, String description, String date) {
        this.isMine = isMine;
        this.user = user;
        this.description = description;
        this.date = date;
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
