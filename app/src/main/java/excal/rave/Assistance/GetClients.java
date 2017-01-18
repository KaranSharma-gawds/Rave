package excal.rave.Assistance;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import excal.rave.Activities.Party;
import excal.rave.Assistance.DeviceDetailFragment;
import excal.rave.Assistance.ServerSocketSingleton;
/**
 * Created by hp on 1/11/2017.
 */

public class GetClients implements Runnable {
    private static String Tag = "GetClients";

    @Override
    public void run() {
        try {
     /*       ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(DeviceDetailFragment.port_no));
            ServerSocketSingleton.setSocket(serverSocket);
            Log.v(Tag,"ServerSocketmade");*/
            ServerSocket serverSocket = ServerSocketSingleton.getSocket();

            while(!Thread.currentThread().isInterrupted()){
                Socket socket = serverSocket.accept();
                DeviceDetailFragment.client_list.add(socket);
//                DataOutputStream dout=new DataOutputStream(socket.getOutputStream());
//                dout.writeUTF("Hello from server");
//                dout.flush();

                Log.v(Tag,"--Client connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Tag,"--serverSocket closed");
        }
    }
}
