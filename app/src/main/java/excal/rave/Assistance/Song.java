package excal.rave.Assistance;

/**
 * Created by Karan on 17-01-2017.
 */

public class Song {
    private String title;
    private String data;
    private boolean isSelected = false;
    public Song(){}
    public String getTitle(){
        return this.title;
    }
    public String getData() {
        return this.data;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setData(String data){
        this.data = data;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public boolean isSelected() {
        return isSelected;
    }
}
