package excal.rave.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;

import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import excal.rave.Assistance.DeviceDetailFragment;
import excal.rave.Assistance.DeviceListFragment;
import excal.rave.Assistance.DeviceListFragment.DeviceActionListener;
import excal.rave.Assistance.ReceiverForWifi;
import excal.rave.Assistance.SendToClientService;
import excal.rave.Assistance.SocketSingleton;
import excal.rave.R;

import static android.os.Looper.getMainLooper;
import static excal.rave.Assistance.DeviceDetailFragment.client_list;


public class Tab extends AppCompatActivity implements ChannelListener, DeviceActionListener {
    private static final int CHOOSE_FILE_RESULT_CODE = 20;
    private static String TAG = "Tab";
    Party party = null;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static WifiP2pManager manager;
    private static boolean isWifiP2pEnabled = false;
    private Channel channel;
    private boolean retryChannel = false;
    public static String role=null;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver receiver = null;
    public static Activity thisActivity;
    public static Context thisContext;

    public static DeviceListFragment listFragment = null;
    public static DeviceDetailFragment detailFragment = null;

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        thisActivity = Tab.this;
        thisContext = getApplicationContext();


//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //party.discoverDevices();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent fromMain2Activity = getIntent();
        role = fromMain2Activity.getStringExtra("ROLE");
//        party = new Party(Tab.this,getApplicationContext(),role);
        setup();
//        party.Resume();


        receiver = new ReceiverForWifi(manager, channel, thisActivity, this);
        thisActivity.registerReceiver(receiver, intentFilter);
    }

    void setup(){
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) thisActivity.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(thisContext, getMainLooper(), null);

        deletePersistentGroups();
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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
//        party.Pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Party.Destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.atn_direct_enable:
//                party.checkWifiEnable();
                if (manager != null && channel != null) {

                    // Since this is the system wireless settings activity, it's
                    // not going to send us a result. We will be notified by
                    // WiFiDeviceBroadcastReceiver instead.

                    thisActivity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                } else {
                    Log.e(TAG, "channel or manager is null");
                }
                return true;

            case R.id.atn_direct_discover:
//                party.discoverDevices();
                if (!isWifiP2pEnabled) {
                    Toast.makeText(thisContext, R.string.p2p_off_warning,Toast.LENGTH_SHORT).show();
                    return true;
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
                return true;

            case R.id.noOfClients:
                if(role.equals("MASTER"))
                    Toast.makeText(this, "No of clients connected: "+ client_list.size(), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Beware! You are only a client..", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.tabs:
                Intent intent = new Intent(this,Tab.class);
                startActivity(intent);
                return true;

            case R.id.sending:
                if(role.equals("MASTER")){
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("audio/*");
                    startActivityForResult(i, CHOOSE_FILE_RESULT_CODE);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();

        // Host is to send some data to all clients
        // SendToClientService
        if(requestCode == CHOOSE_FILE_RESULT_CODE){
            // for loop for all songs to be sent
            for(Socket socket : client_list){
                if(socket!=null && socket.isConnected() && !socket.isClosed()){
                    Intent clientIntent = new Intent(thisActivity, SendToClientService.class);
                    clientIntent.setAction(SendToClientService.ACTION_SEND_FILE);
                    clientIntent.putExtra(SendToClientService.EXTRAS_MESSAGE_TYPE, "musicFile");
                    clientIntent.putExtra(SendToClientService.EXTRAS_FILE_PATH, uri.toString());
                    SocketSingleton.setSocket(socket);
                    startService(clientIntent);
                }else{
                    client_list.remove(socket);
                    Log.v(TAG,"--a socket removed");
                }
            }

            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }else{
            Toast.makeText(thisContext, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        listFragment = new DeviceListFragment();
        adapter.addFragment(listFragment, "PEERS");
/*        if(Party.role.equals("MASTER")){
            adapter.addFragment(new TwoFragment(), "ALL SONG");
        }*/
//        adapter.addFragment(new ThreeFragment(), "SHARED SONGS");
        adapter.addFragment(new DeviceListFragment(), "Extra");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
/*        DeviceDetailFragment fragment = (DeviceDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_detail);*/
        if(detailFragment==null){
            DeviceDetailFragment fragment = new DeviceDetailFragment();
            Tab.detailFragment = fragment;
        }
//        detailFragment.showDetails(device);
        detailFragment.connectDevice(device);
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
//        final DeviceDetailFragment fragment = (DeviceDetailFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.frag_detail);
//        final DeviceDetailFragment fragment = Tab.detailFragment;
//        fragment.resetViews();
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
            }

            @Override
            public void onSuccess() {
                Log.v(TAG,"--disconnected");
//                fragment.getView().setVisibility(View.GONE);
            }
        });
    }

}

