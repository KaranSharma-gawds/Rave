package excal.rave.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import excal.rave.Assistance.PlaySongs;
import excal.rave.Assistance.RecyclerAdapter;
import excal.rave.Assistance.RecyclerViewDivider;
import excal.rave.Assistance.Song;
import excal.rave.R;

public class SelectedSongs extends AppCompatActivity {
    List<Song> selectedSongs;
    List<View> selectedViewsList;
    PlaySongs pSongs;
    View thisActivity;
    RecyclerView songsList;
    RecyclerAdapter adapter;
    // private int currentPlaying = -1; //made static
    private static View selectedView;
    private RecyclerView songs_list;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_songs);

        context = this;
        thisActivity = findViewById(R.id.playlist);


        pSongs = new PlaySongs(this, thisActivity);
        pSongs.init();
        pSongs.setListeners();
        selectedSongs = SampleActivity.adapter.getList();
        // selectedViewsList = SampleActivity.adapter.getSelectedViewsList();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        //selectedSongs = adapter.getList();
        pSongs.listToBePlayed(selectedSongs);

        adapter = new RecyclerAdapter(selectedSongs,this,this,2);
        adapter.setObject(pSongs);

        songsList = (RecyclerView) findViewById(R.id.songs_list);
        songsList.setLayoutManager(mLayoutManager);
        songsList.setItemAnimator(new DefaultItemAnimator());
        songsList.setHasFixedSize(true);
        songsList.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        songsList.setItemAnimator(new DefaultItemAnimator());
        songsList.setAdapter(adapter);




        //songs_list = (RecyclerView)findViewById(R.id.songs_list);
    }


/*
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        int prevPosition = pSongs.getPrevPosition();
        Log.v("DEBUG",position+"   ");
        Toast.makeText(context,"Previous Position is "+prevPosition,Toast.LENGTH_SHORT).show();
        if (pSongs.mp.isPlaying())   {
            if (prevPosition*//*currentPlaying*/

    @Override
    public void onBackPressed() {
        pSongs.mp.stop();
        super.onBackPressed();
    }/* == position) {
                pSongs.mp.pause();
            } else {
                pSongs.playSong(position);
                Toast.makeText(getApplicationContext(),"Previous Position is  "+prevPosition+"and current position is "+position,Toast.LENGTH_SHORT).show();
                songs_list.findViewHolderForAdapterPosition(prevPosition).itemView.setBackgroundResource(R.color.deselectedItem);
                view.setBackgroundResource(R.color.selectedItem);*//*
                view.setBackgroundResource(R.color.selectedItem);
                selectedView.setBackgroundResource(R.color.deselectedItem);*//*
            }
        } else {
            if (prevPosition*//*currentPlaying*//* == position) {
                pSongs.mp.start();
            } else {
                Toast.makeText(getApplicationContext(),"Previous Position is  "+prevPosition+"and current position is "+position,Toast.LENGTH_SHORT).show();
                view.setBackgroundResource(R.color.selectedItem);
                if(prevPosition!=-1)
                songs_list.findViewHolderForAdapterPosition(prevPosition).itemView.setBackgroundResource(R.color.deselectedItem);
            *//*    view.setBackgroundResource(R.color.selectedItem);*//*

                if(prevPosition != -1) {
                    selectedView.setBackgroundResource(R.color.deselectedItem);
                }

                pSongs.playSong(position);
            }
        }
        //prevPosition = position;
        Toast.makeText(context ,"setPreviousPosition Called",Toast.LENGTH_SHORT).show();
        pSongs.setPrevPosition(position);
       // selectedView = pSongs.getSelectedView();
    }*/
}
