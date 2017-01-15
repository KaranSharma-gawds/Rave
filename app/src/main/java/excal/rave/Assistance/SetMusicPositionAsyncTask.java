package excal.rave.Assistance;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by hp on 1/13/2017.
 */

public class SetMusicPositionAsyncTask extends AsyncTask<Void, Void, String> {
    private static String Tag = "SetMusicPositionAsyncTask";
    private Socket socket;
    private int position;

    public SetMusicPositionAsyncTask(Socket socket, int p) {
        this.socket = socket;
        position = p;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected String doInBackground(Void... voids) {
        Log.v(Tag,"--position received: "+position);
//        DataInputStream din= null;
//        din = new DataInputStream(socket.getInputStream());
//            String s=din.readUTF();
        // cant do readUTF here.. coz parallely ClientSocket is waiting for readUTF

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
