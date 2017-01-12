package excal.rave.Assistance;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import excal.rave.Activities.Party;
import excal.rave.R;

/**
 * Created by hp on 1/13/2017.
 */

public class SaveMusicAsyncTask extends AsyncTask<Void, Void, String> {
    private String Tag = "SaveMusicAsyncTask";
    private Socket socket;
    private Activity activity;

    public SaveMusicAsyncTask(Socket socket, Activity activity) {
        this.socket = socket;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {

        InputStream istream = null;
        try {
            istream = socket.getInputStream();
            final File f = new File(Environment.getExternalStorageDirectory() + "/Music/"+activity.getResources().getString(R.string.app_name)
                   + "/rave-" + System.currentTimeMillis() + ".jpg");
            //TODO: save as music mp3

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();

            Log.v(Tag, "server: copying files " + f.toString());
            boolean copied = DeviceDetailFragment.copyFile(istream,new FileOutputStream(f));
            Log.v(Tag,copied?"copied":"not copied");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // for viewing image
        if (result != null) {
            Log.v(Tag,"File copied - " + result);
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
            activity.startActivity(intent);
        }else{
            Log.v(Tag,"null result");
        }
    }
}
