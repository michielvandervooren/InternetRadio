package nl.mvdv.internetradio.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nl.mvdv.internetradio.Constants;
import nl.mvdv.internetradio.trackinfo.GetTrackInfoHandler;
import nl.mvdv.internetradio.mediaplayer.MediaPlayerService;
import nl.mvdv.internetradio.R;
import nl.mvdv.internetradio.trackinfo.TrackInfo;
import nl.mvdv.internetradio.Util;

public class MainActivity extends AppCompatActivity implements GetTrackInfoHandler.OnTrackInfoReceivedListener {

    private static final String TAG = "MainActivity";

    private TextView nowPlayingView;
    private TextView nextTrackView;

    private Handler handler;
    private GetTrackInfoHandler getTrackInfoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playButton = (Button) findViewById(R.id.play_button);
        nowPlayingView = (TextView) findViewById(R.id.nowTextView);
        nextTrackView = (TextView) findViewById(R.id.nextTextView);

        // create the handler
        handler = new Handler();
        getTrackInfoHandler = new GetTrackInfoHandler(this, handler, 30 * 1000);

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
        cancelGetTrackInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scheduleGetTrackInfo();
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

    @Override
    public void onTrackInfoReceived(TrackInfo info) {
        if (info == null) {
            Util.toast(this, "Geen track informatie beschikbaar");
        } else {
            nowPlayingView.setText("Je luistert naar: " + info.getNowPlaying());
            nextTrackView.setText("Hierna: " + info.getNext());
        }
    }

    private void scheduleGetTrackInfo() {
        handler.post(getTrackInfoHandler);
    }

    private void cancelGetTrackInfo() {
        handler.removeCallbacks(getTrackInfoHandler);
    }

    private void showMoreInfo() {
        MoreInfoFragment moreInfoFragment = new MoreInfoFragment();
        moreInfoFragment.show(getSupportFragmentManager(), null);
    }
}
