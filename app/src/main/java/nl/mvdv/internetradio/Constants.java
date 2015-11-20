package nl.mvdv.internetradio;

/**
 * Created by voorenmi on 17-11-2015.
 */
public class Constants {

    public interface Actions {
        public static final String MAIN_ACTION = "nl.mvdv.internetradio.action.main";
        public static final String STARTFOREGROUND_ACTION = "nl.mvdv.internetradio.action.startforeground";
        public static final String STOPFOREGROUND_ACTION = "nl.mvdv.internetradio.action.stopforeground";
        public static final String SEND_TRACK_INFO_ACTION = "nl.mvdv.internetradio.action.sendtrackinfo";
        public static final String GET_TRACK_INFO_ACTION = "nl.mvdv.internetradio.action.gettrackinfo";
    }

    public interface NotificationIds {
        public static final int FOREGROUND_SERVICE = 101;
    }

    public interface Urls {
        public static final String STREAM_URL = "http://radioheiloo.no-ip.info:8000";
        public static final String WEB_URL = "http://www.radiodeblauwetegel.nl";
        public static final String INFO_URL = "http://radioheiloo.no-ip.info:8000/7.html";
    }

    public interface Extras {
        public static final String TRACK_INFO = "nl.mvdv.internetradio.extra.trackinfo";
    }

    public interface ShoutcastFields {
        public static final int CURRENT_LISTENERS = 0;
        public static final int STATUS = 1;
        public static final int LISTENER_PEAK = 2;
        public static final int MAX_LISTENERS = 3;
        public static final int REPORTED_LISTENERS = 4;
        public static final int BITRATE = 5;
        public static final int TRACK_INFO = 6;
    }
}
