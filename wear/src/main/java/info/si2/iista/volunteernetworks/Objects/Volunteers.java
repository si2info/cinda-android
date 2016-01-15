package info.si2.iista.volunteernetworks.Objects;

/**
 * Created by si2soluciones on 14/12/15.
 */
public class Volunteers {
    String id;
    String name;
    int suscriptions;
    int angle;

    public Volunteers(String id, String name, int suscriptions, int angle) {
        this.id = id;
        this.name = name;
        this.suscriptions = suscriptions;
        this.angle = angle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSuscriptions() {
        return suscriptions;
    }

    public void setSuscriptions(int suscriptions) {
        this.suscriptions = suscriptions;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}
