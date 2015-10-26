package info.si2.iista.volunteernetworks.apiclient;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 26/10/15
 * Project: Virde
 */
public class ItemTopUser implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("avatar")
    private String image;

    public ItemTopUser(int id, String image) {
        this.id = id;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    protected ItemTopUser(Parcel in) {
        id = in.readInt();
        image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(image);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ItemTopUser> CREATOR = new Parcelable.Creator<ItemTopUser>() {
        @Override
        public ItemTopUser createFromParcel(Parcel in) {
            return new ItemTopUser(in);
        }

        @Override
        public ItemTopUser[] newArray(int size) {
            return new ItemTopUser[size];
        }
    };

}
