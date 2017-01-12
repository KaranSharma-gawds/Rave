package excal.rave.Assistance;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import excal.rave.Assistance.DeviceDetailFragment;

/**
 * Created by hp on 1/11/2017.
 */

public class GetClients implements Runnable {


    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(DeviceDetailFragment.port_no));

            while(true){
                Socket socket = serverSocket.accept();
                DeviceDetailFragment.client_list.add(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
