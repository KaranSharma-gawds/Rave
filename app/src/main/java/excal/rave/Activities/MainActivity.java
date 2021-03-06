package excal.rave.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.p2p.WifiP2pManager.Channel;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import excal.rave.Assistance.Donor;
import excal.rave.Assistance.ReceiverForWifi;
import excal.rave.Assistance.Reciever;
import excal.rave.R;

public class MainActivity extends AppCompatActivity {
    private final IntentFilter intentFilter = new IntentFilter();
    Channel mChannel;
    WifiP2pManager mManager;
    String role;
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(role.equals("Master")){
            registerReceiver(new ReceiverForWifi(mManager,mChannel,new Donor()),intentFilter);
        } else if(role.equals("SLAVE")){
            registerReceiver(new ReceiverForWifi(mManager,mChannel,new Reciever()),intentFilter);
        }
    }
}