package excal.rave.Assistance;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
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
    private long fileSize;
    private String fileName;

    public SaveMusicAsyncTask(Socket socket, Activity activity, long fileSize, String fileName) {
        this.socket = socket;
        this.activity = activity;
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {

        InputStream istream = null;
        boolean copied = false;
        String path = null;
        try {
            istream = socket.getInputStream();
            final File f = new File(Environment.getExternalStorageDirectory() + "/Music/"+activity.getResources().getString(R.string.app_name)
                    + "/" + fileName);
//                   + "/rave-" + System.currentTimeMillis() + ".mp3");
            path = f.toString();

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();

            Log.v(Tag, "copying music file " + path);
            copied = DeviceDetailFragment.copyFile(istream,new FileOutputStream(f), fileSize);
            Log.v(Tag,copied?"copied":"not copied");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return copied?fileName:"not copied";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //
        if (result != null) {
            Log.v(Tag,"File copied - " + result);
            /*Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
            activity.startActivity(intent);*/
            ClientSocket.isFileCopied = true;
            Toast.makeText(activity, "music file saved: "+result, Toast.LENGTH_SHORT).show();
        }else{
            Log.v(Tag,"null result");
        }
    }
}
