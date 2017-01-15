package excal.rave.Assistance;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

import excal.rave.Activities.Party;
import excal.rave.R;

/**
 * Created by hp on 1/11/2017.
 */

public class ClientSocket implements Runnable {
    private String Tag = "ClientSocket";
    private String serverAddress;
    private Activity activity;
    private static final int SOCKET_TIMEOUT = 5000;

    public ClientSocket(String hostAddress, Activity act) {
        serverAddress =  hostAddress;
        activity = act;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(serverAddress,DeviceDetailFragment.port_no), SOCKET_TIMEOUT);
            DeviceDetailFragment.MyIpAddress_client = socket.getLocalAddress().getHostAddress();
            generateToast("MyIpAddress: "+DeviceDetailFragment.MyIpAddress_client);

            DataInputStream din=new DataInputStream(socket.getInputStream());
            String s = null;
//            String s=din.readUTF();
//            Log.v(Tag,"--"+s);
//            generateToast(s);


            while (true){
                s=din.readUTF();
                Log.v(Tag,"received "+s);
                if(s.equals("position")){
                    s=din.readUTF();
                    int position = Integer.parseInt(s);
                    new SetMusicPositionAsyncTask(socket,position).execute();
                }else if(s.equals("musicFile")){
                    //fileSize
                    s=din.readUTF();
                    long fileSize = Long.parseLong(s);
                    //fileName
                    s=din.readUTF();

                    new SaveMusicAsyncTask(socket,activity,fileSize,s).execute();
                }else if(s.equals("nextSong")){

                }else{
                    Log.v(Tag,"--unexpected data: "+s);
                }
            }


//            new SaveMusicAsyncTask(socket,activity).execute();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateToast(final String str){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity.getApplicationContext(), str!=null?str:"null", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
