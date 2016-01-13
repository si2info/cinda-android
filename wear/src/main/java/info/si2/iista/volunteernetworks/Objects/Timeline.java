package info.si2.iista.volunteernetworks.Objects;

/**
 * Created by si2soluciones on 14/12/15.
 */
public class Timeline {
    String month;
    int contributions;

    public Timeline(String month, int contributions) {
        this.month = month;
        this.contributions = contributions;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getContributions() {
        return contributions;
    }

    public void setContributions(int contributions) {
        this.contributions = contributions;
    }
}
