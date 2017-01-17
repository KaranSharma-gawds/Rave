package excal.rave.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import excal.rave.Assistance.DeviceDetailFragment;
import excal.rave.Assistance.DeviceListFragment;
import excal.rave.Assistance.DeviceListFragment.DeviceActionListener;
import excal.rave.Assistance.ReceiverForWifi;
import excal.rave.Assistance.ServerSocketSingleton;
import excal.rave.R;

import static android.os.Looper.getMainLooper;

/**
 * Created by Karan on 02-01-2017.
 */

public class Party /* extends AppCompatActivity */ implements ChannelListener, DeviceActionListener {
    long Uid;
    int pic;
    int currentSong;
    int nextSong;

    public static final String TAG = "PartyActivity";
    private WifiP2pManager manager;
    private static boolean isWifiP2pEnabled = false;
    private Channel channel;
    private boolean retryChannel = false;
    public static String role=null;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver receiver = null;
    public static Activity thisActivity;
    public static FragmentActivity fragActivity;
    public static Context thisContext;
    WifiP2pGroup group;

    public Party(Activity a,Context c, String r){
        thisActivity = a;
        thisContext = c;
        role = r;
        fragActivity = (FragmentActivity) thisActivity;
    }

    boolean isWifiEnabled;
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

/*

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        thisContext=getApplicationContext();
        thisActivity=Party.this;
        Intent fromMain2Activity = getIntent();
        role = fromMain2Activity.getStringExtra("ROLE");
//        resetData();

        // add necessary intent values to be matched.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        */
/* deletePersistentGroups to delete the groups created so far.. it deletes all the groups though.
        * we do this coz 2 devices when trying to connect again join the old group and the owner is the same..
        *
        * "The P2P Group Owner of a Persistent P2P Group is determined when the P2P Group is formed
         * and is the same P2P Device in subsequent P2P Group sessions."
        * This line from the p2p specification says that you cant' change the group owner.
        *
        * *//*

        deletePersistentGroups();
    }
*/

    void setup(){
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) thisActivity.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(thisContext, getMainLooper(), null);

        deletePersistentGroups();
    }

    /** register the BroadcastReceiver with the intent values to be matched */
/*    @Override
    public void onResume() {
    }*/

    public void Resume(){
        receiver = new ReceiverForWifi(manager, channel, thisActivity, this);
        thisActivity.registerReceiver(receiver, intentFilter);
    }

/*    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }*/

    public void Pause(){
        thisActivity.unregisterReceiver(receiver);
    }

/*    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(DeviceDetailFragment.getClientsThread!=null) {
            DeviceDetailFragment.getClientsThread.interrupt();
        }
        if(DeviceDetailFragment.connectToServerThread!=null){
            DeviceDetailFragment.connectToServerThread.interrupt();
        }
        if(role.equals("MASTER"))
            closeSockets();
    }*/

    public void Destroy() {
        if(DeviceDetailFragment.getClientsThread!=null) {
            DeviceDetailFragment.getClientsThread.interrupt();
        }
        if(DeviceDetailFragment.connectToServerThread!=null){
            DeviceDetailFragment.connectToServerThread.interrupt();
        }
        if(role.equals("MASTER"))
            closeSockets();
    }



    public static void closeSockets() {
        ServerSocket s = ServerSocketSingleton.getSocket();
        if(s!=null && !s.isClosed()){
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ServerSocketSingleton.setIsServerSocketCreated(false);

        try{
            ArrayList<Socket> list = DeviceDetailFragment.client_list;
            for(Socket socket : list) {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            list.clear();
        }catch (Exception e){
            Log.d(Party.TAG,e.toString());
        }

    }

    private void deletePersistentGroups(){
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        methods[i].invoke(manager, channel, netid, null);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void resetData() {
        if(DeviceListFragment.isListSet){
//            DeviceListFragment fragmentList = (DeviceListFragment) (fragActivity.getSupportFragmentManager())
//                    .findFragmentById(R.id.frag_list);
            if (Tab.listFragment != null) {
                Tab.listFragment.clearPeers();
            }
        }
        if(DeviceDetailFragment.isDeatilSet){
//            DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) fragActivity.getSupportFragmentManager()
//                    .findFragmentById(R.id.frag_detail);
            if (Tab.listFragment != null) {
                Tab.detailFragment.resetViews();
            }
        }
    }


    /**--USE FOR STARTING DISCOVERY..not necessary that we use a menu..**/
/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.atn_direct_enable:
                if (manager != null && channel != null) {

                    // Since this is the system wireless settings activity, it's
                    // not going to send us a result. We will be notified by
                    // WiFiDeviceBroadcastReceiver instead.

                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                } else {
                    Log.e(TAG, "channel or manager is null");
                }
                return true;

            case R.id.atn_direct_discover:
                discoverDevices();
                return true;
            
            case R.id.noOfClients:
                if(role.equals("MASTER"))
                    Toast.makeText(thisContext, "No of clients connected: "+DeviceDetailFragment.client_list.size(), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(thisContext, "Beware! You are only a client..", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.tabs:
                Intent intent = new Intent(thisActivity,Tab.class);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    public void checkWifiEnable(){
        if (manager != null && channel != null) {

            // Since this is the system wireless settings activity, it's
            // not going to send us a result. We will be notified by
            // WiFiDeviceBroadcastReceiver instead.

            thisActivity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        } else {
            Log.e(TAG, "channel or manager is null");
        }
    }

    public void discoverDevices() {
        if (!isWifiP2pEnabled) {
            Toast.makeText(thisContext, R.string.p2p_off_warning,Toast.LENGTH_SHORT).show();
            return; // true;
        }
         /*DeviceListFragment fragment = (DeviceListFragment) fragActivity.getSupportFragmentManager()
                .findFragmentById(R.id.frag_list);  //first DeviceList reference
        fragment.onInitiateDiscovery();*/
       Tab.listFragment.onInitiateDiscovery();

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(thisContext, "Discovery Initiated",Toast.LENGTH_SHORT).show();
                //Broadcast Action WIFI_P2P_PEERS_CHANGED_ACTION is initiated
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(thisContext, "Discovery Failed : " + reasonCode,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(thisContext, R.string.channel_lost, Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(thisContext, getMainLooper(), this);
        } else {
            Toast.makeText(thisContext,R.string.channel_lost_permanently,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void showDetails(WifiP2pDevice device) {
//        DeviceDetailFragment fragment = (DeviceDetailFragment) fragActivity.getSupportFragmentManager()
//                .findFragmentById(R.id.frag_detail);
        Tab.detailFragment.showDetails(device);

    }

    @Override
    public void cancelDisconnect() {
        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
//            final DeviceListFragment fragment = (DeviceListFragment) fragActivity.getSupportFragmentManager()
//                    .findFragmentById(R.id.frag_list);
            DeviceListFragment fragment = Tab.listFragment;

            if (fragment.getDevice() == null || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                                        || fragment.getDevice().status == WifiP2pDevice.INVITED) {
                manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(thisContext, "Aborting connection", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(thisContext,"Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // ReceiverForWifi will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(thisContext, "Connect failed. Retry.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
//        final DeviceDetailFragment fragment = (DeviceDetailFragment) fragActivity.getSupportFragmentManager()
//                .findFragmentById(R.id.frag_detail);
        final DeviceDetailFragment fragment = Tab.detailFragment;
        fragment.resetViews();
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
            }

            @Override
            public void onSuccess() {
                fragment.getView().setVisibility(View.GONE);
            }
        });
    }

}
