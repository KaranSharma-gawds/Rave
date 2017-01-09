package excal.rave.Activities;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.widget.Toast;

import excal.rave.Assistance.BroadcastReceiverforWifi;
import excal.rave.Assistance.Hotspot;
import excal.rave.Assistance.WifiP2pServiceDiscoveryHelper;
import excal.rave.R;

public class MainActivity extends AppCompatActivity {
    private final IntentFilter intentFilter = new IntentFilter();
    Channel mChannel;
    WifiP2pManager mManager;
    String role;
    boolean isWifiEnabled;
    BroadcastReceiverforWifi receiver;
    public void setIsWifiP2pEnabled(boolean isWifiEnabled) {
        this.isWifiEnabled = isWifiEnabled;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_WIFI_STATE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                        1);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                        2);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        3);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                        1);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                        2);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        3);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }*/
        seekPermission();
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        Toast.makeText(this,"Permission "+permissionCheck,Toast.LENGTH_LONG).show();

        /*Intent fromMain2Activity = getIntent();
        role = fromMain2Activity.getStringExtra("ROLE");
        registerReceiver(new ReceiverForWifi(mManager,mChannel,this),intentFilter);
*/


    }
    /*protected void onResume() {
        super.onResume();
        //if(role.equals("Master")){

        *//*} else if(role.equals("SLAVE")){
            registerReceiver(new ReceiverForWifi(mManager,mChannel,this),intentFilter);
        }*//*
    }*/
    @Override
    public void onResume() {
        super.onResume();
        receiver = new BroadcastReceiverforWifi(mManager, mChannel, this);
        registerReceiver(receiver, intentFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Hotspot hotspot = new Hotspot(mChannel,this, mManager);
                    //hotspot.turnOnHotspot();
                    hotspot.startRegistration();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 2: {}
            case 3: {}

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void seekPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                1);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                2);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.INTERNET},
                3);
        Toast.makeText(this, "seek called", Toast.LENGTH_LONG).show();
    }
}

