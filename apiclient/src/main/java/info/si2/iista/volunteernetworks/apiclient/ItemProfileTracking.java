package info.si2.iista.volunteernetworks.apiclient;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 14/1/16
 * Project: Cinda
 */
public class ItemProfileTracking implements Parcelable {

    private String id;
    private int type;

    @SerializedName("id_campaign")
    private int idCampaign;

    @SerializedName("id_volunteer")
    private int idVolunteer;

    @SerializedName("campaign_name")
    private String nameCampaign;

    @SerializedName("campaign_image")
    private String imageCampaign;

    private String tracking;

    @SerializedName("create_date")
    private Date createDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIdCampaign() {
        return idCampaign;
    }

    public void setIdCampaign(int idCampaign) {
        this.idCampaign = idCampaign;
    }

    public int getIdVolunteer() {
        return idVolunteer;
    }

    public void setIdVolunteer(int idVolunteer) {
        this.idVolunteer = idVolunteer;
    }

    public String getNameCampaign() {
        return nameCampaign;
    }

    public void setNameCampaign(String nameCampaign) {
        this.nameCampaign = nameCampaign;
    }

    public String getImageCampaign() {
        return imageCampaign;
    }

    public void setImageCampaign(String imageCampaign) {
        this.imageCampaign = imageCampaign;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    protected ItemProfileTracking(Parcel in) {
        id = in.readString();
        type = in.readInt();
        idCampaign = in.readInt();
        idVolunteer = in.readInt();
        nameCampaign = in.readString();
        imageCampaign = in.readString();
        tracking = in.readString();
        long tmpCreateDate = in.readLong();
        createDate = tmpCreateDate != -1 ? new Date(tmpCreateDate) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(type);
        dest.writeInt(idCampaign);
        dest.writeInt(idVolunteer);
        dest.writeString(nameCampaign);
        dest.writeString(imageCampaign);
        dest.writeString(tracking);
        dest.writeLong(createDate != null ? createDate.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ItemProfileTracking> CREATOR = new Parcelable.Creator<ItemProfileTracking>() {
        @Override
        public ItemProfileTracking createFromParcel(Parcel in) {
            return new ItemProfileTracking(in);
        }

        @Override
        public ItemProfileTracking[] newArray(int size) {
            return new ItemProfileTracking[size];
        }
    };

}
