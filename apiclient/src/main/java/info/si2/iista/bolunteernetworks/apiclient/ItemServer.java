package info.si2.iista.bolunteernetworks.apiclient;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class ItemServer {

    private int type;
    private String server;

    public ItemServer (int type, String server) {
        this.type = type;
        this.server = server;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

}
