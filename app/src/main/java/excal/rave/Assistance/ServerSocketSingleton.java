package excal.rave.Assistance;

import java.net.ServerSocket;

/**
 * Created by hp on 1/12/2017.
 */

public class ServerSocketSingleton {
    private static ServerSocket socket = null;
    private static boolean isServerSocketCreated = false;

    public static void setSocket(ServerSocket s){
        socket = s;
    }

    public static ServerSocket getSocket(){
        return socket;
    }

    public static void setIsServerSocketCreated(boolean b){
        isServerSocketCreated = b;
    }

    public static boolean getIsServerSocketCreated(){
        return isServerSocketCreated;
    }
}
