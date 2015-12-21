package info.si2.iista.volunteernetworks.apiclient;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 3/9/15
 * Project: Virde
 */
public class ItemCampaign implements Parcelable {

    private int type;

    @SerializedName("ID")
    private int id;
    private int idServer;

    @SerializedName("color")
    private String headerColor;

    private String title;

    @SerializedName("description_extended")
    private String description;

    @SerializedName("description")
    private String shortDescription;

    private String scope;
    private String image;
    private String cover;

    @SerializedName("is_subscribed")
    private boolean isSuscribe;

    @SerializedName("date_start")
    private Date dateStart;

    @SerializedName("date_end")
    private Date dateEnd;

    @SerializedName("volunteers_top")
    private ArrayList<ItemTopUser> topUsers;

    @SerializedName("tracking")
    private boolean haveTracking;

    private String geoposition;

    /** Constructor **/
    public ItemCampaign(){
    }

    /** Getter and setter **/
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdServer() {
        return idServer;
    }

    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }

    public String getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(String headerColor) {
        this.headerColor = headerColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSuscribe() {
        return isSuscribe;
    }

    public void setIsSuscribe(boolean isSuscribe) {
        this.isSuscribe = isSuscribe;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public ArrayList<ItemTopUser> getTopUsers() {
        return topUsers;
    }

    public void setTopUsers(ArrayList<ItemTopUser> topUsers) {
        this.topUsers = topUsers;
    }

    public String getStringGeoposition () {
        return geoposition;
    }

    public void setGeoposition(String geoposition) {
        this.geoposition = geoposition;
    }

    public String[] getGeoposition() {

        String[] latLng = null;

        if (geoposition != null) {
            if (geoposition.contains(",")) {

                latLng = geoposition.split(",");

                // Trim result
                latLng[0] = latLng[0].trim();
                latLng[1] = latLng[1].trim();

            }
        }

        return latLng;
    }

    public void setGeoposition(String lat, String lng) {
        this.geoposition = lat + "," + lng;
    }

    public boolean haveTracking() {
        return haveTracking;
    }

    public void setHaveTracking(boolean haveTracking) {
        this.haveTracking = haveTracking;
    }

    /** Parcelable **/
    protected ItemCampaign(Parcel in) {
        type = in.readInt();
        id = in.readInt();
        headerColor = in.readString();
        title = in.readString();
        description = in.readString();
        shortDescription = in.readString();
        scope = in.readString();
        image = in.readString();
        geoposition = in.readString();
        isSuscribe = in.readByte() != 0x00;
        haveTracking = in.readByte() != 0x00;
        long tmpDateStart = in.readLong();
        dateStart = tmpDateStart != -1 ? new Date(tmpDateStart) : null;
        long tmpDateEnd = in.readLong();
        dateEnd = tmpDateEnd != -1 ? new Date(tmpDateEnd) : null;
        if (in.readByte() == 0x01) {
            topUsers = new ArrayList<>();
            in.readList(topUsers, ItemTopUser.class.getClassLoader());
        } else {
            topUsers = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeInt(id);
        dest.writeString(headerColor);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(shortDescription);
        dest.writeString(scope);
        dest.writeString(image);
        dest.writeString(geoposition);
        dest.writeByte((byte) (isSuscribe ? 0x01 : 0x00));
        dest.writeByte((byte) (haveTracking ? 0x01 : 0x00));
        dest.writeLong(dateStart != null ? dateStart.getTime() : -1L);
        dest.writeLong(dateEnd != null ? dateEnd.getTime() : -1L);
        if (topUsers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(topUsers);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ItemCampaign> CREATOR = new Parcelable.Creator<ItemCampaign>() {
        @Override
        public ItemCampaign createFromParcel(Parcel in) {
            return new ItemCampaign(in);
        }

        @Override
        public ItemCampaign[] newArray(int size) {
            return new ItemCampaign[size];
        }
    };

}
