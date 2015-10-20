package info.si2.iista.volunteernetworks.apiclient;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 2/10/15
 * Project: Virde
 */
public class ItemUser implements Parcelable {

    private int id;

    @SerializedName("contributions")
    private int nContributions;

    @SerializedName("nickname")
    private String username;

    @SerializedName("avatar")
    private String image;

    public ItemUser(int id, int nContributions, String username, String image) {
        this.id = id;
        this.nContributions = nContributions;
        this.username = username;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getnContributions() {
        return nContributions;
    }

    public void setnContributions(int nContributions) {
        this.nContributions = nContributions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    protected ItemUser(Parcel in) {
        id = in.readInt();
        nContributions = in.readInt();
        username = in.readString();
        image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(nContributions);
        dest.writeString(username);
        dest.writeString(image);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ItemUser> CREATOR = new Parcelable.Creator<ItemUser>() {
        @Override
        public ItemUser createFromParcel(Parcel in) {
            return new ItemUser(in);
        }

        @Override
        public ItemUser[] newArray(int size) {
            return new ItemUser[size];
        }
    };

}
