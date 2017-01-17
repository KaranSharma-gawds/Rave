package excal.rave.Assistance;

import java.net.Socket;

/**
 * Created by hp on 1/11/2017.
 */

public class SocketSingleton {
    private static Socket socket=new Socket();

    public static void setSocket(Socket s){
        socket = s;
    }

    public static Socket getSocket(){
        return socket;
    }
}
