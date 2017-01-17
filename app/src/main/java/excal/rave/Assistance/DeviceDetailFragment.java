/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package excal.rave.Assistance;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import excal.rave.Assistance.DeviceListFragment.DeviceActionListener;
import excal.rave.Activities.Party;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;

import excal.rave.R;


/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {
    private static String Tag = "DeviceDetailFragment";
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private static View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    public static int port_no = 8980;
    ProgressDialog progressDialog = null;
    public static ArrayList<Socket> client_list = new ArrayList<>();
    public static Socket MyClientSocket = null;
    public static String MyIpAddress_client = null;
    public static Thread getClientsThread = null;
    public static Thread connectToServerThread = null;
    public static boolean isDeatilSet = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_detail, null);
        isDeatilSet = true;
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                if(Party.role.equals("SLAVE")){
                    config.groupOwnerIntent = 0;
                }else{
                    config.groupOwnerIntent = 15;
                }

                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                );
                ((DeviceActionListener) getActivity()).connect(config);
                /* Second way: could call createGroup() to make the current device as the group owner*/
            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DeviceActionListener) getActivity()).disconnect();
            }
        });

        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Allow user to pick an image from Gallery or other registered apps
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
            }
        });

        return mContentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();

        // Host is to send some data to all clients
        // SendToClientService
        if(requestCode == CHOOSE_FILE_RESULT_CODE){
            // for loop for all songs to be sent
            for(Socket socket : client_list){
                if(socket!=null && socket.isConnected()){
                    Intent clientIntent = new Intent(getActivity(), SendToClientService.class);
                    clientIntent.setAction(SendToClientService.ACTION_SEND_FILE);
                    clientIntent.putExtra(SendToClientService.EXTRAS_MESSAGE_TYPE, "musicFile");
                    clientIntent.putExtra(SendToClientService.EXTRAS_FILE_PATH, uri.toString());
                    SocketSingleton.setSocket(socket);
                    getActivity().startService(clientIntent);
                }else{
                    client_list.remove(socket);
                    Log.v(Tag,"--a socket removed");
                }
            }
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Updates the UI with device data
     * 
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        /*view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("\n"+device.toString());*/
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews(){
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);}

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = wifiP2pInfo;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        String text = getResources().getString(R.string.group_owner_text) + ((info.isGroupOwner == true) ? "yes" : "no");
        text += "\n" + getResources().getString(R.string.party_owner_text) + ((Party.role.equals("MASTER"))? "yes" : "no");
        view.setText(text);

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress()+"\n");

        // This is creating a groupOwner based on a device performance...
        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server socket.

        if(Party.role.equals("MASTER")){
            // make it send data as he is hosting the party
            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources().getString(R.string.host_text));
            // master sends data.. it can remember all the sockets for repetitive sending

            //accepting client requests
            if(!ServerSocketSingleton.getIsServerSocketCreated()){
                //to check that Server is created only once
                GetClients getClients = new GetClients();
                getClientsThread = new Thread(getClients);
                getClientsThread.start();
                ServerSocketSingleton.setIsServerSocketCreated(true);
            }

        } else{
            /*
            //make it receive data as it wants to join the party
            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
            */



            //create socket to server
            ClientSocket clientSocket = new ClientSocket(info.groupOwnerAddress.getHostAddress(),Party.thisActivity);
            connectToServerThread = new Thread(clientSocket);
            connectToServerThread.start();

        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    public static boolean copyFile(InputStream inputStream, OutputStream out, long fileSize) {
        byte buf[] = new byte[1024];
        int bytesRead = 0, len;

        Log.v(Tag,"--copying "+fileSize);

        try {
            Calendar c;
            long t=0;
            int x=0;
            while (/*bytesRead < fileSize && */(len = inputStream.read(buf)) != -1 ) {
                if(x==0){
                    c= Calendar.getInstance();
                    x=1;
                    t=c.getTimeInMillis();
                }
                bytesRead+=len;
                out.write(buf, 0, len);
                Log.v(Tag,"--"+len + "  file:"+fileSize + "  read:" + bytesRead);
            }
            Calendar c1= Calendar.getInstance();
            long t1 = c1.getTimeInMillis();

            Log.v(Tag,"--done "+(t1-t));
            Log.v(Tag,"--bytesRead: "+bytesRead + " fileSize: "+fileSize);

//            out.close();
//            inputStream.close();

            if(Party.role.equals("MASTER")){
                inputStream.close();
                out.flush();
            }
            if(Party.role.equals("SLAVE")) {
                out.close();
            }
        } catch (IOException e) {
            Log.v(Tag, e.toString());
            return false;
        }
        Log.v(Tag, "copyFile: "+((Party.role.equals("MASTER"))? "file sent to client" : "file received from server") );
        return true;
    }


}
