package excal.rave.Assistance;

import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by hp on 1/13/2017.
 */

public class SetMusicPositionAsyncTask extends AsyncTask<Void, Void, String> {
    private Socket socket;
    private int position;

    public SetMusicPositionAsyncTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    protected String doInBackground(Void... voids) {
        DataInputStream din= null;
        try {
            din = new DataInputStream(socket.getInputStream());
            String s=din.readUTF();
            position = Integer.parseInt(s);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //TODO: set position on musicPlayer
    }
}
