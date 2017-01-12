package excal.rave.Assistance;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ContentHandler;
import java.net.Socket;

import excal.rave.Activities.Party;

/**
 * Created by hp on 1/11/2017.
 */

public class SendToClientService extends IntentService {
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "excal.rave.SEND_FILE";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_SERVER_TO_CLIENT_SOCKET = "socket";

    public SendToClientService(String name) { super(name); }

    public SendToClientService(){ super("SendToClientService");}

    @Override
    protected void onHandleIntent(Intent intent) {

        if(intent.getAction().equals(ACTION_SEND_FILE)){

            Socket socket = SocketSingleton.getSocket();
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);

            try {
                OutputStream ostream = socket.getOutputStream();
                ContentResolver cr = getApplicationContext().getContentResolver();
                InputStream istream = null;
                try {
                    istream = cr.openInputStream(Uri.parse(fileUri));
                } catch (FileNotFoundException e) {
                    Log.d(Party.TAG, e.toString());
                }
                DeviceDetailFragment.copyFile(istream,ostream);
                String msg = "Host: Data written to client "+socket.getLocalAddress().getHostAddress();
                Log.d(Party.TAG, msg);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
