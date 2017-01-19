package excal.rave.Assistance;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import excal.rave.R;

/**
 * Created by Karan on 17-01-2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerList> {
    List<Song> songList;
    List<Song> selectedSongList;
    //static List<View> selectedViewsList;
    Context context;
    PlaySongs pSongs;
    int currentPlaying;
    AppCompatActivity activity;
    Fragment fragment;
    View selectedView;
    int activityId;
    public static final int SAMPLEACTIVITY= 1;
    public static final int SELECTEDSONGS = 1;

    public class RecyclerList extends RecyclerView.ViewHolder{
        public TextView songTitle;
        public View view;
        public RecyclerList(View view) {
            super(view);
            this.view = view;
            songTitle = (TextView) view.findViewById(R.id.title);

        }
    }
    public void setObject(PlaySongs pSongs){
        this.pSongs = pSongs;
    }
    public RecyclerAdapter(List<Song> songList, Context context, Fragment fragment, int activityId){
        this.songList = songList;
        this.context = context;
//        this.activity = activity;
        this.fragment = fragment;
        this.activityId = activityId;
        selectedView = new View(context);
    }
    public RecyclerAdapter(List<Song> songList, Context context, AppCompatActivity activity, int activityId){
        this.songList = songList;
        this.context = context;
        this.activity = activity;
        this.fragment = fragment;
        this.activityId = activityId;
        selectedView = new View(context);
    }

    @Override
    public RecyclerList onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_layout,parent,false);
        selectedSongList = new ArrayList<>();
       // selectedItems = new SparseBooleanArray();
        return new RecyclerList(view);
    }
    @Override
    public void onBindViewHolder(final RecyclerList holder, final int position) {
        final Song song = songList.get(position);
        holder.songTitle.setText(song.getTitle());
        if(activityId==1)
            holder.view.setBackgroundColor(song.isSelected() ? ContextCompat.getColor(context,R.color.selectedItem):ContextCompat.getColor(context,R.color.deselectedItem));
        else if(activityId == 2)
            holder.view.setBackgroundColor(ContextCompat.getColor(context,R.color.deselectedItem));
        holder.songTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                song.setSelected(!song.isSelected());
                if(activityId ==1) {
                    if (song.isSelected()) {
                        holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.selectedItem));
                        selectedSongList.add(song);
                        // SampleActivity.selectedViewsList.add(view);
                        //  Toast.makeText(context,"Added to selected Views",Toast.LENGTH_SHORT).show();
                    } else {
                        holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.deselectedItem));
                        selectedSongList.remove(song);
                        //SampleActivity.selectedViewsList.remove(view);
                        //  Toast.makeText(context,"Removed from selected Views",Toast.LENGTH_SHORT).show();
                    }

/*                    if (selectedSongList.size() == 0) {
                        ((SampleActivity) activity).getFab().setVisibility(View.GONE);
                    } else {
                        ((SampleActivity) activity).getFab().setVisibility(View.VISIBLE);
                    }*/
                } else if(activityId == 2 ) {
                    selectedView = pSongs.getPreviousView();
                    int prevPosition = pSongs.getPrevPosition();
                    Log.v("DEBUG", position + "   ");
                    Toast.makeText(context, "Previous Position is " + prevPosition, Toast.LENGTH_SHORT).show();
                    if (pSongs.mp.isPlaying()) {
                        if (prevPosition/*currentPlaying*/ == position) {
                            pSongs.mp.pause();
                        } else {
                            pSongs.playSong(position);
                            Toast.makeText(context, "Previous Position is  " + prevPosition + "and current position is " + position, Toast.LENGTH_SHORT).show();
                            selectedView.setBackgroundResource(R.color.deselectedItem);
//                            songs_list.findViewHolderForAdapterPosition(prevPosition).itemView.setBackgroundResource(R.color.deselectedItem);
                            view.setBackgroundResource(R.color.selectedItem);/*
                view.setBackgroundResource(R.color.selectedItem);
                selectedView.setBackgroundResource(R.color.deselectedItem);*/
                        }
                    } else {
                        if (prevPosition/*currentPlaying*/ == position) {
                            pSongs.mp.start();
                        } else {
                            Toast.makeText(context, "Previous Position is  " + prevPosition + "and current position is " + position, Toast.LENGTH_SHORT).show();
                            view.setBackgroundResource(R.color.selectedItem);
                            if (prevPosition != -1)
                                selectedView.setBackgroundResource(R.color.deselectedItem);
//                                findViewHolderForAdapterPosition(prevPosition).itemView.setBackgroundResource(R.color.deselectedItem);
                            view.setBackgroundResource(R.color.selectedItem);

                            if (prevPosition != -1) {
                                selectedView.setBackgroundResource(R.color.deselectedItem);
                            }

                            pSongs.playSong(position);
                        }
                    }
                    //prevPosition = position;
                    Toast.makeText(context, "setPreviousPosition Called", Toast.LENGTH_SHORT).show();
                    pSongs.setPrevPosition(position);
                    pSongs.setPreviousView(view);
                    // selectedView = pSongs.getSelectedView();
                }
                currentPlaying = position;
                selectedView = view;

            }
        });
    }
    public List<Song> getList(){
        return selectedSongList;
    }
    /*public List<View> getSelectedViewsList(){
       return SampleActivity.selectedViewsList;
   }*/
    @Override
    public int getItemCount() {
        return songList.size();
    }

}