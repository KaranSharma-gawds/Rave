package excal.rave.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import excal.rave.Assistance.ReceiverForWifi;
import excal.rave.R;
import excal.rave.Assistance.CreateConnection;

public class MainActivity extends AppCompatActivity {
    private final IntentFilter intentFilter = new IntentFilter();
    Channel mChannel;
    WifiP2pManager mManager;
    String role;
    public static List<WifiP2pDevice> peers;
    public static ArrayAdapter<WifiP2pDevice> adapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        Intent fromMain2Activity = getIntent();
        role = fromMain2Activity.getStringExtra("ROLE");

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Peers Discovered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(MainActivity.this, "Discovery failed", Toast.LENGTH_SHORT).show();
            }
        });

        //listView to display list of all available wifi devices
        //TODO: add adapter here.. update in receiver
        adapter= new ArrayAdapter<WifiP2pDevice>(MainActivity.this,android.R.layout.simple_expandable_list_item_1,peers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CreateConnection.init(getApplicationContext(),mManager,mChannel);
                CreateConnection.selectDevice(peers.get(i));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(role.equals("Master")){
            registerReceiver(new ReceiverForWifi(mManager,mChannel,new Donor()),intentFilter);
        } else if(role.equals("SLAVE")){
            registerReceiver(new ReceiverForWifi(mManager,mChannel,new Receiver()),intentFilter);
        }
    }
}