package nl.mvdv.internetradio;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

import static nl.mvdv.internetradio.Util.toast;

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "MediaPlayerService";
    private MediaPlayer mediaPlayer;

    public static boolean running = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "MediaPlayer onError: " + what);
        mediaPlayer.reset();
        preparePlayer();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "player prepared");
        mp.start();
        Log.i(TAG, "player started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.Actions.STARTFOREGROUND_ACTION)) {
            Log.i(TAG, "Received Start Foreground Intent ");
            preparePlayer();
            showNotification();
        } else if (intent.getAction().equals(Constants.Actions.STOPFOREGROUND_ACTION)) {
            Log.i(TAG, "Received Stop Foreground Intent");
            releasePlayer();
            stopForeground(true);
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void preparePlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        try {
            mediaPlayer.setDataSource(this, Uri.parse(Constants.Urls.STREAM_URL));
            Log.i(TAG, "datasource set to : " + Constants.Urls.STREAM_URL);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            toast(this, "Audiospeler kan niet gestart worden");
            stopSelf();
        }
        mediaPlayer.prepareAsync();
        Log.i(TAG, "start preparing player");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        MediaPlayerService.running = running;
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.Actions.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("InternetRadio")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentText("Open de speler")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(Constants.NotificationIds.FOREGROUND_SERVICE,
                notification);
    }
}
