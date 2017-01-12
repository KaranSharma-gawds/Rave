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

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import excal.rave.Assistance.DeviceListFragment.DeviceActionListener;
import excal.rave.Activities.Party;
import excal.rave.Assistance.IpChecker;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    private String myIP;
    public static int port_no = 8980;
    private IpChecker myIpCheckerThread = null;
    public String host_address;
    ProgressDialog progressDialog = null;
    public static ArrayList<Socket> client_list = new ArrayList<>();
    public static Socket MyClientSocket = null;
    public static String MyIpAddress_client = null;
    public static Thread getClientsThread = null;
    public static Thread connectToServerThread = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_detail, null);
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
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
            }
        });

        return mContentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();

       /*
        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + uri);
        Log.d(Tag, "Intent----------- " + uri);
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_HOST_PORT, port_no);
        getActivity().startService(serviceIntent);
        //multiple receivers will be there, so all the party joiners must get data from here..
        */

        // Host is to send some data to all clients
        // SendToClientService
        if(requestCode == CHOOSE_FILE_RESULT_CODE){
            for(Socket socket : client_list){
                if(socket!=null && socket.isConnected()){
                    OutputStream ostream = null;
                    try {
                        ostream = socket.getOutputStream();
                        DataOutputStream dout=new DataOutputStream(ostream);
                        dout.writeUTF("musicFile");
                        dout.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent clientIntent = new Intent(getActivity(), SendToClientService.class);
                    clientIntent.setAction(SendToClientService.ACTION_SEND_FILE);
                    clientIntent.putExtra(SendToClientService.EXTRAS_FILE_PATH, uri.toString());
                    SocketSingleton.setSocket(socket);
                    getActivity().startService(clientIntent);
                }else{
                    client_list.remove(socket);
                    Log.v(Tag,"--a socket removed");
                }
            }
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
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("\n"+device.toString());
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


    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {
        //to receive

        private Context context;
        private TextView statusText;

        /**
         * @param context
         * @param statusText
         */
        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(Tag, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d(Tag, "Server: connection done");
                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".jpg");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();

                Log.d(Tag, "server: copying files " + f.toString());
                InputStream inputstream = client.getInputStream();
                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
                //need to accept data even after this... do something
                return f.getAbsolutePath();
            } catch (IOException e) {
                Log.e(Tag, e.getMessage());
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                statusText.setText("File copied - " + result);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);
            }

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }

    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }

            Log.v(Tag,"--done");
            if(Party.role.equals("MASTER"))
                inputStream.close();
            if(Party.role.equals("SLAVE"))
                out.close();
        } catch (IOException e) {
            Log.d(Tag, e.toString());
            return false;
        }
//            out.close();
//            inputStream.close();
        Log.d(Tag, "copyFile: "+((Party.role.equals("MASTER"))? "file sent to client" : "file received from server") );
        return true;
    }


}
