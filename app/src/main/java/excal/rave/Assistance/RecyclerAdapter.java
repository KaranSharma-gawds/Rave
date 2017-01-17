package excal.rave.Assistance;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import excal.rave.R;

/**
 * Created by Karan on 17-01-2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerList> {
    List<Song> songList;
    public class RecyclerList extends RecyclerView.ViewHolder{
        public TextView songTitle;
        public RecyclerList(View view) {
            super(view);
            songTitle = (TextView) view.findViewById(R.id.title);
        }
    }
    public RecyclerAdapter(List<Song> songList){
        this.songList = songList;
    }
    @Override
    public RecyclerList onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_layout,parent,false);
        return new RecyclerList(view);
    }

    @Override
    public void onBindViewHolder(RecyclerList holder, int position) {
        Song song = songList.get(position);
        holder.songTitle.setText(song.getTitle());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
