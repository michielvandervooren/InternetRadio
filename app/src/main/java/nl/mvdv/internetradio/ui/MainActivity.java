package nl.mvdv.internetradio.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private ImageButton playButton;
    private ImageButton pauseButton;
    private Button linkButton;
    private String nowPlayingTxt;
    private String nextTxt;

    private Handler handler;
    private GetTrackInfoHandler getTrackInfoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        playButton = (ImageButton) findViewById(R.id.play_btn);
        pauseButton = (ImageButton) findViewById(R.id.pause_btn);
        linkButton = (Button) findViewById(R.id.goto_website);
        nowPlayingView = (TextView) findViewById(R.id.nowTextView);
        nextTrackView = (TextView) findViewById(R.id.nextTextView);

        // create the handler
        handler = new Handler();
        getTrackInfoHandler = new GetTrackInfoHandler(this, handler, 30 * 1000);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(MainActivity.this, MediaPlayerService.class);
                if (!MediaPlayerService.isRunning()) {
                    service.setAction(Constants.Actions.STARTFOREGROUND_ACTION);
                    MediaPlayerService.setRunning(true);
                    updateUI();
                } else {
                    service.setAction(Constants.Actions.STOPFOREGROUND_ACTION);
                    MediaPlayerService.setRunning(false);
                    updateUI();
                }
                startService(service);
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(MainActivity.this, MediaPlayerService.class);
                if (!MediaPlayerService.isRunning()) {
                    service.setAction(Constants.Actions.STARTFOREGROUND_ACTION);
                    MediaPlayerService.setRunning(true);
                    updateUI();
                } else {
                    service.setAction(Constants.Actions.STOPFOREGROUND_ACTION);
                    MediaPlayerService.setRunning(false);
                    updateUI();
                }
                startService(service);
            }
        });
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openWebsiteIntent = new Intent(Intent.ACTION_VIEW);
                openWebsiteIntent.setData(Uri.parse(Constants.Urls.WEB_URL));
                startActivity(openWebsiteIntent);
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
        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTrackInfoReceived(TrackInfo info) {
        if (info == null) {
            Util.toast(this, "Geen track informatie beschikbaar");
        } else {
            nowPlayingTxt = info.getNowPlaying();
            nextTxt = info.getNext();
        }
        updateUI();
    }

    private void updateUI() {
        if (MediaPlayerService.isRunning()) {
            playButton.setEnabled(false);
            pauseButton.setEnabled(true);
            nowPlayingView.setText(nowPlayingTxt);
            nextTrackView.setText(nextTxt);
        } else {
            playButton.setEnabled(true);
            pauseButton.setEnabled(false);
            nowPlayingView.setText("");
            nextTrackView.setText("");
        }
    }

    private void scheduleGetTrackInfo() {
        handler.post(getTrackInfoHandler);
    }

    private void cancelGetTrackInfo() {
        handler.removeCallbacks(getTrackInfoHandler);
    }

}
