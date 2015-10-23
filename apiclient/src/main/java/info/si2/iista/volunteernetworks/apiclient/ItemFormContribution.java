package info.si2.iista.volunteernetworks.apiclient;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 16/9/15
 * Project: Virde
 */
public class ItemFormContribution {

    private String key, value;
    private boolean withImage;

    public ItemFormContribution () {}

    public ItemFormContribution (String key, String value) {

        this.key = key;
        this.value = value;
        this.withImage = false;

    }

    public ItemFormContribution (String key, String value, boolean withImage) {

        this.key = key;
        this.value = value;
        this.withImage = withImage;

    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isWithImage() {
        return withImage;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setWithImage(boolean withImage) {
        this.withImage = withImage;
    }

}
