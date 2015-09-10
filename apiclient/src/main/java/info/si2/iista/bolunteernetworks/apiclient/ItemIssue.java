package info.si2.iista.bolunteernetworks.apiclient;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 3/9/15
 * Project: Virde
 */
public class ItemIssue {

    private int type, id;
    private String headerColor, title, description, image;
    private boolean isSuscribe;

    public ItemIssue (int type, String headerColor, String title, String description, boolean isSuscribe) {
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

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSuscribe() {
        return isSuscribe;
    }

    public void setIsSuscribe(boolean isSuscribe) {
        this.isSuscribe = isSuscribe;
    }

}
