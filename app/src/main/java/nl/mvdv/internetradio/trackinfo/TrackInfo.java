package nl.mvdv.internetradio.trackinfo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by voorenmi on 19-11-2015.
 */
public class TrackInfo implements Parcelable {

    private String nowPlaying;
    private String next;
    private int bitRate;
    private int currentListeners;
    private int maxListeners;

    public TrackInfo() {
        super();
    }

    public TrackInfo(Parcel in) {
        nowPlaying = in.readString();
        next = in.readString();
        bitRate = in.readInt();
        currentListeners = in.readInt();
        maxListeners = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(nowPlaying);
        parcel.writeString(next);
        parcel.writeInt(bitRate);
        parcel.writeInt(currentListeners);
        parcel.writeInt(maxListeners);
    }

    public static final Parcelable.Creator<TrackInfo> CREATOR = new Parcelable.Creator<TrackInfo>() {
        public TrackInfo createFromParcel(Parcel pc) {
            return new TrackInfo(pc);
        }
        public TrackInfo[] newArray(int size) {
            return new TrackInfo[size];
        }
    };

    public String getNowPlaying() {
        return nowPlaying;
    }

    public void setNowPlaying(String nowPlaying) {
        this.nowPlaying = nowPlaying;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getCurrentListeners() {
        return currentListeners;
    }

    public void setCurrentListeners(int currentListeners) {
        this.currentListeners = currentListeners;
    }

    public int getMaxListeners() {
        return maxListeners;
    }

    public void setMaxListeners(int maxListeners) {
        this.maxListeners = maxListeners;
    }
}
