package info.si2.iista.volunteernetworks.apiclient;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 13/1/16
 * Project: Cinda
 */
public class ItemProfile implements Parcelable {

    private int id, type;

    @SerializedName("id_campaign")
    private int idCampaign;

    @SerializedName("create_date")
    private Date createDate;

    @SerializedName("author_id")
    private int authorId;

    @SerializedName("author_name")
    private String username;

    @SerializedName("campaign_name")
    private String nameCampaign;

    @SerializedName("campaign_image")
    private String imageCampaign;

    private String description, image;

    private String tracking;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    protected ItemProfile(Parcel in) {
        idCampaign = in.readInt();
        long tmpCreateDate = in.readLong();
        createDate = tmpCreateDate != -1 ? new Date(tmpCreateDate) : null;
        authorId = in.readInt();
        username = in.readString();
        tracking = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idCampaign);
        dest.writeLong(createDate != null ? createDate.getTime() : -1L);
        dest.writeInt(authorId);
        dest.writeString(username);
        dest.writeString(tracking);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ItemProfile> CREATOR = new Parcelable.Creator<ItemProfile>() {
        @Override
        public ItemProfile createFromParcel(Parcel in) {
            return new ItemProfile(in);
        }

        @Override
        public ItemProfile[] newArray(int size) {
            return new ItemProfile[size];
        }
    };

}
