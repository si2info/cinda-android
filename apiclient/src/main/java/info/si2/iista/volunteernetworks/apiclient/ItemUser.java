package info.si2.iista.volunteernetworks.apiclient;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 2/10/15
 * Project: Virde
 */
public class ItemUser {

    private int nTop, nContributions;
    private String username, image;

    public ItemUser(int nTop, int nContributions, String username, String image) {
        this.nTop = nTop;
        this.nContributions = nContributions;
        this.username = username;
        this.image = image;
    }

    public int getnTop() {
        return nTop;
    }

    public int getnContributions() {
        return nContributions;
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

}
