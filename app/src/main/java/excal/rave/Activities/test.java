package excal.rave.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.Manifest;

import excal.rave.Assistance.PlaySongs;
import excal.rave.R;

public class test extends AppCompatActivity {
    ListView songsList;
    ArrayList<String> allSongTitles;
    ArrayList<String> allSongData;
    ArrayList<String> selectedSongTitles;
    ArrayList<String> selectedSongData;
    int count;
    View selectedView;
    SparseArray<View> selectedViews;
    PlaySongs pSongs;
    Cursor cursor;
    public boolean[] itemState;
    View thisActivity;
    private boolean selectionMode = false;
    int currentPlaying = -1;
    Button gotoNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        /*Uri uri = Uri.parse("content://media/external/images/media");
        String providers = "com.android.providers.media.MediaProvider";
        grantUriPermission(providers,uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);*/
        seekPermission();
        songsList = (ListView) findViewById(R.id.songs_list);
        selectedViews = new SparseArray<View>();
        thisActivity = (View) findViewById(R.id.activity_main2);
        pSongs = new PlaySongs(this, thisActivity);
        pSongs.init();
        pSongs.setListeners();
        gotoNext = (Button) findViewById(R.id.goto_next);
        gotoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : send these files to the peers
                Intent intent = new Intent(getApplicationContext() ,SelectedSongs.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("selectedSongs",selectedSongData);
                bundle.putStringArrayList("selectedTitles",selectedSongTitles);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        selectedSongTitles = new ArrayList<String>();
        selectedSongData = new ArrayList<String>();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadSongs();
                    pSongs.listToBePlayed(allSongData);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                            android.R.id.text1, allSongTitles) {
                        @NonNull
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            itemState[position] = false;
                            return super.getView(position, convertView, parent);
                        }
                    };
                    songsList.setAdapter(adapter);
                    //on long press on a view reverse the selection state of the view
                    //set selection code on the onItemClickListener active
                    //TODO: make a counter for the number of views selected
                    //use this counter's count to determine whether the code for selection is active or not
                    songsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                            selectionMode = true;
                            if (pSongs.mp.isPlaying()) {
                                pSongs.mp.stop();
                            }
                            if(currentPlaying != -1) {
                                selectedView.setBackgroundResource(R.color.deselectedItem);
                            }
                            if (itemState[i]) {
                                selectedViews.remove(i);
                                selectedSongData.remove(allSongData.get(i));
                                selectedSongTitles.remove(allSongTitles.get(i));
                                itemState[i]=false;
                                view.setBackgroundResource(R.color.deselectedItem);
                            } else {
                                itemState[i]=true;
                                selectedViews.append(i,view);
                                selectedSongData.add(allSongData.get(i));
                                selectedSongTitles.add(allSongTitles.get(i));
                                view.setBackgroundResource(R.color.selectedItem);
                            }
                            return true;
                        }
                    });

                    songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (selectionMode) {
                                if (itemState[i]) {
                                    view.setBackgroundResource(R.color.deselectedItem);
                                    selectedViews.remove(i);
                                    selectedSongData.remove(allSongData.get(i));
                                    selectedSongTitles.remove(allSongTitles.get(i));
                                    itemState[i]=false;
                                    Toast.makeText(getApplicationContext(), "2nd case", Toast.LENGTH_SHORT).show();
                                } else {
                                    view.setBackgroundResource(R.color.selectedItem);
                                    selectedViews.append(i, view);
                                    selectedSongData.add(allSongData.get(i));
                                    selectedSongTitles.add(allSongTitles.get(i));
                                    itemState[i]=true;
                                    Toast.makeText(getApplicationContext(), "3rd case", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (pSongs.mp.isPlaying() && currentPlaying == i) {
                                    pSongs.mp.pause();
                                } else if ((!pSongs.mp.isPlaying()) && currentPlaying == i) {
                                    pSongs.mp.start();

                                } else if (currentPlaying != i) {
                                    view.setBackgroundResource(R.color.selectedItem);
                                    pSongs.playSong(i);
                                    if(currentPlaying != -1){
                                        selectedView.setBackgroundResource(R.color.deselectedItem);
                                    }

                                }
                            }
                            currentPlaying = i;
                            selectedView = view;
                        }
                    });
                } else {
                    Toast.makeText(this, "permission not granted", Toast.LENGTH_LONG).show();
                }

            }

        }
    }

    @Override
    public void onBackPressed() {
        if (selectionMode) {
            setSelectionModeOff();
        } else {
            super.onBackPressed();
        }
    }

    public void seekPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    public void setSelectionModeOff() {
        //layout change
        count = 0;
        selectionMode = false;
        for (int i = 0; i < selectedViews.size(); i++) {
            itemState[selectedViews.keyAt(i)] = false;
            selectedViews.valueAt(i).setBackgroundResource(R.color.deselectedItem);
        }
        selectedViews.clear();
    }

    @SuppressWarnings("deprecation")
    public void loadSongs() {
        int i = 0;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA};
        cursor = this.managedQuery(uri, projection, null, null, null);
        allSongTitles = new ArrayList<String>();
        allSongData = new ArrayList<String>();
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        do {
            allSongTitles.add(cursor.getString(0));
            allSongData.add(cursor.getString(column_index));
        } while (cursor.moveToNext());
        //cursor.close();
        itemState = new boolean[allSongTitles.size()];
    }


}
