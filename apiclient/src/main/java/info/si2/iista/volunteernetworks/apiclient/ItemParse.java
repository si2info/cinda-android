package info.si2.iista.volunteernetworks.apiclient;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 30/11/15
 * Project: Virde
 */
public class ItemParse {

    private String api;
    private String key;

    public ItemParse (String api, String key) {
        this.api = api;
        this.key = key;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
