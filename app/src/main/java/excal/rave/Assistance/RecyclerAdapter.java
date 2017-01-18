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

import excal.rave.R;

/**
 * Created by Karan on 17-01-2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerList> {
    List<Song> songList;
    private SparseBooleanArray selectedItems;
    Context context;

    public class RecyclerList extends RecyclerView.ViewHolder{
        public TextView songTitle;
        public View view;
        public RecyclerList(View view) {
            super(view);
            this.view = view;
            songTitle = (TextView) view.findViewById(R.id.title);
        }
    }

    public RecyclerAdapter(List<Song> songList,Context context){
        this.songList = songList;
        this.context = context;

    }
    @Override
    public RecyclerList onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_layout,parent,false);
       // selectedItems = new SparseBooleanArray();
        return new RecyclerList(view);
    }
    @Override
    public void onBindViewHolder(final RecyclerList holder, int position) {
        final Song song = songList.get(position);
        holder.songTitle.setText(song.getTitle());
        AppCompatActivity activity;
        holder.view.setBackgroundColor(song.isSelected() ? ContextCompat.getColor(context,R.color.selectedItem) : ContextCompat.getColor(context,R.color.deselectedItem));
        holder.songTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                song.setSelected(!song.isSelected());
                holder.view.setBackgroundColor(song.isSelected() ? ContextCompat.getColor(context,R.color.selectedItem):ContextCompat.getColor(context,R.color.deselectedItem));
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }
    public int getSelectedItemCount(){
        return selectedItems.size();
    }
    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

}
