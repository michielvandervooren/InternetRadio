package nl.mvdv.internetradio;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TrackInfoService extends Service {

    private static final String TAG = "TrackInfoService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new GetTrackInfoTask().execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void sendBroadcast(TrackInfo info){
        Intent intent = new Intent(Constants.Actions.SEND_TRACK_INFO_ACTION);
        intent.putExtra(Constants.Extras.TRACK_INFO, info);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private class GetTrackInfoTask extends AsyncTask<Void, Void, TrackInfo> {

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
            Log.i(TAG,"responseBody: " + responseBody);
            int startIdx = responseBody.indexOf("<body>") + 6;
            int endIdx = responseBody.indexOf("</body>");
            String infoContent = responseBody.substring(startIdx, endIdx);
            Log.i(TAG,"infoContent: " + infoContent);
            String[] infoArray = infoContent.split(",");
            // join strings from offset 6, because that's a comma in the content
            StringBuilder nowAndNext = new StringBuilder();
            for (int i = 0; i < infoArray.length; i++) {
                Log.i(TAG, "i: " + i + "= " + infoArray[i]);
                switch (i) {
                    case Constants.ShoutcastFields.CURRENT_LISTENERS:
                        Log.i(TAG, "currentListeners");
                        result.setCurrentListeners(Integer.parseInt(infoArray[i]));
                        break;
                    case Constants.ShoutcastFields.STATUS:
                    case Constants.ShoutcastFields.LISTENER_PEAK:
                    case Constants.ShoutcastFields.REPORTED_LISTENERS:
                        Log.i(TAG, "status,listenerPeak,reportedListeners");
                        break;
                    case Constants.ShoutcastFields.MAX_LISTENERS:
                        Log.i(TAG, "maxListeners");
                        result.setMaxListeners(Integer.parseInt(infoArray[i]));
                        break;
                    case Constants.ShoutcastFields.BITRATE:
                        Log.i(TAG, "bitRate");
                        result.setBitRate(Integer.parseInt(infoArray[i]));
                        break;
                    case Constants.ShoutcastFields.TRACK_INFO:
                        Log.i(TAG, "trackInfo");
                        nowAndNext.append(infoArray[i].trim());
                        break;
                    default:
                        Log.i(TAG, "trackInfo(default)");
                        nowAndNext.append(infoArray[i].trim());

                }
            }
            String nowAndNextString = nowAndNext.toString();
            Log.i(TAG,"nowAndNextString: " + nowAndNextString);
            int startIdxNow = nowAndNextString.indexOf("Nu op Radio De Blauwe Tegel:") + 28;
            int endIdxNow = nowAndNextString.indexOf("Straks hoort u:");
            int startIdxNext = endIdxNow + 15;

            result.setNowPlaying(nowAndNextString.substring(startIdxNow, endIdxNow));
            result.setNext(nowAndNextString.substring(startIdxNext));

            return result;
        }

        @Override
        protected void onPostExecute(TrackInfo info) {
            sendBroadcast(info);
            super.onPostExecute(info);
        }
    }
}
