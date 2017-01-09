package excal.rave.Assistance;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paragi on 08-01-2017.
 */

public class Hotspot {
    WifiP2pManager mManager;
    WifiP2pDnsSdServiceRequest serviceRequest;
    WifiP2pManager.Channel channel;
    private final int SERVER_PORT = 0;
    private final String TAG = "";
    Context mContext;

    public void turnOnHotspot() {


    }

    public Hotspot(WifiP2pManager.Channel channel, Context context,WifiP2pManager manager) {
        this.channel = channel;
        mContext = context;
        mManager = manager;
    }
    public void startRegistration() {
        //  Create a string map containing information about your service.
        Map record = new HashMap();
        record.put("listenport", String.valueOf(SERVER_PORT));
        record.put("buddyname", "John Doe" + (int) (Math.random() * 1000));
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
      //  turnOnHotspot(); //AAAAASSSSSKKKKKK!!!!!
        mManager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mContext,"successfully added local service!!",Toast.LENGTH_SHORT).show();
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
            }

            @Override
            public void onFailure(int arg0) {
                Toast.makeText(mContext,"failed!!",Toast.LENGTH_SHORT).show();
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            }
        });
    }
}
