package excal.rave.Assistance;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paragi on 08-01-2017.
 */

public class HotspotReceivers {
    WifiP2pManager mManager;
    WifiP2pDnsSdServiceRequest serviceRequest;
    WifiP2pManager.Channel channel;
    private final String TAG = "";
    Context mContext;
    public HotspotReceivers(WifiP2pManager.Channel channel, Context context) {
        this.channel = channel;
        mContext = context;
    }
    public void turnOnWifi() {

    }
    final HashMap<String, String> buddies = new HashMap<String, String>();
    public void discoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
        /* Callback includes:
         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
         * record: TXT record dta as a map of key/value pairs.
         * device: The device running the advertised service.
         */

            public void onDnsSdTxtRecordAvailable(String fullDomain, Map record, WifiP2pDevice device) {
                Log.d(TAG, "DnsSdTxtRecord available -" + record.toString());
                buddies.put(device.deviceAddress, record.get("buddyname").toString());

            }

        };
        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice resourceType) {

                // Update the device name with the human-friendly version from
                // the DnsTxtRecord, assuming one arrived.
                resourceType.deviceName = buddies.containsKey(resourceType.deviceAddress)
                        ? buddies.get(resourceType.deviceAddress) : resourceType.deviceName;

                // Add to the custom adapter defined specifically for showing
                // wifi devices.
                Toast.makeText(mContext, "this code id running", Toast.LENGTH_SHORT).show();
                    /*WiFiDirectServicesList fragment = (WiFiDirectServicesList)
                            getFragmentManager().findFragmentById(R.id.frag_peerlist);
                    WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment.getListAdapter());
                    adapter.add(resourceType);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "onBonjourServiceAvailable " + instanceName);*/
            }
        };

        mManager.setDnsSdResponseListeners(channel, servListener, txtListener);
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(channel,
                serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        // Success!
                    }

                    @Override
                    public void onFailure(int code) {
                        // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    }
                });
        mManager.discoverServices(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Success!
            }

            @Override
            public void onFailure(int code) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                if (code == WifiP2pManager.P2P_UNSUPPORTED) {
                    Log.d(TAG, "P2P isn't supported on this device.");
                }
                   /* else if()
                    ...*/
            }
        });
    }

}
