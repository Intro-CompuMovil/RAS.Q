import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class KeyMarker(
    @SerializedName("latitude") @Expose var latitude: Double? = null,
    @SerializedName("longitude") @Expose var longitude: Double? = null,
    @SerializedName("name") @Expose var name: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<KeyMarker> {
        override fun createFromParcel(parcel: Parcel): KeyMarker {
            return KeyMarker(parcel)
        }

        override fun newArray(size: Int): Array<KeyMarker?> {
            return arrayOfNulls(size)
        }
    }
}

