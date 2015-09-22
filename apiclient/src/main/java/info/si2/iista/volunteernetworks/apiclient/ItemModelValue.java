package info.si2.iista.volunteernetworks.apiclient;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 21/9/15
 * Project: Virde
 */
public class ItemModelValue {

    private int id, idModel, order;
    private String field, value;
    private boolean isSync;

    public ItemModelValue () {}

    public ItemModelValue(int idModel, String field, String value, int order, boolean isSync) {
        this.idModel = idModel;
        this.field = field;
        this.value = value;
        this.order = order;
        this.isSync = isSync;
    }

    public ItemModelValue(String field, String value) {
        this.field = field;
        this.value = value;
        this.isSync = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdModel() {
        return idModel;
    }

    public void setIdModel(int idModel) {
        this.idModel = idModel;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }

}
