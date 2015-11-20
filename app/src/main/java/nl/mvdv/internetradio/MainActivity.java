package nl.mvdv.internetradio;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BroadcastReceiver trackInfoReceiver;
    private PendingIntent getTrackInfoIntent;

    private Button playButton;
    private TextView nowPlayingView;
    private TextView nextTrackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = (Button) findViewById(R.id.play_button);
        nowPlayingView = (TextView) findViewById(R.id.nowTextView);
        nextTrackView = (TextView) findViewById(R.id.nextTextView);

        // create the TrackInfoReceiver
        trackInfoReceiver = createTrackInfoReceiver();

        // create the pendingIntent for the TrackInfoService
        getTrackInfoIntent = createGetTrackInfoPendingIntent();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                Intent service = new Intent(MainActivity.this, MediaPlayerService.class);
                if (!MediaPlayerService.isRunning()) {
                    service.setAction(Constants.Actions.STARTFOREGROUND_ACTION);
                    MediaPlayerService.setRunning(true);
                    button.setText(R.string.pause_txt);
                } else {
                    service.setAction(Constants.Actions.STOPFOREGROUND_ACTION);
                    MediaPlayerService.setRunning(false);
                    button.setText(R.string.play_txt);
                }
                startService(service);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelAlarm();
        unregisterTrackInfoReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerAlarm();
        registerTrackInfoReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // The menu xml contains a single action: action_more_info
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_more_info) {
            // display the more info dialog
            showMoreInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerAlarm() {
        getAlarmManager().setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.uptimeMillis(), 30 * 1000, getTrackInfoIntent);
    }

    private void cancelAlarm() {
        getAlarmManager().cancel(getTrackInfoIntent);
    }

    private BroadcastReceiver createTrackInfoReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final TrackInfo info = intent.getParcelableExtra(Constants.Extras.TRACK_INFO);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (info == null) {
                            Util.toast(MainActivity.this, "Geen track informatie beschikbaar");
                        } else {
                            nowPlayingView.setText(info.getNowPlaying());
                            nextTrackView.setText(info.getNext());
                        }
                    }
                });

            }
        };
    }

    private void registerTrackInfoReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(trackInfoReceiver, new IntentFilter(Constants.Actions.SEND_TRACK_INFO_ACTION));
    }

    private void unregisterTrackInfoReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(trackInfoReceiver);
    }

    private void showMoreInfo() {
        MoreInfoFragment moreInfoFragment = new MoreInfoFragment();
        moreInfoFragment.show(getSupportFragmentManager(), null);
    }

    private PendingIntent createGetTrackInfoPendingIntent() {
        Intent serviceIntent = new Intent(this, TrackInfoService.class);
        return PendingIntent.getService(this, 0, serviceIntent, 0);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) getSystemService(ALARM_SERVICE);
    }

}
