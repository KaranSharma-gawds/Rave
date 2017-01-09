package excal.rave.Assistance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import excal.rave.Activities.MainActivity;
import excal.rave.R;

/**
 * Created by Karan on 02-01-2017.
 */

public class BroadcastReceiverforWifi extends BroadcastReceiver {
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    AppCompatActivity activity;

    public BroadcastReceiverforWifi(WifiP2pManager manager, WifiP2pManager.Channel channel, AppCompatActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                ((MainActivity) activity).setIsWifiP2pEnabled(true);
                Toast.makeText(context,"p2P IS ON",Toast.LENGTH_LONG).show();
            } else {
                ((MainActivity) activity).setIsWifiP2pEnabled(false);
                Toast.makeText(context,"p2P IS Off",Toast.LENGTH_LONG).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                manager.requestPeers(channel, peerListListener);
            }
            //do something about the changed peers e.g.:Log.d(WiFiDirectActivity.TAG, "P2P peers changed");


        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            /*DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager().findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));*/
        }
    }
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            List<WifiP2pDevice> refreshedPeers = (List<WifiP2pDevice>) wifiP2pDeviceList.getDeviceList();
            if(!refreshedPeers.equals(peers)){
                peers.clear();
                peers.addAll(refreshedPeers);
                /*TODO: do something about the new peers*/

            }

            if(peers.size() == 0) {
                Toast.makeText(activity.getApplicationContext(), "No devices found", Toast.LENGTH_SHORT).show();
            }
        }
    };
}


