package excal.rave.Assistance;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import excal.rave.Activities.SampleActivity;
import excal.rave.Activities.SelectedSongs;
import excal.rave.R;

/**
 * Created by Karan on 17-01-2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerList> {
    List<Song> songList;
    List<Song> selectedSongList;
    Context context;
    PlaySongs pSongs;
    int currentPlaying;
    AppCompatActivity activity;
    View selectedView;

    public class RecyclerList extends RecyclerView.ViewHolder{
        public TextView songTitle;
        public View view;
        public RecyclerList(View view) {
            super(view);
            this.view = view;
            songTitle = (TextView) view.findViewById(R.id.title);
        }
    }

    public RecyclerAdapter(List<Song> songList,Context context,AppCompatActivity activity){
        this.songList = songList;
        this.context = context;
        this.activity = activity;
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
        holder.view.setBackgroundColor(song.isSelected() ? ContextCompat.getColor(context,R.color.selectedItem):ContextCompat.getColor(context,R.color.deselectedItem));

        holder.songTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                song.setSelected(!song.isSelected());
                if(activity.getBaseContext().getClass() == SampleActivity.class) {
                    if (song.isSelected()) {
                        holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.selectedItem));
                        selectedSongList.add(song);
                    } else {
                        holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.deselectedItem));
                        selectedSongList.remove(song);
                    }

                    if (selectedSongList.size() == 0) {
                        ((SampleActivity) activity).getFab().setVisibility(View.GONE);
                    } else {
                        ((SampleActivity) activity).getFab().setVisibility(View.VISIBLE);
                    }
                }else{
                    if (pSongs.mp.isPlaying()) {
                        if (currentPlaying == position) {
                            pSongs.mp.pause();
                        } else {
                            pSongs.playSong(position);
                            view.setBackgroundResource(R.color.selectedItem);
                            selectedView.setBackgroundResource(R.color.deselectedItem);
                        }
                    } else {
                        if (currentPlaying == position) {
                            pSongs.mp.start();
                        } else {
                            view.setBackgroundResource(R.color.selectedItem);
                            if(currentPlaying != -1) {
                                selectedView.setBackgroundResource(R.color.deselectedItem);
                            }
                            pSongs.playSong(position);
                        }
                    }
                    currentPlaying = position;
                    selectedView = view;
                }

                }
            });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

}