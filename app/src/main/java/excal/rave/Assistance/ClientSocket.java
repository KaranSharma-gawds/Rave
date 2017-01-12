package excal.rave.Assistance;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
    private String serverAddress;
    private Context context;
    private static final int SOCKET_TIMEOUT = 5000;

    public ClientSocket(String hostAddress, Context c) {
        serverAddress =  hostAddress;
        context = c;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(serverAddress,DeviceDetailFragment.port_no), SOCKET_TIMEOUT);
            DeviceDetailFragment.MyIpAddress_client = socket.getLocalAddress().getHostAddress();
//            DeviceDetailFragment.setIpOnView();

            while (true){
                InputStream istream = socket.getInputStream();
                if(istream.available()!=0){
                    final File f = new File(Environment.getExternalStorageDirectory() + "/"
                            + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis() + ".jpg");

                    File dirs = new File(f.getParent());
                    if (!dirs.exists())
                        dirs.mkdirs();
                    f.createNewFile();

                    Log.d(Party.TAG, "server: copying files " + f.toString());
                    Toast.makeText(context, "receiving files", Toast.LENGTH_SHORT).show();
                    DeviceDetailFragment.copyFile(istream,new FileOutputStream(f));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
