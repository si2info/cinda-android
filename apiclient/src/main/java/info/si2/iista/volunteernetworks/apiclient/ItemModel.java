package info.si2.iista.volunteernetworks.apiclient;

import com.google.gson.annotations.SerializedName;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 8/9/15
 * Project: Virde
 */
public class ItemModel {

    public static final String ITEM_ID_AUTHOR = "author_id";
    public static final String ITEM_AUTHOR_NAME = "author_name";
    public static final String ITEM_CREATE_DATE = "create_date";
    public static final String ITEM_EDIT_TEXT = "text";
    public static final String ITEM_EDIT_TEXT_BIG = "textarea";
    public static final String ITEM_EDIT_NUMBER = "number";
    public static final String ITEM_DATE = "date";
    public static final String ITEM_DATETIME = "datetime";
    public static final String ITEM_GEOPOS = "geopos";
    public static final String ITEM_IMAGE = "image";
    public static final String ITEM_FILE = "file";
    public static final String ITEM_SPINNER = "select";
    public static final String ITEM_DICTIONARY = "dictionary";
    public static final String ITEM_DESCRIPTION = "description";

    // App
    public static final String ITEM_CAMPAIGN_NAME = "campaign_name";
    public static final String ITEM_DATE_SEND = "date_send";
    public static final String ITEM_URL_SERVER = "url_server";

    private int id;
    @SerializedName("id_campaign")
    private int idCampaign;

    @SerializedName("field_position")
    private int fieldPosition;

    @SerializedName("field_required")
    private boolean fieldRequired;

    @SerializedName("field_label")
    private String fieldLabel;

    @SerializedName("field_name")
    private String fieldName;

    @SerializedName("field_description")
    private String fieldDescription;

    @SerializedName("field_type")
    private String fieldType;

    @SerializedName("field_options")
    private String fieldOptions;

    public ItemModel () {}

    public ItemModel (int id, int idCampaign, int fieldPosition, String fieldLabel, String fieldName,
                      String fieldDescription, String fieldType, boolean fieldRequired, String fieldOptions) {
        this.id = id;
        this.idCampaign = idCampaign;
        this.fieldPosition = fieldPosition;
        this.fieldLabel = fieldLabel;
        this.fieldName = fieldName;
        this.fieldDescription = fieldDescription;
        this.fieldType = fieldType;
        this.fieldRequired = fieldRequired;
        this.fieldOptions = fieldOptions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCampaign() {
        return idCampaign;
    }

    public void setIdCampaign(int idCampaign) {
        this.idCampaign = idCampaign;
    }

    public int getFieldPosition() {
        return fieldPosition;
    }

    public void setFieldPosition(int fieldPosition) {
        this.fieldPosition = fieldPosition;
    }

    public boolean getFieldRequired() {
        return fieldRequired;
    }

    public void setFieldRequired(boolean fieldRequired) {
        this.fieldRequired = fieldRequired;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldOptions() {
        return fieldOptions;
    }

    public void setFieldOptions(String fieldOptions) {
        this.fieldOptions = fieldOptions;
    }

}
