package excal.rave.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import excal.rave.Assistance.PlaySongs;
import excal.rave.R;

public class SelectedSongs extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private SparseArray<View> selectedViews;
    PlaySongs pSongs;
    View thisActivity;
    ArrayList<String> selectedSongs;
    ArrayList<String> selectedTitles;
    ListView songsList;
    ArrayAdapter<String> adapter;
    private int currentPlaying = -1;
    private View selectedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_songs);
        thisActivity = findViewById(R.id.playlist);
        pSongs = new PlaySongs(this, thisActivity);
        songsList = (ListView) findViewById(R.id.songs_list);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        selectedSongs = bundle.getStringArrayList("selectedSongs");
        selectedTitles = bundle.getStringArrayList("selectedTitles");
        pSongs.init();
        pSongs.setListeners();
        pSongs.listToBePlayed(selectedSongs);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, selectedTitles);
        songsList.setAdapter(adapter);
        songsList.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (pSongs.mp.isPlaying()) {
            if (currentPlaying == i) {
                pSongs.mp.pause();
            } else {
                pSongs.playSong(i);
                view.setBackgroundResource(R.color.selectedItem);
                selectedView.setBackgroundResource(R.color.deselectedItem);
            }
        } else {
            if (currentPlaying == i) {
                pSongs.mp.start();
            } else {
                view.setBackgroundResource(R.color.selectedItem);
                if(currentPlaying != -1) {
                    selectedView.setBackgroundResource(R.color.deselectedItem);
                }
                pSongs.playSong(i);
            }
        }
        currentPlaying = i;
        selectedView = view;
    }
}
