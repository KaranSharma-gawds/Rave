package excal.rave.Assistance;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import excal.rave.R;

/**
 * Created by Karan on 14-01-2017.
 */

public class PlaySongs implements Runnable, View.OnClickListener,SeekBar.OnSeekBarChangeListener{
    public Button playButton, nextButton, previousButton;
    public MediaPlayer mp;
    private SeekBar seekBar;
    private Context context;
    private View activity;
    int position = -1;
    private ArrayList<String> selectedTitle;
    private ArrayList<String> allSongsData;
    private ArrayList<String> selectedData;
    private List<Song> playlist;
    public PlaySongs(Context context, View activity) {
        this.context = context;
        this.activity = activity;
    }

    public void init() {
        previousButton = (Button) activity.findViewById(R.id.prev_button);
        playButton = (Button) activity.findViewById(R.id.play_button);
        nextButton = (Button) activity.findViewById(R.id.next_button);
        seekBar = (SeekBar) activity.findViewById(R.id.seekBar);
        seekBar.setEnabled(true);
        seekBar.setProgress(0);
        mp = new MediaPlayer();
    }
    public void setListeners(){
        playButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }
    public void listToBePlayed(List<Song> playlist) {
        this.playlist = playlist;
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        try {
            if (mp.isPlaying() || mp != null) {
                if (b)
                    mp.seekTo(i);
            } else if (mp == null) {
                Toast.makeText(context, "Media is not running", Toast.LENGTH_SHORT).show();
                seekBar.setProgress(0);
            }
        } catch (Exception e) {
            seekBar.setEnabled(false);
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void run() {
        int currentPosition = mp.getCurrentPosition();
        int total = mp.getDuration();
        while (mp != null && currentPosition < total) {
            try {
                Thread.sleep(500);
                currentPosition = mp.getCurrentPosition();
            } catch (Exception e) {
                return;
            }
            seekBar.setProgress(currentPosition);
        }
    }

    public void playSong(int position) {
        mp.reset();
        this.position=position;
        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(playlist.get(position).getData());
            mp.prepare();
            mp.start();
            seekBar.setMax(mp.getDuration());
            Thread t = new Thread(this);
            t.start();
        } catch (Exception e) {
            Toast.makeText(context, "Error in playing song", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_button :{
                if(mp.isPlaying()){
                    playButton.setText("Resume");
                    mp.pause();
                } else {
                    playButton.setText("Pause");
                    mp.start();
                }
                break;
            }
            case R.id.next_button:{
                if((position+1)>=playlist.size())
                    position = -1;
                playSong(position++);
                break;
            }
            case R.id.prev_button :{
                if(position==0)
                   position = playlist.size();
                playSong(position-1);
                break;
            }
        }
    }
}
