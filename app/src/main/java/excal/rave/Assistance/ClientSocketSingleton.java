package excal.rave.Assistance;

import android.app.Activity;

/**
 * Created by hp on 1/18/2017.
 */

public class ClientSocketSingleton {
    private static ClientSocket clientSocket = null;
    private static boolean isClientCreated = false;

    public static void setClientSocket(ClientSocket s){
        clientSocket = s;
    }

    public static ClientSocket getClientSocket(){
        return clientSocket;
    }

    public static void setIsClientCreated(boolean b){
        isClientCreated = b;
    }

    public static boolean getIsClientCreated(){
        return isClientCreated;
    }

    public static void setValues(String hostAddress, Activity thisActivity) {
        clientSocket.set(hostAddress, thisActivity);
    }
}
