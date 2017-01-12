package excal.rave.Assistance;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by hp on 1/11/2017.
 */

public class IpChecker extends Thread {
    private int timeout = 5000;
    String group_owner;
    int port_no;
    private String myAddress;

    public IpChecker(String hostAddress, int port) {
        group_owner = hostAddress;
        port_no = port;
    }

    @Override
    public void start() {

        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(group_owner,port_no), timeout);
            myAddress = socket.getLocalAddress().getHostAddress();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMyIp(){
        return myAddress;
    }


    /*usage code
            if(info.groupFormed && info.isGroupOwner){
                myIP = info.groupOwnerAddress.getHostAddress();
            }else if(info.groupFormed){
                myIpCheckerThread = new IpChecker(info.groupOwnerAddress.getHostAddress(), port_no);
                myIpCheckerThread.start();
                myIP = myIpCheckerThread.getMyIp();
            }else{
                Log.v(Party.TAG,"No Wifi-Direct Group Formed");
            }
     */
}
