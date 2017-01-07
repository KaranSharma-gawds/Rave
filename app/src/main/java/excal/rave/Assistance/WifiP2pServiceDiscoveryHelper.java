package excal.rave.Assistance;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import excal.rave.Activities.MainActivity;
import excal.rave.Activities.WiFiDirectServicesList;
import excal.rave.R;

/**
 * Created by Karan on 06-01-2017.
 */

public class WifiP2pServiceDiscoveryHelper {
    private WifiP2pManager.Channel channel;
    private final String SERVER_PORT = "";
    private final String TAG = "";
    private WifiP2pManager mManager;
    private AppCompatActivity activity;
    private final HashMap<String, String> buddies = new HashMap<String, String>();

    public WifiP2pServiceDiscoveryHelper(WifiP2pManager.Channel channel, WifiP2pManager mManager, AppCompatActivity activity) {
        this.channel = channel;
        this.mManager = mManager;
        this.activity = activity;
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
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
            }

            @Override
            public void onFailure(int arg0) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            }
        });
    }

    public void discoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
        /* Callback includes:
         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
         * record: TXT record dta as a map of key/value pairs.
         * device: The device running the advertised service.
         */

            public void onDnsSdTxtRecordAvailable(
                    String fullDomain, Map record, WifiP2pDevice device) {
                Log.d(TAG, "DnsSdTxtRecord available -" + record.toString());
                buddies.put(device.deviceAddress, record.get("buddyname").toString());
            }
        };
        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {

                // Update the device name with the human-friendly version from
                // the DnsTxtRecord, assuming one arrived.
                resourceType.deviceName = buddies
                        .containsKey(resourceType.deviceAddress) ? buddies
                        .get(resourceType.deviceAddress) : resourceType.deviceName;

                // Add to the custom adapter defined specifically for showing
                // wifi devices.
                WiFiDirectServicesList fragment = (WiFiDirectServicesList) activity.getFragmentManager().findFragmentById(R.id.frag_peerlist);
                WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment.getListAdapter());
                adapter.add(resourceType);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
            }
        };

        mManager.setDnsSdResponseListeners(channel, servListener, txtListener);
    }

}
