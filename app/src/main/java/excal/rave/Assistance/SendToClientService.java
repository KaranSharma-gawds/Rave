package excal.rave.Assistance;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
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
    public static final String EXTRAS_MESSAGE_TYPE = "message_type";
    public static final String EXTRAS_FILE_PATH = "file_uri";
    public static final String EXTRAS_POSITION = "song_position";
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

            try {
                OutputStream ostream = socket.getOutputStream();
                String msgType = intent.getExtras().getString(EXTRAS_MESSAGE_TYPE);
                DataOutputStream dout=new DataOutputStream(ostream);
                dout.writeUTF(msgType);
                dout.flush();

                if(msgType.equals("musicFile")){
                    String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
                    Uri uri = Uri.parse(fileUri);
                    ContentResolver cr = getApplicationContext().getContentResolver();
                    InputStream istream = null;
                    long fileSize = 0;
                    try {
                        istream = cr.openInputStream(uri);
                        fileSize = istream.available();
                    } catch (FileNotFoundException e) {
                        Log.v(Tag,"--"+ e.toString());
                    }

                    //Output size of file to be sent
                    dout.writeLong(fileSize);
                    dout.flush();
                    //Output filename
                    String filename = getFileName(uri);
                    dout.writeUTF(filename);
                    dout.flush();


                    Log.v(Tag,"--initiating sending(copyFile)");
                    DeviceDetailFragment.copyFile(istream,ostream,fileSize);
                    String msg = "--Host: Data written to client "+socket.getRemoteSocketAddress().toString();
                    Log.v(Tag, msg);
                }else if(msgType.equals("position")){
                    String position = intent.getExtras().getString(EXTRAS_POSITION);
                    dout.writeUTF(position);
                    dout.flush();
                }else{
                    Log.v(Tag,"--error in messageType");
                }



            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
