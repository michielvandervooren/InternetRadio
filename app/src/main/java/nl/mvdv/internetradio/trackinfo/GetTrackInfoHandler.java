package nl.mvdv.internetradio.trackinfo;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import nl.mvdv.internetradio.Constants;
import nl.mvdv.internetradio.Util;
import nl.mvdv.internetradio.ui.MainActivity;

/**
 * Created by voorenmi on 20-11-2015.
 */
public class GetTrackInfoHandler implements Runnable {

    private Handler handler;
    private long delay;
    private OnTrackInfoReceivedListener listener;

    public GetTrackInfoHandler(OnTrackInfoReceivedListener listener, Handler handler, long delay) {
        this.handler = handler;
        this.delay = delay;
        this.listener = listener;
    }
    @Override
    public void run() {
        //reschedule
        handler.postDelayed(this, delay);
        new GetTrackInfoTask().execute();
    }

    private class GetTrackInfoTask extends AsyncTask<Void, Void, TrackInfo> {

        private static final String TAG = "GetTrackInfoTask";

        @Override
        protected TrackInfo doInBackground(Void... params) {
            TrackInfo result = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(Constants.Urls.INFO_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("User-Agent","Mozilla compatible");
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result = parse(in);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        private TrackInfo parse(InputStream in) throws IOException {
            TrackInfo result = new TrackInfo();
            BufferedReader input = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                builder.append(line).append(" ");
            }
            input.close();
            // <HTML><meta http-equiv="Pragma" content="no-cache"></head><body>0,1,7,100,0,128, Nu op Radio De Blauwe Tegel: Denans - Zoveel duizenden vrouwen Straks hoort u:  Mark Rijs - Doenja</body></html>
            String responseBody = builder.toString();
            Log.d(TAG,"responseBody: " + responseBody);
            int startIdx = responseBody.indexOf("<body>") + 6;
            int endIdx = responseBody.indexOf("</body>");
            String infoContent = responseBody.substring(startIdx, endIdx);
            Log.d(TAG, "infoContent: " + infoContent);
            String[] infoArray = infoContent.split(",");
            // join strings from offset 6, because that's a comma in the content
            StringBuilder nowAndNext = new StringBuilder();
            for (int i = 0; i < infoArray.length; i++) {
                Log.d(TAG, "i: " + i + "= " + infoArray[i]);
                switch (i) {
                    case Constants.ShoutcastFields.CURRENT_LISTENERS:
                        Log.d(TAG, "currentListeners");
                        result.setCurrentListeners(Integer.parseInt(infoArray[i]));
                        break;
                    case Constants.ShoutcastFields.STATUS:
                    case Constants.ShoutcastFields.LISTENER_PEAK:
                    case Constants.ShoutcastFields.REPORTED_LISTENERS:
                        Log.d(TAG, "status,listenerPeak,reportedListeners");
                        break;
                    case Constants.ShoutcastFields.MAX_LISTENERS:
                        Log.d(TAG, "maxListeners");
                        result.setMaxListeners(Integer.parseInt(infoArray[i]));
                        break;
                    case Constants.ShoutcastFields.BITRATE:
                        Log.d(TAG, "bitRate");
                        result.setBitRate(Integer.parseInt(infoArray[i]));
                        break;
                    case Constants.ShoutcastFields.TRACK_INFO:
                        Log.d(TAG, "trackInfo");
                        nowAndNext.append(infoArray[i].trim());
                        break;
                    default:
                        Log.d(TAG, "trackInfo(default)");
                        nowAndNext.append(infoArray[i].trim());

                }
            }
            String nowAndNextString = nowAndNext.toString();
            Log.d(TAG,"nowAndNextString: " + nowAndNextString);
            int startIdxNow = nowAndNextString.indexOf("Nu op Radio De Blauwe Tegel:") + 28;
            int endIdxNow = nowAndNextString.indexOf("Straks hoort u:");
            int startIdxNext = endIdxNow + 15;

            result.setNowPlaying(nowAndNextString.substring(startIdxNow, endIdxNow));
            result.setNext(nowAndNextString.substring(startIdxNext));

            return result;
        }

        @Override
        protected void onPostExecute(TrackInfo info) {
            super.onPostExecute(info);
            listener.onTrackInfoReceived(info);
        }
    }

    public static interface OnTrackInfoReceivedListener {

        void onTrackInfoReceived(TrackInfo trackInfo);
    }
}
