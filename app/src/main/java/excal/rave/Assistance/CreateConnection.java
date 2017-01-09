package excal.rave.Assistance;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.util.List;

import excal.rave.Activities.MainActivity;

/**
 * Created by Karan on 02-01-2017.
 */

public class CreateConnection {
    private static WifiP2pManager manager;
    private static WifiP2pManager.Channel channel;
    private static Context context;

    public static void init(Context con, WifiP2pManager m, WifiP2pManager.Channel c) {
        manager=m;
        channel=c;
        context=con;
    }

    public static void selectDevice(WifiP2pDevice device) {
//        WifiP2pDevice device=peers.get(i);
        createConnection(device);
    }

    public static void createConnection(WifiP2pDevice device){
        WifiP2pConfig config=new WifiP2pConfig();
        config.deviceAddress=device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            //connect->createGroup : to make group owner for devices w/o WiFi-Direct
            // group owner is automatically selected in connect
            @Override
            public void onSuccess() {
                /*--Connection made.. Send/Receive data--*/
                // maybe use Donor-Receiver
//                Toast.makeText(, "connection success", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int i) {
                //no connection can be made to device device.deviceName
            }
        });
    }

    public void sendCredentials() {

    }
}
