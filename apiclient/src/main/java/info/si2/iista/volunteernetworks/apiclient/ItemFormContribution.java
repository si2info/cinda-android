package info.si2.iista.volunteernetworks.apiclient;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 16/9/15
 * Project: Virde
 */
public class ItemFormContribution {

    private String key, value;

    public ItemFormContribution (String key, String value) {

        this.key = key;
        this.value = value;

    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
