package info.si2.iista.volunteernetworks.Objects;

/**
 * Created by si2soluciones on 14/12/15.
 */
public class Campaigns {
    String id;
    String name;
    int suscriptions;
    int angle;
    String color;

    public Campaigns(String id, String name, int suscriptions, int angle, String color) {
        this.id = id;
        this.name = name;
        this.suscriptions = suscriptions;
        this.angle = angle;
        this.color = color;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}


