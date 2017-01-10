package excal.rave.Activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import excal.rave.R;

public class MusicListActivity extends AppCompatActivity {

    private static final String LOGGING_TAG = "";
    ArrayList<String> songList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
    }
    public void bringMusic() {
        // Get a cursor over all playlists.
        final ContentResolver resolver = this.getContentResolver();
        final Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        final String idKey = MediaStore.Audio.Playlists._ID;
        final String nameKey = MediaStore.Audio.Playlists.NAME;
        Toast.makeText(this, "Before declaring string", Toast.LENGTH_SHORT).show();
        final String[] columns = { idKey/* nameKey */};
        final Cursor playLists = resolver.query(uri, columns, null, null, null);

        Toast.makeText(this,"After resolving Query",Toast.LENGTH_SHORT).show();
        if (playLists == null) {
            Log.e(LOGGING_TAG, "Found no playlists.");
            Toast.makeText(this,"null playlist",Toast.LENGTH_SHORT).show();
            return;
        }

        // Log a list of the playlists.
        Log.i(LOGGING_TAG, "Playlists:");
        String playListName = null;
        if(playLists.moveToFirst()) {
            for (boolean hasItem = playLists.moveToFirst(); hasItem; hasItem = playLists.moveToNext()) {
                playListName = playLists.getString(playLists.getColumnIndex(nameKey));
                Log.i(LOGGING_TAG, playListName);
                songList.add(playListName);
            }

            // Play the first song from the first playlist.
            playLists.moveToFirst();
            final long playlistID = playLists.getLong(playLists.getColumnIndex(idKey));
            this.playTrackFromPlaylist(playlistID);

            // Close the cursor.
            if (playLists != null) {
                playLists.close();
            }
        }
    }
    public static void playAudio(final String path) {
        final MediaPlayer player = new MediaPlayer();
        if (path == null) {
            Log.e(LOGGING_TAG, "Called playAudio with null data stream.");
            return;
        }
        try {
            player.setDataSource(path);
            player.prepare();
            player.start();
        } catch (Exception e) {
            Log.e(LOGGING_TAG, "Failed to start MediaPlayer: " + e.getMessage());
            return;
        }
    }
    /**
     * Play the first track on the specified playlist.
     *
     * @param playListID
     *            from the MediaStore database
     */
    public void playTrackFromPlaylist(final long playListID) {
        final ContentResolver resolver = this.getContentResolver();
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playListID);
        final String dataKey = MediaStore.Audio.Media.DATA;
        Cursor tracks = resolver.query(uri, new String[] { dataKey }, null, null, null);
        if (tracks != null) {
            tracks.moveToFirst();
            final int dataIndex = tracks.getColumnIndex(dataKey);
            final String dataPath = tracks.getString(dataIndex);
            MusicListActivity.playAudio(dataPath);
            tracks.close();
        }
    }
}
