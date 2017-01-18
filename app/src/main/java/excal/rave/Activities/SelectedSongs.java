package excal.rave.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import excal.rave.Assistance.PlaySongs;
import excal.rave.Assistance.RecyclerAdapter;
import excal.rave.Assistance.RecyclerViewDivider;
import excal.rave.Assistance.Song;
import excal.rave.R;

public class SelectedSongs extends AppCompatActivity implements AdapterView.OnItemClickListener {
    List<Song> selectedSongs;
    private SparseArray<View> selectedViews;
    PlaySongs pSongs;
    View thisActivity;/*
    ArrayList<String> selectedSongs;
    ArrayList<String> selectedTitles;*/
    RecyclerView songsList;
    RecyclerAdapter adapter;
    private int currentPlaying = -1;
    private View selectedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_songs);
        thisActivity = findViewById(R.id.playlist);
        pSongs = new PlaySongs(this, thisActivity);
        songsList = (RecyclerView) findViewById(R.id.songs_list);

        pSongs.init();
        pSongs.setListeners();
        selectedSongs = SampleActivity.adapter.getList();
        adapter = new RecyclerAdapter(selectedSongs,this,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        adapter.setObject(pSongs);
        //selectedSongs = adapter.getList();
        pSongs.listToBePlayed(selectedSongs);
        songsList.setLayoutManager(mLayoutManager);
        songsList.setItemAnimator(new DefaultItemAnimator());
        songsList.setHasFixedSize(true);
        songsList.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        songsList.setItemAnimator(new DefaultItemAnimator());
        songsList.setAdapter(adapter);
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
