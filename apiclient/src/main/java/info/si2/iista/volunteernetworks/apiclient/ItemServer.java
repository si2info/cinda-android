package info.si2.iista.volunteernetworks.apiclient;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class ItemServer {

    private int id, type;
    private String server, descripcion;
    private boolean active;

    public ItemServer (int id, int type, String server, String descripcion, boolean active) {
        this.id = id;
        this.type = type;
        this.server = server;
        this.descripcion = descripcion;
        this.active = active;
    }

    public ItemServer () {}

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

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
