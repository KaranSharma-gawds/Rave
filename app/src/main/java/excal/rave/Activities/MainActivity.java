package excal.rave.Activities;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    Button getMusic;
    private String TAG = "";

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
        getMusic = (Button) findViewById(R.id.button);
        getMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bring_music();
            }
        });
        /*Intent fromMain2Activity = getIntent();
        role = fromMain2Activity.getStringExtra("ROLE");
        registerReceiver(new ReceiverForWifi(mManager,mChannel,this),intentFilter);
*/


    }
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

    //To bring music from default music app
    public void bring_music()
    {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Uri tempPlaylistURI = MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI;

        // In the next line 'this' points to current Activity.
        // If you want to use the same code in other java file then activity,
        // then use an instance of any activity in place of 'this'.

        Cursor playListCursor= this.managedQuery(tempPlaylistURI, proj, null,null,null);

        if(playListCursor == null){
            Toast.makeText(this,"Not having any Playlist on phone ",Toast.LENGTH_LONG).show();
            return;//don't have list on phone
        }
        System.gc();
        String playListName = null;
        Toast.makeText(this,"CREATING AND DISPLAYING LIST OF ALL CREATED PLAYLIST ",Toast.LENGTH_LONG).show();

        for(int i = 0; i <playListCursor.getCount() ; i++)
        {
            playListCursor.moveToNext();
            playListName = playListCursor.getString(playListCursor.getColumnIndex("name"));
            Log.d(TAG,"> " + i + "  : " + playListName );
        }

        if(playListCursor != null)
            playListCursor.close();

    }
}

