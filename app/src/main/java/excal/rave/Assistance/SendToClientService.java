package excal.rave.Assistance;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
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
    private static String Tag = "SendToClientService";
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
            if(socket==null){
                Log.v(Tag,"--null socket");
                Toast.makeText(this, "null socket", Toast.LENGTH_SHORT).show();
                stopSelf();
                return;
            }

            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            try {
                OutputStream ostream = socket.getOutputStream();
                /*DataOutputStream dout=new DataOutputStream(ostream);
                dout.writeUTF("musicFile");
                dout.flush();*/

                ContentResolver cr = getApplicationContext().getContentResolver();
                InputStream istream = null;
                try {
                    istream = cr.openInputStream(Uri.parse(fileUri));
                } catch (FileNotFoundException e) {
                    Log.v(Tag,"--"+ e.toString());
                }
                Log.v(Tag,"--initiating sending(copyFile)");
                DeviceDetailFragment.copyFile(istream,ostream);
                String msg = "--Host: Data written to client "+socket.getLocalAddress().getHostAddress();
                Log.v(Tag, msg);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
