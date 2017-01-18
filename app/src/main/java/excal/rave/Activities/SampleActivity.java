package excal.rave.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeTransform;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import excal.rave.Assistance.RecyclerAdapter;
import excal.rave.Assistance.RecyclerViewDivider;
import excal.rave.Assistance.Song;
import excal.rave.R;

import static excal.rave.R.id.fab;

public class SampleActivity extends Fragment {
    private static String TAG = "SampleActivity";
    RecyclerView listView;
    List<Song> songs;
    Cursor cursor;
    static RecyclerAdapter adapter;
    ActionMode actionMode;
    GestureDetectorCompat gestureDetector;
    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_sample,container, false);


        songs = new ArrayList<>();
        listView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        adapter = new RecyclerAdapter(songs,getActivity().getApplicationContext(),this,1);
        int version = Build.VERSION.SDK_INT;
        if(version<23){
            loadSongs();
        }else {
            seekPermission();
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setHasFixedSize(true);
        listView.addItemDecoration(new RecyclerViewDivider(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL));
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(adapter);
        return rootView;
    }

    public void seekPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            }
        } else {
            Toast.makeText(getActivity(), "permission not granted", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("deprecation")
    public void loadSongs() {
        int i = 0;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA};
        cursor = getActivity().managedQuery(uri, projection, null, null, null);
        Log.v(TAG,"--"+cursor.getCount());
        Song song ;
        /*allSongTitles = new ArrayList<String>();
        allSongData = new ArrayList<String>();*/
        cursor.moveToFirst();
        //int x = 0;
        int column_index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        do {
            song = new Song();
            song.setTitle(cursor.getString(0));
            song.setData(cursor.getString(column_index));
            songs.add(song);
        } while (cursor.moveToNext());
        adapter.notifyDataSetChanged();
    }

    public FloatingActionButton getFab(){
        return fab;
    }
}
