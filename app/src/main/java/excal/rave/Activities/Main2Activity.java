package excal.rave.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import excal.rave.Assistance.ClientSocket;
import excal.rave.Assistance.DeviceListFragment;
import excal.rave.Assistance.ServerSocketSingleton;
import excal.rave.R;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "Main2Activity";
    Button createParty, joinParty;
    private static int partyCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        createParty = (Button) findViewById(R.id.create_party);
        joinParty = (Button) findViewById(R.id.join_party);
        final Intent createGroupIntent = new Intent(Main2Activity.this,Tab.class);
        createParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroupIntent.putExtra("ROLE","MASTER");
                reseting();
                startActivity(createGroupIntent);
            }
        });
        joinParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroupIntent.putExtra("ROLE","SLAVE");
                if(Tab.manager != null){
                    ((DeviceListFragment.DeviceActionListener) Tab.thisActivity).disconnect();
                }
                if(ClientSocket.socket!=null){
                    try {
                        ClientSocket.socket.close();
                        ClientSocket.socket = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                startActivity(createGroupIntent);
            }
        });
        Log.v(TAG,"--"+ (ServerSocketSingleton.getIsServerSocketCreated()?"ServerSocket created":"ServerSocket not created"));
    }

    private void reseting() {
        partyCount++;
        if(partyCount>1)
            Party.closeSockets();
    }

}
